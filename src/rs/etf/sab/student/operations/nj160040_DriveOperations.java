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
    public boolean planingDrive(String courierUserName) {
        int userType = CommonOperations.getUserType(courierUserName);
        if (userType == -1) {
            System.out.println("User with user name '" + courierUserName + "' does not exist!");
            return false;
        }
        if (userType != 1) {
            System.out.println("User with user name '" + courierUserName + "' is not a courier!");
            return false;
        }

        // TODO: Change courier status to 1 (driving)

        return false;
    }

    @Override
    public int nextStop(String courierUserName) {
        return 0;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUserName) {
        return null;
    }

    private nj160040_DriveOperations() {}
}