package rs.etf.sab.student.operations;

import rs.etf.sab.operations.DriveOperation;

import java.util.List;

public class nj160040_DriveOperations implements DriveOperation {

    private static nj160040_DriveOperations instance;

    public static nj160040_DriveOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_DriveOperations();
        }
        return instance;
    }

    @Override
    public boolean planingDrive(String s) {
        return false;
    }

    @Override
    public int nextStop(String s) {
        return 0;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String s) {
        return null;
    }

    private nj160040_DriveOperations() {}
}