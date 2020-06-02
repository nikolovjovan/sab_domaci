package rs.etf.sab.student.data;

import rs.etf.sab.student.operations.CommonOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Address {

    private int idAddress;
    private int idCity;
    private int xCord;
    private int yCord;

    public int getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(int idAddress) {
        this.idAddress = idAddress;
    }

    public int getIdCity() {
        return idCity;
    }

    public void setIdCity(int idCity) {
        this.idCity = idCity;
    }

    public int getxCord() {
        return xCord;
    }

    public void setxCord(int xCord) {
        this.xCord = xCord;
    }

    public int getyCord() {
        return yCord;
    }

    public void setyCord(int yCord) {
        this.yCord = yCord;
    }

    public static BigDecimal getDistance(Address a1, Address a2) {
        double deltaX = a1.getxCord() - a2.getxCord();
        double deltaY = a1.getyCord() - a2.getyCord();
        return BigDecimal.valueOf(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    }

    public static Address getAddressById(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idCity, xCord, yCord from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Address a = new Address();
                a.setIdAddress(idAddress);
                a.setIdCity(rs.getInt(1));
                a.setxCord(rs.getInt(2));
                a.setyCord(rs.getInt(3));
                return a;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean setStops(String courierUserName, List<Address> stops) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into Stop (userName, idAddress, stopNumber) " +
                "values (?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            for (int i = 0; i < stops.size(); i++) {
                stmt.setInt(2, stops.get(i).idAddress);
                stmt.setInt(3, i); // stop number
                if (stmt.executeUpdate() != 1) return false;
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void clearStops(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("delete from Stop where userName = ?")) {
            stmt.setString(1, courierUserName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}