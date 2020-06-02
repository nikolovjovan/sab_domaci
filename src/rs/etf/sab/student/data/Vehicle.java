package rs.etf.sab.student.data;

import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vehicle {

    private String licensePlateNumber;
    private int fuelType;
    private BigDecimal fuelConsumption;
    private BigDecimal capacity;
    private int idStockroom;

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public int getFuelType() {
        return fuelType;
    }

    public void setFuelType(int fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public int getIdStockroom() {
        return idStockroom;
    }

    public void setIdStockroom(int idStockroom) {
        this.idStockroom = idStockroom;
    }

    public static String getVehicleSelectAttributes() {
        // TODO: Reduce this once DriveOperations are implemented...
        return "Vehicle.licensePlateNumber, Vehicle.fuelType, Vehicle.fuelConsumption, Vehicle.capacity, " +
                "Vehicle.idStockroom";
    }

    public static Vehicle getVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setLicensePlateNumber(rs.getString(1));
        v.setFuelType(rs.getInt(2));
        v.setFuelConsumption(rs.getBigDecimal(3));
        v.setCapacity(rs.getBigDecimal(4));
        v.setIdStockroom(rs.getInt(5));
        return v;
    }

    public static Vehicle getVehicleByLicensePlateNumber(String licensePlateNumber) {
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select " + getVehicleSelectAttributes() + " from Vehicle where licensePlateNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, licensePlateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return getVehicleFromResultSet(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean take(String courierUserName, String licensePlateNumber, BigDecimal capacity) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDriving (userName, licensePlateNumber, " +
                "currentStopNumber, distanceTraveled, remainingCapacity) values (?, ?, ?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            stmt.setString(2, licensePlateNumber);
            stmt.setInt(3, 0); // currentStopNumber = 0 (first stop)
            stmt.setBigDecimal(4, BigDecimal.valueOf(0));
            stmt.setBigDecimal(5, capacity); // initially vehicle is empty
            if (stmt.executeUpdate() == 1) return true;
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void leave(String courierUserName, String licensePlateNumber, boolean remember) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("delete from IsDriving where userName = ?")) {
            stmt.setString(1, courierUserName);
            if (stmt.executeUpdate() == 1) {
                if (!remember) return;
                PreparedStatement selStmt = conn.prepareStatement("select * from Drove where userName = ? " +
                        "and licensePlateNumber = ?");
                selStmt.setString(1, courierUserName);
                selStmt.setString(2, licensePlateNumber);
                ResultSet rs = selStmt.executeQuery();
                if (rs.next()) return;
                PreparedStatement insStmt = conn.prepareStatement("insert into Drove (userName, licensePlateNumber)" +
                        "values (?, ?)");
                insStmt.setString(1, courierUserName);
                insStmt.setString(2, licensePlateNumber);
                insStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}