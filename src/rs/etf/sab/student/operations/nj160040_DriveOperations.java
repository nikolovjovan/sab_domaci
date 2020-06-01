package rs.etf.sab.student.operations;

import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.student.data.Address;
import rs.etf.sab.student.data.Package;
import rs.etf.sab.student.data.Vehicle;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
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

    private boolean takeVehicle(String courierUserName, Vehicle vehicle) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDriving (userName, licensePlateNumber, " +
                "currentStopNumber, distanceTraveled, remainingCapacity) values (?, ?, ?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            stmt.setString(2, vehicle.getLicensePlateNumber());
            stmt.setInt(3, 0); // currentStopNumber = 0 (at start)
            stmt.setBigDecimal(4, BigDecimal.valueOf(0));
            stmt.setBigDecimal(5, vehicle.getCapacity()); // initially vehicle is empty
            if (stmt.executeUpdate() == 1) {
                System.out.println("Courier with user name '" + courierUserName + "' has successfully taken the " +
                        "vehicle with license plate number: " + vehicle.getLicensePlateNumber() + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Courier with user name '" + courierUserName + "' has failed to take the vehicle with " +
                "license plate number: " + vehicle.getLicensePlateNumber() + '!');
        return false;
    }

    private void leaveVehicle(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("delete from IsDriving where userName = ?")) {
            stmt.setString(1, courierUserName);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Courier with user name '" + courierUserName + "' has successfully left the vehicle.");
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Courier with user name '" + courierUserName + "' has failed to leave the vehicle.");
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

    private Address chooseNextStop(Address previousStop, Collection<Address> addresses) {
        Address nextStop = null;
        double distance, minDistance = Double.MAX_VALUE;
        for (Address stop : addresses) {
            distance = getDistance(stop, previousStop);
            if (distance < minDistance) {
                nextStop = stop;
                minDistance = distance;
            }
        }
        return nextStop;
    }

    private Address addAddressesToStops(Address previousStop, Collection<Address> addresses, List<Address> stops) {
        while (!addresses.isEmpty()) {
            Address nextStop = chooseNextStop(previousStop, addresses);
            if (nextStop != null) {
                stops.add(nextStop);
                previousStop = nextStop;
                addresses.remove(nextStop);
            }
        }
        return previousStop;
    }

    private BigDecimal choosePackagesToPickUp(BigDecimal availableCapacity, List<Package> packagesToPickUp,
                                              List<Address> stops, int idCity, int idStockroom, boolean isFirstCity) {
        // TODO: Reorder code to only get packages in stockroom if needed (vehicle not full)

        List<Package> packagesInCity = CommonOperations.getNonPickedUpPackagesInCity(idCity);
        List<Package> packagesInStockroom = CommonOperations.getPackagesInStockroom(idStockroom);

        if (packagesInCity.isEmpty()) {
            if (packagesInStockroom.isEmpty()) {
                System.out.println("No packages for pick-up found in city with primary key: " + idCity + '!');
                return availableCapacity;
            } else {
                System.out.println("No non-picked-up packages found in city with primary key: " + idCity + '.');
            }
        } else if (packagesInStockroom.isEmpty()) {
            System.out.println("No packages found in stockroom with primary key: " + idStockroom + '.');
        }

        BigDecimal remainingCapacity = pickUpPackages(pickUpPackages(availableCapacity, packagesInCity), packagesInStockroom);

        if (packagesInCity.isEmpty() && packagesInStockroom.isEmpty()) {
            return availableCapacity;
        }

        // Add packages to be picked-up
        packagesToPickUp.addAll(packagesInCity);
        packagesToPickUp.addAll(packagesInStockroom);

        // Calculate all required addresses for pick-up (multiple packages can be on the same address...)
        Collection<Address> addressesForPickUp = new HashSet<>();
        for (Package p : packagesInCity) {
            Address address = CommonOperations.getAddressById(p.getIdAddress());
            if (address == null) {
                System.out.println("Failed to get address with primary key: " + p.getIdAddress() + '!');
            } else {
                addressesForPickUp.add(address);
            }
        }

        // Add stops for package pick-up
        addAddressesToStops(stops.get(stops.size() - 1), addressesForPickUp, stops);

        // Add stockroom address as a stop if there are packages to be picked-up there
        if (!packagesInStockroom.isEmpty()) {
            if (isFirstCity) {
                // Add starting stop (starting stockroom address) as next stop
                stops.add(stops.get(0));
            } else {
                stops.add(CommonOperations.getAddressById(CommonOperations.getStockroomAddress(idStockroom)));
            }
        }

        return remainingCapacity;
    }

    private void abort(String courierUserName) {
        // Revert courier status
        CommonOperations.setCourierStatus(courierUserName, 0);
        // Leave vehicle
        leaveVehicle(courierUserName);

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

        // TODO: Check which vehicle should the courier pick from the stockroom (largest capacity, smallest fuel
        //       consumption and therefore price)
        // Get first available vehicle in stockroom
        Vehicle vehicle = vehicles.get(0);

        List<Package> packagesToPickUp = new ArrayList<>();
        List<Address> stops = new ArrayList<>();

        // Starting address (zeroth stop) is the stockroom in the starting city
        stops.add(CommonOperations.getAddressById(CommonOperations.getStockroomAddress(idStockroom)));

        // Choose packages to pick-up in the starting city
        BigDecimal availableCapacity = choosePackagesToPickUp(vehicle.getCapacity(), packagesToPickUp, stops, idCity,
                idStockroom, true);

        if (availableCapacity == null) {
            System.out.println("Failed to choose packages to pick up in the starting city!");
            return false;
        } else if (availableCapacity.compareTo(vehicle.getCapacity()) == 0) {
            System.out.println("No packages can be delivered at this time because of capacity constraints!");
            return false;
        }

        // All packages that are to be picked-up in the starting city are to be delivered
        int numberOfPackagesToDeliver = packagesToPickUp.size();
/*
        // Calculate all required addresses for delivery (multiple packages can be on the same address...)
        Collection<Address> addressesForDelivery = new HashSet<>();
        for (Package p : packagesToPickUp) {
            p.setPickedUp(true);
            Address address = CommonOperations.getAddressById(p.getIdAddressTo());
            if (address == null) {
                System.out.println("Failed to get address with primary key: " + p.getIdAddress() + '!');
            } else {
                addressesForDelivery.add(address);
            }
        }

        while (!addressesForDelivery.isEmpty()) {
            // Calculate the next city to be visited
            Address previousStop = stops.get(stops.size() - 1);
            Address nextStop = chooseNextStop(previousStop, addressesForDelivery);
            if (nextStop == null) {
                System.out.println("Failed to select next stop for delivery!");
                return false;
            } else {
                idCity = nextStop.getIdCity();
                stops.add(nextStop);
                previousStop = nextStop;
                addressesForDelivery.remove(nextStop);
            }
            Collection<Address> addressesForDeliveryInCity = new ArrayList<>();
            for (Address address : addressesForDelivery) {
                if (address.getIdCity() == idCity) {
                    addressesForDeliveryInCity.add(address);
                }
            }
            addressesForDelivery.removeAll(addressesForDeliveryInCity);
            if (!addressesForDeliveryInCity.isEmpty()) {
                previousStop = addAddressesToStops(previousStop, addressesForDeliveryInCity, stops);
            }

            for (int i = 0; i < numberOfPackagesToDeliver; i++) {
                Package p = packagesToPickUp.get(i);
                if (p.getIdAddressTo() == nextStop.getIdAddress()) {
                    // drop package
                }
            }
            // TODO: Subtract delivered package weight from availableCapacity...
            // TODO: Call choosePackagesToPickUp...
        }
*/
        // Update courier status
        if (!CommonOperations.setCourierStatus(courierUserName, 1)) {
            System.out.println("Failed to change courier status to driving! Aborting...");
            abort(courierUserName);
            return false;
        }

        // Take the vehicle
        if (!takeVehicle(courierUserName, vehicle)) {
            System.out.println("Failed to take the vehicle! Aborting...");
            abort(courierUserName);
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        // Insert packages to be picked up into table 'IsPickingUp'
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsPickingUp (userName, idPackage) values (?, ?)")) {
            stmt.setString(1, courierUserName);
            for (Package p : packagesToPickUp) {
                stmt.setInt(2, p.getIdPackage());
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add package with primary key: " + p.getIdPackage() + " to pick-up list!");
                    abort(courierUserName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            abort(courierUserName);
            return false;
        }

        // Insert packages to be delivered into table 'IsDelivering'
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDelivering (userName, idPackage) values (?, ?)")) {
            stmt.setString(1, courierUserName);
            for (int i = 0; i < numberOfPackagesToDeliver; i++) {
                Package p = packagesToPickUp.get(i);
                stmt.setInt(2, p.getIdPackage());
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add package with primary key: " + p.getIdPackage() + " to delivery list!");
                    abort(courierUserName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            abort(courierUserName);
            return false;
        }

        // Insert stops (addresses) into table 'Stop'
        try (PreparedStatement stmt = conn.prepareStatement("insert into Stop (userName, idAddress, stopNumber) values (?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            for (int i = 0; i < stops.size(); i++) {
                stmt.setInt(2, stops.get(i).getIdAddress());
                stmt.setInt(3, i); // stop number
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to add address with primary key: " + stops.get(i).getIdAddress() + " to stops list!");
                    abort(courierUserName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            abort(courierUserName);
            return false;
        }

        return true;
    }

    @Override
    public int nextStop(String courierUserName) {
        // Check if courier is already driving
        if (CommonOperations.getCourierStatus(courierUserName) != 1) {
            System.out.println("Courier with user name '" + courierUserName + "' is not driving!");
            // TODO: Check what should be returned in this case...
            return -3;
        }

        Connection conn = DB.getInstance().getConnection();

        String isDrivingSelQuery = "select licensePlateNumber, currentStopNumber, distanceTraveled, remainingCapacity from IsDriving where userName = ?";

        String licensePlateNumber;
        int currentStopNumber;
        BigDecimal distanceTraveled, remainingCapacity;

        try (PreparedStatement stmt = conn.prepareStatement(isDrivingSelQuery)) {
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Row for courier with user name '" + courierUserName + "' in 'IsDriving' table does not exist!");
                // TODO: Check what should be returned in this case...
                return -4;
            } else {
                licensePlateNumber = rs.getString(1);
                currentStopNumber = rs.getInt(2);
                distanceTraveled = rs.getBigDecimal(3);
                remainingCapacity = rs.getBigDecimal(4);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to get current stop number and remaining capacity for courier with user name '" + courierUserName + '!');
            // TODO: Check what should be returned in this case...
            return -5;
        }

        System.out.println(courierUserName + ':' + licensePlateNumber + ':' + currentStopNumber + ':' + distanceTraveled + ':' + remainingCapacity);

        String stopSelQuery = "select idAddress from Stop where userName = ? and stopNumber in (?, ?)";

        Address prevStop, nextStop;

        try (PreparedStatement stmt = conn.prepareStatement(stopSelQuery)) {
            stmt.setString(1, courierUserName);
            stmt.setInt(2, currentStopNumber);
            stmt.setInt(3, currentStopNumber + 1); // next stop number
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Row for courier with user name '" + courierUserName + "' in 'Stop' table does not exist!");
                // TODO: Check what should be returned in this case...
                return -6;
            } else {
                prevStop = CommonOperations.getAddressById(rs.getInt(1));
                if (rs.next()) { // there is a next stop...
                    nextStop = CommonOperations.getAddressById(rs.getInt(1));
                } else { // last stop...
                    // TODO: Do what's needed for last stop



                    return -1; // last stop marker
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to get current stop number and remaining capacity for courier with user name '" + courierUserName + '!');
            // TODO: Check what should be returned in this case...
            return -7;
        }

        distanceTraveled = distanceTraveled.add(BigDecimal.valueOf(getDistance(prevStop, nextStop)));

        // TODO: First delivery then pickup

        String pickUpUpdQuery = "update Package set status = ?, courierUserName = ? where idPackage in (" +
                "select Package.idPackage from (Package inner join IsPickingUp on " +
                "Package.idPackage = IsPickingUp.idPackage) where IsPickingUp.userName = ? and Package.idAddress = ?)";

        int retValue = 0;

        try (PreparedStatement stmt = conn.prepareStatement(pickUpUpdQuery)) {
            stmt.setInt(1, 2); // status = 2 (package picked up)
            stmt.setString(2, courierUserName);
            stmt.setString(3, courierUserName);
            stmt.setInt(4, nextStop.getIdAddress());
            if (stmt.executeUpdate() > 0) {
                retValue = -2; // package pickup flag
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to get packages for pickup for courier with user name '" + courierUserName + '!');
            // TODO: Check what should be returned in this case...
            return -8;
        }

        if (retValue == -2) {
            String weightSelQuery = "select weight from (Package inner join IsPickingUp on " +
                    "Package.idPackage = IsPickingUp.idPackage) where IsPickingUp.userName = ? and Package.idAddress = ?";

            try (PreparedStatement stmt = conn.prepareStatement(weightSelQuery)) {
                stmt.setString(1, courierUserName);
                stmt.setInt(2, nextStop.getIdAddress());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    remainingCapacity = remainingCapacity.subtract(rs.getBigDecimal(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Failed to get weight of packages for pickup for courier with user name '" + courierUserName + '!');
                // TODO: Check what should be returned in this case...
                return -9;
            }
        }

        // Update IsDriving
        String isDrivingUpdQuery = "update IsDriving set currentStopNumber = ?, distanceTraveled = ?, " +
                "remainingCapacity = ? where userName = ?";

        try (PreparedStatement stmt = conn.prepareStatement(isDrivingUpdQuery)) {
            stmt.setInt(1, currentStopNumber + 1); // update stop number
            stmt.setBigDecimal(2, distanceTraveled); // update distance traveled
            stmt.setBigDecimal(3, remainingCapacity); // update remaining capacity
            stmt.setString(4, courierUserName);
            if (stmt.executeUpdate() != 1) {
                System.out.println("Failed to update IsDriving table!");
                // TODO: Check what should be returned in this case...
                return -10;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            // TODO: Check what should be returned in this case...
            return -11;
        }

        return retValue;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select idPackage from Package where courierUserName = ? and status != ?";
        List<Integer> list = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, courierUserName);
            stmt.setInt(2, 3); // status = 3 (delivered)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    private nj160040_DriveOperations() {}
}