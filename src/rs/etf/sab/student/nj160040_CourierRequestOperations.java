package rs.etf.sab.student;

import rs.etf.sab.operations.CourierRequestOperation;

import java.util.List;

public class nj160040_CourierRequestOperations implements CourierRequestOperation {

    private static nj160040_CourierRequestOperations instance;

    public static nj160040_CourierRequestOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_CourierRequestOperations();
        }
        return instance;
    }

    @Override
    public boolean insertCourierRequest(String s, String s1) {
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String s) {
        return false;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String s, String s1) {
        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        return null;
    }

    @Override
    public boolean grantRequest(String s) {
        return false;
    }

    private nj160040_CourierRequestOperations() {}
}