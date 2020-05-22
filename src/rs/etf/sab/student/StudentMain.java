package rs.etf.sab.student;

import rs.etf.sab.student.tests.CustomTestRunner;
import rs.etf.sab.tests.TestHandler;
//import rs.etf.sab.tests.TestRunner;

public class StudentMain {

    public static void main(String[] args) {
        TestHandler.createInstance(
                nj160040_AddressOperations.getInstance(),
                nj160040_CityOperations.getInstance(),
                nj160040_CourierOperations.getInstance(),
                nj160040_CourierRequestOperations.getInstance(),
                nj160040_DriveOperations.getInstance(),
                nj160040_GeneralOperations.getInstance(),
                nj160040_PackageOperations.getInstance(),
                nj160040_StockroomOperations.getInstance(),
                nj160040_UserOperations.getInstance(),
                nj160040_VehicleOperations.getInstance());

        // TODO: Uncomment original test runner call and use it instead of the custom one.
        // TestRunner.runTests();

        CustomTestRunner.runTests();
    }
}