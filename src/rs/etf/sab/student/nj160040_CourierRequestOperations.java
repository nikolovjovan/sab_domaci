package rs.etf.sab.student;

import rs.etf.sab.operations.CourierRequestOperation;

import java.util.List;

public class nj160040_CourierRequestOperations implements CourierRequestOperation {

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
}