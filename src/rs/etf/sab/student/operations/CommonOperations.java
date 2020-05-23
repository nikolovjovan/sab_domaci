package rs.etf.sab.student.operations;

import rs.etf.sab.student.utils.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommonOperations {

    public static boolean cityNotExist(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idCity from City where idCity = ?")) {
            stmt.setInt(1, idCity);
            return !stmt.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean addressNotExist(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            return !stmt.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean isNullOrEmpty(String s, String paramName) {
        if (s == null || s.isEmpty()) {
            System.out.print("Null or empty " + paramName + "! ");
            return true;
        }
        return false;
    }

    // Returns -1 if user does not exist
    public static int getUserType(String userName) {
        Connection conn = DB.getInstance().getConnection();
        if (isNullOrEmpty(userName, "user name")) return -1;
        try (PreparedStatement stmt = conn.prepareStatement("select type from [User] where userName = ?")) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static boolean userNotExist(String userName) {
        return getUserType(userName) == -1;
    }

    public static int getUserTypeAndCheckIfCourier(String userName) {
        int type = CommonOperations.getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return -1;
        }
        if (type == 1) {
            System.out.println("User with user name '" + userName + "' is already a courier.");
            // TODO: Check if it should return true or false in this case...
            return -1;
        }
        return type;
    }

    public static boolean insertCourierOrRequest(String userName, String driversLicenseNumber, boolean request) {
        int type = getUserTypeAndCheckIfCourier(userName);
        if (type == -1) return false;
        if (type == 1) { // user already a courier
            // TODO: Check if it should return true or false in this case...
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String selQuery = "select status from Courier where userName = ? or driversLicenseNumber = ?";
        String insQuery = "insert into Courier (userName, driversLicenseNumber, status) values (?, ?, ?)";

        // Check if courier or courier request with the same user name or driver's license number exists...
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, userName);
            stmt.setString(2, driversLicenseNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int status = rs.getInt(1); // courier status (0 - not driving, 1 - driving, 2 - request)
                System.out.println("Courier " + (status == 2 ? "request " : "") + "with the same user name or " +
                        "driver's license number already exists!");
                return false;
            }
            // If it does not insert a new courier or courier request depending on parameter 'request'.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setString(1, userName);
            insStmt.setString(2, driversLicenseNumber);
            insStmt.setInt(3, request ? 2 : 0); // status = 0 (not currently driving) or status = 2 (request)
            if (insStmt.executeUpdate() == 1) {
                if (request) {
                    System.out.println("Successfully inserted a courier request for user with user name '" +
                            userName + "'.");
                } else {
                    PreparedStatement updStmt = conn.prepareStatement("update [User] set type = ? where userName = ?");
                    updStmt.setInt(1, 1); // type = 0 (change user type to courier)
                    updStmt.setString(2, userName);
                    if (updStmt.executeUpdate() == 1) {
                        System.out.println("Successfully made user with user name '" + userName + "' a courier.");
                        return true;
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static boolean deleteCourierOrRequest(String userName, boolean request) {
        int type = getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String delQuery = "delete from Courier where userName = ? and status " + (request ? "= ?" : "!= ?");

        try (PreparedStatement stmt = conn.prepareStatement(delQuery)) {
            stmt.setString(1, userName);
            stmt.setInt(2, 2); // status = 2 (request)
            if (stmt.executeUpdate() == 1) {
                if (request) {
                    System.out.println("Successfully deleted courier request with user name '" + userName + "'.");
                    return true;
                }
                // TODO: If user should be deleted altogether then change this to a delete statement...
                PreparedStatement updStmt = conn.prepareStatement("update [User] set type = ? where userName = ?");
                updStmt.setInt(1, 0); // type = 0 (change user type from courier to buyer)
                updStmt.setString(2, userName);
                if (updStmt.executeUpdate() == 1) {
                    System.out.println("Successfully deleted courier with user name '" + userName + "'.");
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete courier " + (request ? "request " : "") +
                "with user name '" + userName + "'.");
        return false;
    }
}