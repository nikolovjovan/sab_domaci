package rs.etf.sab.student.operations;

import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_CourierOperations implements CourierOperations {

    private static nj160040_CourierOperations instance;

    public static nj160040_CourierOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_CourierOperations();
        }
        return instance;
    }

    @Override
    public boolean insertCourier(String userName, String driversLicenseNumber) {
        return CommonOperations.insertCourierOrRequest(userName, driversLicenseNumber, false);
    }

    @Override
    public boolean deleteCourier(String userName) {
        return CommonOperations.deleteCourierOrRequest(userName, false);
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
        if (status != 0 && status != 1) {
            System.out.println("Invalid status: " + status + '!');
            // TODO: Check return value in this case...
            return null;
        }

        Connection conn = DB.getInstance().getConnection();

        List<String> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select userName from Courier where status = ?")) {
            stmt.setInt(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public List<String> getAllCouriers() {
        Connection conn = DB.getInstance().getConnection();

        List<String> list = new ArrayList<>();

        // Get all accepted couriers (ignore courier requests)
        try (PreparedStatement stmt = conn.prepareStatement("select userName from Courier where status != ?")) {
            stmt.setInt(1, 2); // status = 2 (courier request)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        // TODO: Implement this method...
        return null;
    }

    private nj160040_CourierOperations() {}
}