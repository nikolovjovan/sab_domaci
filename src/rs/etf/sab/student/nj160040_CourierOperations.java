package rs.etf.sab.student;

import rs.etf.sab.operations.CourierOperations;

import java.math.BigDecimal;
import java.util.List;

public class nj160040_CourierOperations implements CourierOperations {

    private static nj160040_CourierOperations instance;

    public static nj160040_CourierOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_CourierOperations();
        }
        return instance;
    }

    @Override
    public boolean insertCourier(String s, String s1) {
        return false;
    }

    @Override
    public boolean deleteCourier(String s) {
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        return null;
    }

    @Override
    public List<String> getAllCouriers() {
        return null;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        return null;
    }

    private nj160040_CourierOperations() {}
}