package rs.etf.sab.student.operations;

import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.student.utils.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_CourierRequestOperations implements CourierRequestOperation {

    private static nj160040_CourierRequestOperations instance;

    public static nj160040_CourierRequestOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_CourierRequestOperations();
        }
        return instance;
    }

    @Override
    public boolean insertCourierRequest(String userName, String driversLicenseNumber) {
        return CommonOperations.insertCourierOrRequest(userName, driversLicenseNumber, true);
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        int type = CommonOperations.getUserTypeAndCheckIfCourier(userName);
        if (type == -1) return false;
        if (type == 1) { // user already a courier
            System.out.println("There is no courier request to be deleted.");
            // TODO: Check if it should return true or false in this case...
            return false;
        }
        return CommonOperations.deleteCourierOrRequest(userName, true);
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String userName, String driversLicenseNumber) {
        if (CommonOperations.userNotExist(userName)) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Courier set driversLicenseNumber = ? where userName = ? and status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setString(1, driversLicenseNumber);
            stmt.setString(2, userName);
            stmt.setInt(3, 2); // status = 2 (courier request)
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed driver's license number of the courier with user name '" +
                        userName + "'.");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change driver's license number of the courier with user name '" + userName + "'.");
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        Connection conn = DB.getInstance().getConnection();

        List<String> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select userName from Courier where status = ?")) {
            stmt.setInt(1, 2); // status = 2 (courier request)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public boolean grantRequest(String userName) {
        int type = CommonOperations.getUserTypeAndCheckIfCourier(userName);
        if (type == -1) return false;
        if (type == 1) { // user already a courier
            System.out.println("There is no courier request to be granted.");
            // TODO: Check if it should return true or false in this case...
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        // Update courier request by changing status to 'not driving'.
        try (PreparedStatement stmt = conn.prepareStatement("update Courier set status = ? where userName = ?")) {
            stmt.setInt(1, 0); // status = 0 (not driving)
            stmt.setString(2, userName);
            if (stmt.executeUpdate() == 1) {
                // Update user by changing type to 'courier'.
                PreparedStatement usrUpdStmt = conn.prepareStatement("update [User] set type = ? where userName = ?");
                usrUpdStmt.setInt(1, 1); // type = 1 (courier)
                usrUpdStmt.setString(2, userName);
                if (usrUpdStmt.executeUpdate() == 1) {
                    System.out.println("Successfully granted courier request of the user with user name '" +
                            userName + "'.");
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to grant courier request of the user with user name '" + userName + "'.");
        return false;
    }

    private nj160040_CourierRequestOperations() {}
}