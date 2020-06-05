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

    private BigDecimal pickupPackages(BigDecimal availableCapacity, List<Package> packages) {
        int i = 0;
        while (i < packages.size()) {
            Package p = packages.get(i);
            if (availableCapacity.compareTo(p.getWeight()) > 0) {
                availableCapacity = availableCapacity.subtract(p.getWeight());
                i++;
            } else {
                packages.remove(i);
            }
        }
        return availableCapacity;
    }

    private BigDecimal choosePackagesForPickup(BigDecimal availableCapacity, List<Package> packagesToPickup,
                                               List<Package> packagesInVehicle, List<Address> stops,
                                               int idCity, int idStockroom) {
        List<Package> packagesInCity = Package.getNonPickedUpPackagesInCity(idCity);
        List<Package> packagesInStockroom = CommonOperations.getPackagesInStockroom(idStockroom);

        for (Package p : packagesToPickup) {
            for (int i = 0; i < packagesInCity.size();) {
                Package p2 = packagesInCity.get(i);
                if (p2.getIdPackage() == p.getIdPackage()) {
                    packagesInCity.remove(i);
                } else i++;
            }
            for (int i = 0; i < packagesInStockroom.size();) {
                Package p2 = packagesInStockroom.get(i);
                if (p2.getIdPackage() == p.getIdPackage()) {
                    packagesInStockroom.remove(i);
                } else i++;
            }
        }

        if (packagesInCity.isEmpty()) {
            if (packagesInStockroom.isEmpty()) {
                System.out.println("No packages for pickup found in city with primary key: " + idCity + '.');
                return availableCapacity;
            } else {
                System.out.println("No non-picked-up packages found in city with primary key: " + idCity + '.');
            }
        } else if (packagesInStockroom.isEmpty()) {
            System.out.println("No packages found in stockroom with primary key: " + idStockroom + '.');
        }

        BigDecimal remainingCapacity = pickupPackages(pickupPackages(availableCapacity, packagesInCity), packagesInStockroom);

        Address prevStop = stops.get(stops.size() - 1);
        for (Package p : packagesInCity) {
            if (p.getIdAddressFrom() != prevStop.getIdAddress()) {
                Address nextStop = Address.getAddressById(p.getIdAddressFrom());
                if (nextStop == null) {
                    System.out.println("Failed to get address with primary key: " + p.getIdAddressFrom() + '!');
                } else {
                    stops.add(nextStop);
                    prevStop = nextStop;
                }
            }
            p.setPickupStopNumber(stops.size() - 1);
        }

        if (!packagesInStockroom.isEmpty()) {
            stops.add(Address.getAddressById(CommonOperations.getStockroomAddress(idStockroom)));
            for (Package p : packagesInStockroom) {
                p.setPickupStopNumber(stops.size() - 1);
            }
        }

        packagesToPickup.addAll(packagesInCity);
        packagesToPickup.addAll(packagesInStockroom);

        packagesInVehicle.addAll(packagesInCity);
        packagesInVehicle.addAll(packagesInStockroom);

        return remainingCapacity;
    }

    private Address chooseNextStop(Address prevStop, List<Package> packagesForDelivery) {
        Address nextStop = null;
        BigDecimal distance, minDistance = BigDecimal.valueOf(Double.MAX_VALUE);
        for (Package p : packagesForDelivery) {
            Address stop = Address.getAddressById(p.getIdAddressTo());
            if (stop == null) {
                System.out.println("Failed to get destination address by id: " + p.getIdAddressTo() + '!');
                return null;
            }
            distance = Address.getDistance(prevStop, stop);
            if (distance.compareTo(minDistance) < 0) {
                nextStop = stop;
                minDistance = distance;
            }
        }
        return nextStop;
    }

    private void abort(int stage, String courierUserName, String licensePlateNumber) {
        if (stage < 1) return;
        // Reset courier status to not driving.
        CommonOperations.setCourierStatus(courierUserName, 0);
        if (stage < 2) return;
        // Leave vehicle without remembering it.
        Vehicle.leave(courierUserName, licensePlateNumber, false);
        if (stage < 3) return;
        Package.clearPackagesForDelivery(courierUserName);
        if (stage < 4) return;
        Package.clearPackagesForPickup(courierUserName);
        if (stage < 5) return;
        Address.clearStops(courierUserName);
    }

    @Override
    public boolean planingDrive(String courierUserName) {
        // PHASE 1: Delivery selection and drive initialization.

        // STEP 1: Check if courier is already driving.
        int statusCourier = CommonOperations.getCourierStatus(courierUserName);
        if (statusCourier == -1) return false; // user invalid, user not courier, etc.
        if (statusCourier == 1) {
            System.out.println("Courier with user name '" + courierUserName + "' is already driving!");
            return false;
        }

        // STEP 2: Get courier address and city (starting city).
        int idAddressCourier = CommonOperations.getUserAddress(courierUserName);
        if (idAddressCourier == -1) return false;
        int idCityCourier = CommonOperations.getAddressCity(idAddressCourier);
        if (idCityCourier == -1) return false;

        // STEP 3: Get stockroom in courier's city.
        int idStockroomCourier = CommonOperations.getStockroomInCity(idCityCourier);
        if (idStockroomCourier == -1) return false;

        // STEP 4: Get available vehicles in stockroom.
        List<Vehicle> vehicles = CommonOperations.getVehiclesInStockroom(idStockroomCourier);
        if (vehicles.isEmpty()) {
            System.out.println("There are no available vehicles in stockroom!");
            return false;
        }

        // STEP 5: Choose a vehicle from available vehicles.
        // TODO: Check how vehicle should be chosen (smallest fuel consumption, largest capacity...)
        Vehicle vehicle = vehicles.get(0);

        // STEP 6: Choose packages for pickup in starting city and add stops.
        List<Address> stops = new ArrayList<>();
        List<Package> packagesToPickup = new ArrayList<>();
        List<Package> packagesInVehicle = new ArrayList<>();

        // Add starting point - stockroom in starting city.
        stops.add(Address.getAddressById(CommonOperations.getStockroomAddress(idStockroomCourier)));

        // Choose packages for pickup and update stops...
        BigDecimal availableCapacity = choosePackagesForPickup(vehicle.getCapacity(), packagesToPickup,
                packagesInVehicle, stops, idCityCourier, idStockroomCourier);

        if (availableCapacity == null) {
            System.out.println("Failed to choose packages to pickup in the starting city!");
            return false;
        } else if (packagesToPickup.isEmpty()) {
            System.out.println("No packages can be delivered at this time!");
            return false;
        }

        // PHASE 2: Delivery planning and package pickup in visited cities.

        // STEP 1: All packages that are picked up in the starting city are to be delivered.
        int numberOfPackagesForDelivery = packagesToPickup.size();

        // STEP 2: Simulate the drive in order to add all required stops and packages for pickup.
        List<Package> packagesForDelivery = new ArrayList<>(packagesToPickup);

        // All stops in starting city "were visited" so the first stop in next city is chosen here.
        Address prevStop, nextStop = chooseNextStop(stops.get(stops.size() - 1), packagesForDelivery);
        if (nextStop == null) {
            System.out.println("Failed to choose next stop for delivery!");
            return false;
        }
        while (!packagesForDelivery.isEmpty()) {
            // Deliver all packages while in the same city...
            do {
                stops.add(nextStop);
                for (int i = 0; i < packagesForDelivery.size(); ) {
                    Package p = packagesForDelivery.get(i);
                    if (p.getIdAddressTo() == nextStop.getIdAddress()) {
                        availableCapacity = availableCapacity.add(p.getWeight());
                        p.setDeliveryStopNumber(stops.size() - 1);
                        packagesForDelivery.remove(p);
                        packagesInVehicle.remove(p);
                    } else i++;
                }
                prevStop = nextStop;
                if (packagesForDelivery.isEmpty()) break;
                nextStop = chooseNextStop(prevStop, packagesForDelivery);
                if (nextStop == null) {
                    System.out.println("Failed to choose next stop for delivery!");
                    return false;
                }
            } while (nextStop.getIdCity() == prevStop.getIdCity());
            // Next stop is not in the same city so now pickup all packages from this city.
            int idCity = prevStop.getIdCity();
            int idStockroom = CommonOperations.getStockroomInCity(idCity);
            availableCapacity = choosePackagesForPickup(availableCapacity, packagesToPickup, packagesInVehicle, stops,
                    idCity, idStockroom);
        }

        // PHASE 3: Return planning and package storing in stockroom.
        stops.add(stops.get(0)); // add starting address as last address (stockroom in starting city)

        // PHASE 4: Update database.

        // STEP 1: Set courier status to 1 (driving).
        if (!CommonOperations.setCourierStatus(courierUserName, 1)) {
            System.out.println("Failed to change courier status to driving! Aborting...");
            abort(0, courierUserName, vehicle.getLicensePlateNumber());
            return false;
        }

        // STEP 2: Take the vehicle
        if (!Vehicle.take(courierUserName, vehicle.getLicensePlateNumber(), vehicle.getCapacity())) {
            System.out.println("Failed to take the vehicle! Aborting...");
            abort(1, courierUserName, vehicle.getLicensePlateNumber());
            return false;
        }

        // STEP 3: Set packages to be delivered.
        if (!Package.setPackagesForDelivery(courierUserName, packagesToPickup, numberOfPackagesForDelivery)) {
            System.out.println("Failed to set packages to be delivered. Aborting...");
            abort(2, courierUserName, vehicle.getLicensePlateNumber());
            return false;
        }

        // STEP 4: Set packages to be picked-up.
        if (!Package.setPackagesForPickup(courierUserName, packagesToPickup)) {
            System.out.println("Failed to set packages to be picked-up. Aborting...");
            abort(3, courierUserName, vehicle.getLicensePlateNumber());
            return false;
        }

        // STEP 5: Set stops.
        if (!Address.setStops(courierUserName, stops)) {
            System.out.println("Failed to set stops. Aborting...");
            abort(4, courierUserName, vehicle.getLicensePlateNumber());
            return false;
        }

        // All is well!
        return true;
    }

    // -3 for everything invalid...
    @Override
    public int nextStop(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();

        try {
            // PHASE 1: Data retrieval.

            // STEP 1: Check if courier is driving.
            int statusCourier = CommonOperations.getCourierStatus(courierUserName);
            if (statusCourier == -1) return -3; // user invalid, user not courier, etc.
            if (statusCourier != 1) {
                System.out.println("Courier with user name '" + courierUserName + "' is not driving!");
                return -3;
            }

            // STEP 2: Get current drive state.
            String licensePlateNumber;
            int currentStopNumber;
            BigDecimal distanceTraveled, remainingCapacity;

            String isDrivingSelQuery = "select licensePlateNumber, currentStopNumber, distanceTraveled, " +
                    "remainingCapacity from IsDriving where userName = ?";

            PreparedStatement stmt = conn.prepareStatement(isDrivingSelQuery);
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Failed to get current drive state!");
                return -3;
            }

            licensePlateNumber = rs.getString(1);
            currentStopNumber = rs.getInt(2);
            distanceTraveled = rs.getBigDecimal(3);
            remainingCapacity = rs.getBigDecimal(4);

            // STEP 3: Get current stop and next stop.
            int nextStopNumber = currentStopNumber + 1;

            String stopSelQuery = "select idAddress from Stop where userName = ? and stopNumber in (?, ?, ?) order by stopNumber asc";

            stmt = conn.prepareStatement(stopSelQuery);
            stmt.setString(1, courierUserName);
            stmt.setInt(2, currentStopNumber);
            stmt.setInt(3, nextStopNumber);
            stmt.setInt(4, nextStopNumber + 1); // stop after next stop, used to check if last stop...
            rs = stmt.executeQuery();

            Address prevStop, nextStop;

            if (!rs.next()) {
                System.out.println("Error! Current stop info does not exist!");
                return -3;
            }
            prevStop = Address.getAddressById(rs.getInt(1));
            if (prevStop == null) {
                System.out.println("Failed to get current stop address!");
                return -3;
            }

            if (!rs.next()) {
                System.out.println("Error! Next stop info does not exist!");
                return -3;
            }
            nextStop = Address.getAddressById(rs.getInt(1));
            if (nextStop == null) {
                System.out.println("Failed to get next stop address!");
                return -3;
            }

            // STEP 4: Update distance traveled.
            distanceTraveled = distanceTraveled.add(Address.getDistance(prevStop, nextStop));

            // STEP 5: Check if last stop...
            if (!rs.next()) {
                // Leave vehicle and remember that it was driven by the specified courier.
                Vehicle.leave(courierUserName, licensePlateNumber, true);

                String dropInStockroomQuery = "update Package set courierUserName = null, idAddress = ? " +
                        "where courierUserName = ? and status = ?";

                stmt = conn.prepareStatement(dropInStockroomQuery);
                stmt.setInt(1, nextStop.getIdAddress());
                stmt.setString(2, courierUserName);
                stmt.setInt(3, 2); // status = 2 (package picked up)

                // Drop all packages at the last stop's address (starting stockroom address).
                stmt.executeUpdate();

                // Set courier status to not driving.
                CommonOperations.setCourierStatus(courierUserName, 0);

                // Get the list of delivered packages before clearing it...
                List<Package> packagesForDelivery = Package.getPackagesForDelivery(courierUserName);

                // Clear packages for pickup and delivery for the specified courier.
                Package.clearPackagesForPickup(courierUserName);
                Package.clearPackagesForDelivery(courierUserName);

                // Clear drive stops for this courier.
                Address.clearStops(courierUserName);

                // Get courier profit
                stmt = conn.prepareStatement("select profit from Courier where userName = ?");
                stmt.setString(1, courierUserName);
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("Failed to get courier profit!");
                    return -3;
                }

                BigDecimal profit = rs.getBigDecimal(1);

                Vehicle vehicle = Vehicle.getVehicleByLicensePlateNumber(licensePlateNumber);
                if (vehicle == null) {
                    System.out.println("Failed to get vehicle by license plate number: " + licensePlateNumber + '!');
                    return -3;
                }

                // Calculate courier profit...
                for (Package p : packagesForDelivery) {
                    profit = profit.add(p.getPrice());
                }

                BigDecimal fuelCost;
                if (vehicle.getFuelType() == 0) { // natural gas
                    fuelCost = BigDecimal.valueOf(15);
                } else if (vehicle.getFuelType() == 1) { // diesel
                    fuelCost = BigDecimal.valueOf(32);
                } else { // petrol
                    fuelCost = BigDecimal.valueOf(36);
                }

                profit = profit.subtract(distanceTraveled.multiply(vehicle.getFuelConsumption()).multiply(fuelCost));

                // Update courier profit
                stmt = conn.prepareStatement("update Courier set profit = ? where userName = ?");
                stmt.setBigDecimal(1, profit);
                stmt.setString(2, courierUserName);
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Failed to update courier profit!");
                }

                return -1; // last stop marker
            }

            // STEP 6: Check if there are packages for pickup or delivery at next stop.
            List<Package> packagesForPickup = Package.getPackagesForPickupAtStop(courierUserName, currentStopNumber + 1);
            List<Package> packagesForDelivery = Package.getPackagesForDeliveryAtStop(courierUserName, currentStopNumber + 1);

            if (!packagesForPickup.isEmpty() && !packagesForDelivery.isEmpty()) {
                System.out.println("Invalid action for stop with number: " + nextStopNumber + '!');
                return -3;
            }

            // STEP 7: Depending on whether packages should be picked up or delivered update packages accordingly...

            int retVal;

            if (!packagesForPickup.isEmpty()) {
                String pickupUpdQuery = "update Package set status = ?, courierUserName = ? where idPackage = ?";

                stmt = conn.prepareStatement(pickupUpdQuery);
                stmt.setInt(1, 2); // status = 2 (package picked up)
                stmt.setString(2, courierUserName);

                for (Package p : packagesForPickup) {
                    remainingCapacity = remainingCapacity.subtract(p.getWeight());
                    stmt.setInt(3, p.getIdPackage());
                    if (stmt.executeUpdate() != 1) {
                        System.out.println("Failed to pickup package with package id: " + p.getIdPackage() + '!');
                    }
                }

                retVal = -2;
            } else {
                String deliveryUpdQuery = "update Package set status = ?, idAddress = ? where idPackage = ?";

                stmt = conn.prepareStatement(deliveryUpdQuery);
                stmt.setInt(1, 3); // status = 3 (package delivered)
                stmt.setInt(2, nextStop.getIdAddress());

                for (Package p : packagesForDelivery) {
                    remainingCapacity = remainingCapacity.add(p.getWeight());
                    stmt.setInt(3, p.getIdPackage());
                    if (stmt.executeUpdate() != 1) {
                        System.out.println("Failed to deliver package with package id: " + p.getIdPackage() + '!');
                    }
                }

                // TODO: Check which package id should be returned if there are multiple packages for delivery at this address.
                retVal = packagesForDelivery.get(0).getIdPackage();
            }

            // STEP 8: Update current stop number, distance traveled and remaining capacity...
            String isDrivingUpdQuery = "update IsDriving set currentStopNumber = ?, distanceTraveled = ?, " +
                    "remainingCapacity = ? where userName = ?";

            stmt = conn.prepareStatement(isDrivingUpdQuery);
            stmt.setInt(1, nextStopNumber);
            stmt.setBigDecimal(2, distanceTraveled);
            stmt.setBigDecimal(3, remainingCapacity);
            stmt.setString(4, courierUserName);

            if (stmt.executeUpdate() == 1) return retVal;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQL exception occurred!");
        }

        // Something failed, should have returned before this point...
        return -3;
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
            Logger.getLogger(nj160040_DriveOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    private nj160040_DriveOperations() {}
}