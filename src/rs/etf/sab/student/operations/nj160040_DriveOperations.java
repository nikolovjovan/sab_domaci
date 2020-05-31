package rs.etf.sab.student.operations;

import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.student.data.Address;
import rs.etf.sab.student.data.Package;
import rs.etf.sab.student.data.Vehicle;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_DriveOperations implements DriveOperation {

    private static nj160040_DriveOperations instance;

    public static nj160040_DriveOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_DriveOperations();
        }
        return instance;
    }

    private BigDecimal pickUpPackages(BigDecimal remainingCapacity, List<Package> packages) {
        int count = 0, i = 0;
        while (count < packages.size()) {
            Package p = packages.get(i);
            if (remainingCapacity.compareTo(p.getWeight()) > 0) {
                remainingCapacity = remainingCapacity.subtract(p.getWeight());
                count++;
                i++;
            } else {
                packages.remove(i);
            }
        }
        return remainingCapacity;
    }

    private double getDistance(Address a1, Address a2) {
        double deltaX = a1.getxCord() - a2.getxCord();
        double deltaY = a1.getyCord() - a2.getyCord();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean planingDrive(String courierUserName) {
        // Check if courier is already driving
        if (CommonOperations.getCourierStatus(courierUserName) == 1) {
            System.out.println("Courier with user name '" + courierUserName + "' is already driving!");
            return false;
        }

        // Get courier city (starting city)
        int idAddressCourier = CommonOperations.getUserAddress(courierUserName);
        if (idAddressCourier == -1) return false;
        int idCity = CommonOperations.getAddressCity(idAddressCourier);
        if (idCity == -1) return false;

        // Get stockroom in courier's city
        int idStockroom = CommonOperations.getStockroomInCity(idCity);
        if (idStockroom == -1) return false;

        // Get available vehicles in stockroom
        List<Vehicle> vehicles = CommonOperations.getVehiclesInStockroom(idStockroom);
        if (vehicles.isEmpty()) {
            System.out.println("There is no vehicle available for courier with user name '" + courierUserName + "'!");
            return false;
        }

        // Get first available vehicle in stockroom
        // TODO: Check which vehicle should the courier pick from the stockroom (largest capacity, smallest fuel
        //       consumption and therefore price)
        Vehicle v = vehicles.get(0);

        // Get packages to deliver
        List<Package> nonPickedUpPackages = CommonOperations.getNonPickedUpPackagesInCity(idCity);
        List<Package> packagesInStockroom = CommonOperations.getPackagesInStockroom(idStockroom);

        if (nonPickedUpPackages.isEmpty()) {
            if (packagesInStockroom.isEmpty()) {
                System.out.println("No packages for delivery found in city with primary key: " + idCity + '!');
                return false;
            } else {
                System.out.println("No non-picked-up packages found in city with primary key: " + idCity + '.');
            }
        } else if (packagesInStockroom.isEmpty()) {
            System.out.println("No packages found in stockroom with primary key: " + idStockroom + '.');
        }

        pickUpPackages(pickUpPackages(v.getCapacity(), nonPickedUpPackages), packagesInStockroom);

        if (nonPickedUpPackages.isEmpty() && packagesInStockroom.isEmpty()) {
            System.out.println("No packages can be delivered at this time because of capacity constraints!");
            return false;
        }

        // Update courier status
        if (!CommonOperations.setCourierStatus(courierUserName, 1)) {
            System.out.println("Failed to change courier status to driving! Aborting...");
            return false;
        }

        // Take the vehicle
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDriving (userName, licensePlateNumber, " +
                "currentStopNumber, distanceTraveled, remainingCapacity) values (?, ?, ?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            stmt.setString(2, v.getLicensePlateNumber());
            stmt.setInt(3, 0); // currentStopNumber = 0 (at start)
            stmt.setBigDecimal(4, BigDecimal.valueOf(0));
            stmt.setBigDecimal(5, v.getCapacity()); // initially vehicle is empty
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update 'isDriving' table! Aborting...");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Set packages to be delivered
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDelivering (userName, idPackage) values (?, ?)")) {
            stmt.setString(1, courierUserName);
            for (Package p : nonPickedUpPackages) {
                stmt.setInt(2, p.getIdPackage());
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add package with primary key: " + p.getIdPackage() + " to delivery list!");
                    return false;
                }
            }
            for (Package p : packagesInStockroom) {
                stmt.setInt(2, p.getIdPackage());
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add package with primary key: " + p.getIdPackage() + " to delivery list!");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        List<Address> addresses = new ArrayList<>();
        for (Package p : nonPickedUpPackages) {
            int idAddress = p.getIdAddressFrom();
            boolean found = false;
            for (Address a : addresses) {
                if (a.getIdAddress() == idAddress) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Address a = CommonOperations.getAddressById(idAddress);
                if (a == null) {
                    System.out.println("Failed to get address with primary key: " + idAddress + '!');
                } else {
                    addresses.add(a);
                }
            }
        }

        // Generate stops...
        List<Address> stops = new ArrayList<>();

        // First stop = stockroom address
        Address previousStop = CommonOperations.getAddressById(CommonOperations.getStockroomAddress(idStockroom));
        stops.add(previousStop);

        // Add stops in starting city
        while (!addresses.isEmpty()) {
            Address nextStop = null;
            double distance, minDistance = Double.MAX_VALUE;
            for (Address a : addresses) {
                distance = getDistance(a, previousStop);
                if (distance < minDistance) {
                    nextStop = a;
                    minDistance = distance;
                }
            }
            if (nextStop != null) {
                stops.add(nextStop);
                previousStop = nextStop;
                addresses.remove(nextStop);
            }
        }

        if (!packagesInStockroom.isEmpty()) {
            // Go back to stockroom if there are more packages to be picked up for delivery
            previousStop = stops.get(0);
            stops.add(previousStop);
        }

        // TODO: Go to cities and etc...

        // Set addresses to be visited
        try (PreparedStatement stmt = conn.prepareStatement("insert into Stop (userName, idAddress, stopNumber) values (?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            for (int i = 0; i < addresses.size(); i++) {
                stmt.setInt(2, addresses.get(i).getIdAddress());
                stmt.setInt(3, i); // stop number
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add address with primary key: " + addresses.get(i).getIdAddress() + " to stops list!");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    @Override
    public int nextStop(String courierUserName) {
        return 0;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUserName) {
        return null;
    }

    private nj160040_DriveOperations() {}
}