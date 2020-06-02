package rs.etf.sab.student.operations;

import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_VehicleOperations implements VehicleOperations {

    private static nj160040_VehicleOperations instance;

    public static nj160040_VehicleOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_VehicleOperations();
        }
        return instance;
    }

    private boolean isBeingDriven(String licensePlateNumber) {
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select userName from IsDriving where licensePlateNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, licensePlateNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean notInStockroom(String licensePlateNumber) {
        if (isBeingDriven(licensePlateNumber)) return true;
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select idStockroom from Vehicle where licensePlateNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, licensePlateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.getInt(1);
                return rs.wasNull();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean insertVehicle(String licensePlateNumber, int fuelType, BigDecimal fuelConsumption, BigDecimal capacity) {
        if (fuelType < 0 || fuelType > 2) {
            System.out.println("Invalid fuel type: " + fuelType + '!');
        }
        if (fuelConsumption.signum() == -1) {
            System.out.println("Invalid fuel consumption: " + fuelConsumption + '!');
        }
        if (capacity.signum() == -1) {
            System.out.println("Invalid capacity: " + capacity + '!');
        }

        Connection conn = DB.getInstance().getConnection();

        String selQuery = "select licensePlateNumber from Vehicle where licensePlateNumber = ?";
        String insQuery = "insert into Vehicle (licensePlateNumber, fuelType, fuelConsumption, capacity) values (?, ?, ?, ?)";

        // Check if vehicle with same license plate number already exists...
        try (PreparedStatement selStmt = conn.prepareStatement(selQuery)) {
            selStmt.setString(1, licensePlateNumber);
            ResultSet rs = selStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Vehicle with specified license plate number already exists!");
                return false;
            }

            // If it does not insert a new vehicle with specified parameters.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setString(1, licensePlateNumber);
            insStmt.setInt(2, fuelType);
            insStmt.setBigDecimal(3, fuelConsumption);
            insStmt.setBigDecimal(4, capacity);
            if (insStmt.executeUpdate() == 1) {
                System.out.println("Successfully inserted vehicle with license plate number '" + licensePlateNumber + "'!");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert vehicle with license plate number '" + licensePlateNumber + "'!");
        return false;
    }

    @Override
    public int deleteVehicles(String... licensePlateNumbers) {
        String licensePlateNumbersList = DB.getInstance().generateColumnValueList(licensePlateNumbers);
        if (licensePlateNumbersList == null) return 0;

        Connection conn = DB.getInstance().getConnection();

        try (Statement stmt = conn.createStatement()) {
            int count = stmt.executeUpdate("delete from Vehicle where licensePlateNumber in (" + licensePlateNumbersList + ')');
            if (count == 0) {
                System.out.println("No vehicles with license plate numbers: " + licensePlateNumbersList + " found!");
            } else {
                System.out.println("Deleted " + count + " vehicles.");
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public List<String> getAllVehichles() {
        Connection conn = DB.getInstance().getConnection();

        List<String> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select licensePlateNumber from Vehicle")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public boolean changeFuelType(String licensePlateNumber, int fuelType) {
        if (fuelType < 0 || fuelType > 2) {
            System.out.println("Invalid fuel type: " + fuelType + '!');
        }

        if (notInStockroom(licensePlateNumber)) {
            System.out.println("Vehicle is not in stockroom! Cannot change fuel type!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Vehicle set fuelType = ? where licensePlateNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setInt(1, fuelType);
            stmt.setString(2, licensePlateNumber);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed fuel type of the vehicle with license plate number: " + licensePlateNumber + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change fuel type of the vehicle with license plate number: " + licensePlateNumber + '!');
        return false;
    }

    @Override
    public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {
        if (fuelConsumption.signum() == -1) {
            System.out.println("Invalid fuel consumption: " + fuelConsumption + '!');
        }

        if (notInStockroom(licensePlateNumber)) {
            System.out.println("Vehicle is not in stockroom! Cannot change fuel consumption!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Vehicle set fuelConsumption = ? where licensePlateNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setBigDecimal(1, fuelConsumption);
            stmt.setString(2, licensePlateNumber);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed fuel consumption of the vehicle with license plate number: " + licensePlateNumber + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change fuel consumption of the vehicle with license plate number: " + licensePlateNumber + '!');
        return false;
    }

    @Override
    public boolean changeCapacity(String licensePlateNumber, BigDecimal capacity) {
        if (capacity.signum() == -1) {
            System.out.println("Invalid capacity: " + capacity + '!');
        }

        if (notInStockroom(licensePlateNumber)) {
            System.out.println("Vehicle is not in stockroom! Cannot change capacity!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Vehicle set capacity = ? where licensePlateNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setBigDecimal(1, capacity);
            stmt.setString(2, licensePlateNumber);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed capacity of the vehicle with license plate number: " + licensePlateNumber + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change capacity of the vehicle with license plate number: " + licensePlateNumber + '!');
        return false;
    }

    @Override
    public boolean parkVehicle(String licensePlateNumber, int idStockroom) {
        if (CommonOperations.stockroomNotExist(idStockroom)) {
            System.out.println("Stockroom with primary key: " + idStockroom + " does not exist! Cannot park the vehicle!");
            return false;
        }

        if (isBeingDriven(licensePlateNumber)) {
            System.out.println("Vehicle is being driven! Cannot park the vehicle!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Vehicle set idStockroom = ? where licensePlateNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setInt(1, idStockroom);
            stmt.setString(2, licensePlateNumber);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully parked vehicle with license plate number: " + licensePlateNumber + " into stockroom with primary key: " + idStockroom + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to park the vehicle with license plate number: " + licensePlateNumber + " into stockroom with primary key: " + idStockroom + '!');
        return false;
    }

    private nj160040_VehicleOperations() {}
}