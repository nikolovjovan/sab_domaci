package rs.etf.sab.student;

import rs.etf.sab.operations.CityOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_CityOperations implements CityOperations {

    private static nj160040_CityOperations instance;

    public static nj160040_CityOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_CityOperations();
        }
        return instance;
    }

    @Override
    public int insertCity(String name, String postalCode) {
        Connection conn = DB.getInstance().getConnection();

        String selQuery = "select idCity from City where name = ? or postalCode = ?";
        String insQuery = "insert into City (name, postalCode) values (?, ?)";

        try (PreparedStatement selStmt = conn.prepareStatement(selQuery)) {
            selStmt.setString(1, name);
            selStmt.setString(2, postalCode);

            // Check if the city already exists...
            ResultSet rs = selStmt.executeQuery();
            if (rs.next()) {
                System.out.println("City '" + name + "' with postal code '" + postalCode + "' already exists!");
                return -1;
            }

            // If it does not insert a new city with specified name and postal code.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setString(1, name);
            insStmt.setString(2, postalCode);
            insStmt.executeUpdate();

            // Get primary key of the inserted city.
            rs = selStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Successfully inserted city '" + name + "' with postal code '" + postalCode +
                        "' and primary key: " + rs.getInt(1) + ".");
                // Return the primary key.
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert city '" + name + "' with postal code '" + postalCode + "'!");
        // Return -1 as insert failed.
        return -1;
    }

    @Override
    public int deleteCity(String... names) {
        if (names == null || names.length == 0) return 0;

        Connection conn = DB.getInstance().getConnection();

        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < names.length - 1; i++) {
            listBuilder.append('\'').append(names[i]).append("', ");
        }
        listBuilder.append('\'').append(names[names.length - 1]).append('\'');
        String nameList = listBuilder.toString();

        try (Statement stmt = conn.createStatement()) {
            int count = stmt.executeUpdate("delete from City where name in (" + nameList + ')');
            if (count == 0) {
                System.out.println("No cities named: " + nameList + " found!");
            } else {
                System.out.println("Deleted " + count + " cities.");
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public boolean deleteCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("delete from City where idCity = ?")) {
            stmt.setInt(1, idCity);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully deleted city with primary key: " + idCity + ".");
                return true;
            } else {
                System.out.println("City with primary key: " + idCity + " does not exist!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete city with primary key: " + idCity + "!");
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idCity from City")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    private nj160040_CityOperations() {}
}