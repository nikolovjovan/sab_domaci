package rs.etf.sab.student.operations;

import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.student.utils.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_AddressOperations implements AddressOperations {

    private static nj160040_AddressOperations instance;

    public static nj160040_AddressOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_AddressOperations();
        }
        return instance;
    }

    @Override
    public int insertAddress(String street, int number, int idCity, int xCord, int yCord) {
        Connection conn = DB.getInstance().getConnection();

        // TODO: Check if tuple idCity-street-number should be unique...
//        String addressSelQuery = "select idAddress from Address where " +
//                "(idCity = ? and street = ? and number = ?) or (xCord = ? and yCord = ?)";
        String addressSelQuery = "select idAddress from Address where xCord = ? and yCord = ?";
        String insQuery = "insert into Address (idCity, street, number, xCord, yCord) values (?, ?, ?, ?, ?)";

        // Check if city with specified primary key exists...
        if (CommonOperations.cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            return -1;
        }

        // Check if address with the same coordinates exists...
        try (PreparedStatement addressSelStmt = conn.prepareStatement(addressSelQuery)) {
//            addressSelStmt.setInt(1, idCity);
//            addressSelStmt.setString(2, street);
//            addressSelStmt.setInt(3, number);
//            addressSelStmt.setInt(4, xCord);
//            addressSelStmt.setInt(5, yCord);
            addressSelStmt.setInt(1, xCord);
            addressSelStmt.setInt(2, yCord);
            ResultSet rs = addressSelStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Address with specified parameters already exists!");
                return -1;
            }

            // If it does not insert a new address with specified parameters.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setInt(1, idCity);
            insStmt.setString(2, street);
            insStmt.setInt(3, number);
            insStmt.setInt(4, xCord);
            insStmt.setInt(5, yCord);
            insStmt.executeUpdate();

            // Get primary key of the inserted address.
            rs = addressSelStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Successfully inserted address '" + street + ' ' + number + "' with coordinates: " +
                        xCord + 'x' + yCord + " and primary key: " + rs.getInt(1) + '.');
                return rs.getInt(1); // Return the primary key.
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert address '" + street + ' ' + number + "' with coordinates: " +
                xCord + 'x' + yCord + '!');
        return -1;
    }

    @Override
    public int deleteAddresses(String street, int number) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("delete from Address where street = ? and number = ?")) {
            stmt.setString(1, street);
            stmt.setInt(2, number);
            int count = stmt.executeUpdate();
            if (count == 0) {
                System.out.println("No addresses with street '" + street + "' and number: " + number + " found!");
            } else {
                System.out.println("Deleted " + count + " addresses with street '" +
                        street + "' and number: " +number + '.');
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    // Why can't nobody grammar check their code...
    @Override
    public boolean deleteAdress(int idAddress) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("delete from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully deleted address with primary key: " + idAddress + ".");
                return true;
            } else {
                System.out.println("Address with primary key: " + idAddress + " does not exist!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete address with primary key: " + idAddress + "!");
        return false;
    }

    @Override
    public int deleteAllAddressesFromCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();

        // Check if city with specified primary key exists...
        if (CommonOperations.cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            return 0;
        }

        try (PreparedStatement stmt = conn.prepareStatement("delete from Address where idCity = ?")) {
            stmt.setInt(1, idCity);
            int count = stmt.executeUpdate();
            if (count == 0) {
                System.out.println("No addresses in city with primary key: " + idCity + " found!");
            } else {
                System.out.println("Deleted " + count + " addresses from city with primary key: " + idCity + '.');
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();

        // Check if city with specified primary key exists...
        if (CommonOperations.cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            return null;
        }

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from Address where idCity = ?")) {
            stmt.setInt(1, idCity);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list.isEmpty() ? null : list;
    }

    @Override
    public List<Integer> getAllAddresses() {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from Address")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_AddressOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    private nj160040_AddressOperations() {}
}