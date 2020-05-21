package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new nj160040_AddressOperations();
        CityOperations cityOperations = new nj160040_CityOperations();
        CourierOperations courierOperations = new nj160040_CourierOperations();
        CourierRequestOperation courierRequestOperation = new nj160040_CourierRequestOperations();
        DriveOperation driveOperation = new nj160040_DriveOperations();
        GeneralOperations generalOperations = new nj160040_GeneralOperations();
        PackageOperations packageOperations = new nj160040_PackageOperations();
        StockroomOperations stockroomOperations = new nj160040_StockroomOperations();
        UserOperations userOperations = new nj160040_UserOperations();
        VehicleOperations vehicleOperations = new nj160040_VehicleOperations();

        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    }
}