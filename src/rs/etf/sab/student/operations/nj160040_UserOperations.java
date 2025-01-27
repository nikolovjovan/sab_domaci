package rs.etf.sab.student.operations;

import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.student.utils.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_UserOperations implements UserOperations {

    private static nj160040_UserOperations instance;

    public static nj160040_UserOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_UserOperations();
        }
        return instance;
    }

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idAddress) {
        Connection conn = DB.getInstance().getConnection();

        if (CommonOperations.isNullOrEmpty(userName, "user name") ||
                CommonOperations.isNullOrEmpty(firstName, "first name") ||
                CommonOperations.isNullOrEmpty(lastName, "last name") ||
                CommonOperations.isNullOrEmpty(password, "password")) {
            System.out.println("Cannot add user!");
            return false;
        }
        if (!Character.isUpperCase(firstName.charAt(0))) {
            System.out.println("Invalid first name format! First character is not uppercase.");
            return false;
        }
        if (!Character.isUpperCase(lastName.charAt(0))) {
            System.out.println("Invalid last name format! First character is not uppercase.");
            return false;
        }
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[_\\W])(?=\\S+$)[A-Za-z0-9_\\W]{8,20}$")) {
            System.out.println("Invalid password format! Password must be at least 8 characters long with at least " +
                    "one uppercase letter, one lowercase letter, one digit and one special sign [_\\W].");
            return false;
        }
        if (CommonOperations.addressNotExist(idAddress)) {
            System.out.println("Address with primary key: " + idAddress + " does not exist!");
            return false;
        }

        String selQuery = "select userName from [User] where userName = ?";
        String insQuery = "insert into [User] (userName, firstName, lastName, password, idAddress, type) " +
                "values (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement selStmt = conn.prepareStatement(selQuery)) {
            selStmt.setString(1, userName);

            // Check if the user already exists...
            ResultSet rs = selStmt.executeQuery();
            if (rs.next()) {
                System.out.println("User with user name '" + userName + "' already exists!");
                return false;
            }

            // If it does not insert a new user with specified parameters and type = 0 (buyer)
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setString(1, userName);
            insStmt.setString(2, firstName);
            insStmt.setString(3, lastName);
            // TODO: Check if pwd needs hashing (irl it should but this is homework after all...)
            insStmt.setString(4, password);
            insStmt.setInt(5, idAddress);
            insStmt.setInt(6, CommonOperations.userTypeDefault);
            if (insStmt.executeUpdate() == 1) {
                System.out.println("Successfully inserted user '" + firstName + ' ' + lastName + "' with user name '" +
                        userName + "'.");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert user '" + firstName + ' ' + lastName + "' with user name '" +
                userName + "'!");
        return false;
    }

    @Override
    public boolean declareAdmin(String userName) {
        Connection conn = DB.getInstance().getConnection();

        if (CommonOperations.isNullOrEmpty(userName, "user name")) {
            System.out.println("Cannot declare admin!");
            return false;
        }

        int type = CommonOperations.getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return false;
        }
        if ((type & CommonOperations.userTypeAdminFlag) != 0) {
            System.out.println("User with user name '" + userName + "' is already an administrator.");
            return false;
        }

        // User was not admin, update the row...
        try (PreparedStatement stmt = conn.prepareStatement("update [User] set type = ? where userName = ?")) {
            stmt.setInt(1, type | CommonOperations.userTypeAdminFlag);
            stmt.setString(2, userName);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully made user with user name '" + userName + "' an administrator.");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to make user with user name '" + userName + "' an administrator!");
        return false;
    }

    @Override
    public int getSentPackages(String... userNames) {
        String userNameList = DB.getInstance().generateColumnValueList(userNames);
        if (userNameList == null) return 0;

        Connection conn = DB.getInstance().getConnection();

        try (Statement stmt = conn.createStatement()) {
            int count = 0;
            ResultSet rs = stmt.executeQuery("select userName from [User] where userName in (" + userNameList + ')');
            while (rs.next()) count++;
            if (count == 0) {
                System.out.println("No users with user names: " + userNameList + " found!");
                return -1;
            } else {
                rs = stmt.executeQuery("select count(*) from Package where senderUserName in (" + userNameList + ')');
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public int deleteUsers(String... userNames) {
        String userNameList = DB.getInstance().generateColumnValueList(userNames);
        if (userNameList == null) return 0;

        Connection conn = DB.getInstance().getConnection();

        try (Statement stmt = conn.createStatement()) {
            int count = stmt.executeUpdate("delete from [User] where userName in (" + userNameList + ')');
            if (count == 0) {
                System.out.println("No users with user names: " + userNameList + " found!");
            } else {
                System.out.println("Deleted " + count + " users.");
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public List<String> getAllUsers() {
        Connection conn = DB.getInstance().getConnection();

        List<String> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select userName from [User]")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    private nj160040_UserOperations() {}
}