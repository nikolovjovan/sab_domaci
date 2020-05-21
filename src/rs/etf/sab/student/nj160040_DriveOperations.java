package rs.etf.sab.student;

import rs.etf.sab.operations.DriveOperation;

import java.util.List;

public class nj160040_DriveOperations implements DriveOperation {

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
}
