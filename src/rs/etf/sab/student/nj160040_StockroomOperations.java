package rs.etf.sab.student;

import rs.etf.sab.operations.StockroomOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_StockroomOperations implements StockroomOperations {

    private static nj160040_StockroomOperations instance;

    public static nj160040_StockroomOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_StockroomOperations();
        }
        return instance;
    }

    @Override
    public int insertDistrict(int idAddress) {
        Connection conn = DB.getInstance().getConnection();

        String citySelQuery = "select idCity from Address where idAddress = ?";
        String insQuery = "insert into Stockroom (idAddress) values (?)";
        String primKeySelQuery = "select idStockroom from Stockroom where idAddress = ?";

        try (PreparedStatement citySelStmt = conn.prepareStatement(citySelQuery)) {
            citySelStmt.setInt(1, idAddress);
            ResultSet rs = citySelStmt.executeQuery();
            // Check if address with specified primary key exists...
            if (!rs.next()) {
                System.out.println("Address with primary key: " + idAddress + " does not exist!");
                return -1;
            }
            // Get the primary key of the city in which the specified address is in.
            int idCity = rs.getInt(1);

            // Check if stockroom exists in the same city...
            int idStockroom = getStockroomInCity(idCity);
            if (idStockroom != -1) {
                System.out.println("Stockroom with primary key: " + idStockroom +
                        " already exists in the city with primary key: " + idCity + '!');
                return -1;
            }

            // If it does not insert a new stockroom at the specified address.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setInt(1, idAddress);
            insStmt.executeUpdate();

            // Get primary key of the inserted stockroom.
            PreparedStatement primKeySelStmt = conn.prepareStatement(primKeySelQuery);
            primKeySelStmt.setInt(1, idAddress);
            rs = primKeySelStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Successfully inserted stockroom with address: " + idAddress + " and primary key " +
                        rs.getInt(1) + '.');
                return rs.getInt(1); // Return the primary key.
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert stockroom with address: " + idAddress + '!');
        return -1;
    }

    @Override
    public boolean deleteDistrict(int idStockroom) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("delete from Stockroom where idStockroom = ?")) {
            stmt.setInt(1, idStockroom);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully deleted stockroom with primary key: " + idStockroom + ".");
                return true;
            } else {
                System.out.println("Stockroom with primary key: " + idStockroom + " does not exist!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete stockroom with primary key: " + idStockroom + "!");
        return false;
    }

    public int getStockroomInCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();

        // Check if city with specified primary key exists...
        if (nj160040_CityOperations.getInstance().cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            return -1;
        }

        String stockroomSelQuery = "select idStockroom from " +
                "((Stockroom inner join Address on Stockroom.idAddress = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity)" +
                "where City.idCity = ?";

        try (PreparedStatement stmt = conn.prepareStatement(stockroomSelQuery)) {
            stmt.setInt(1, idCity);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    // Stupidly named method supposedly should return the primary key of the stockroom within the specified city instead
    // of removing said stockroom. If the city does not exist or there is no addresses in that city or there is no
    // stockroom on said addresses returns -1, otherwise returns the primary key of the found stockroom.
    @Override
    public int deleteStockroomFromCity(int idCity) {
        // TODO: See wth they want with this method...
        return getStockroomInCity(idCity);
    }

    @Override
    public List<Integer> getAllStockrooms() {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idStockroom from Stockroom")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_StockroomOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    private nj160040_StockroomOperations() {}
}