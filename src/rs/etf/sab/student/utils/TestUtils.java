package rs.etf.sab.student.utils;

import org.junit.Assert;
import rs.etf.sab.operations.*;
import rs.etf.sab.student.operations.*;

import java.math.BigDecimal;

public class TestUtils {

    public static final String sampleCityName = "Belgrade";
    public static final String sampleCityPostalCode = "11000";

    public static final String sampleAddressStreet = "Bulevar Kralja Aleksandra";
    public static final int sampleAddressNumber = 73;
    public static final int sampleAddressXCord = 69;
    public static final int sampleAddressYCord = 420;

    public static final String sampleUserUserName = "pera";
    public static final String sampleUserFirstName = "Petar";
    public static final String sampleUserLastName = "Petrovic";
    public static final String sampleUserPassword = "Tralala123@";

    public static final String sampleCourierDriversLicenseNumber = "123456789";

    public static final int samplePackageDeltaX = -5;
    public static final int samplePackageDeltaY = 7;
    public static final int samplePackageType = 2; // package with non-standard dimensions
    public static final BigDecimal samplePackageWeight = BigDecimal.valueOf(12.3);

    public static GeneralOperations generalOperations;
    public static CityOperations cityOperations;
    public static AddressOperations addressOperations;
    public static StockroomOperations stockroomOperations;
    public static UserOperations userOperations;
    public static CourierOperations courierOperations;
    public static CourierRequestOperation courierRequestOperations;
    public static PackageOperations packageOperations;
    public static DriveOperation driveOperations;

    static {
        generalOperations = nj160040_GeneralOperations.getInstance();
        Assert.assertNotNull(generalOperations);
        cityOperations = nj160040_CityOperations.getInstance();
        Assert.assertNotNull(cityOperations);
        addressOperations = nj160040_AddressOperations.getInstance();
        Assert.assertNotNull(addressOperations);
        stockroomOperations = nj160040_StockroomOperations.getInstance();
        Assert.assertNotNull(stockroomOperations);
        userOperations = nj160040_UserOperations.getInstance();
        Assert.assertNotNull(userOperations);
        courierOperations = nj160040_CourierOperations.getInstance();
        Assert.assertNotNull(courierOperations);
        courierRequestOperations = nj160040_CourierRequestOperations.getInstance();
        Assert.assertNotNull(courierRequestOperations);
        packageOperations = nj160040_PackageOperations.getInstance();
        Assert.assertNotNull(packageOperations);
        driveOperations = nj160040_DriveOperations.getInstance();
        Assert.assertNotNull(driveOperations);
    }

    public static int insertSampleCity(String name, String postalCode) {
        int idCity = cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1, idCity);
        return idCity;
    }

    public static int insertSampleCity() {
        return insertSampleCity(sampleCityName, sampleCityPostalCode);
    }

    public static int insertSampleAddress(int idCity, String street, int number, int xCord, int yCord) {
        int rowId = addressOperations.insertAddress(street, number, idCity, xCord, yCord);
        Assert.assertNotEquals(-1, rowId);
        return rowId;
    }

    public static int insertSampleAddress(int idCity) {
        return insertSampleAddress(idCity, sampleAddressStreet, sampleAddressNumber,
                sampleAddressXCord, sampleAddressYCord);
    }

    public static int insertSampleAddress() {
        return insertSampleAddress(insertSampleCity(), sampleAddressStreet, sampleAddressNumber,
                sampleAddressXCord, sampleAddressYCord);
    }

    public static int insertSampleStockroom(int idAddress) {
        int rowId = stockroomOperations.insertStockroom(idAddress);
        Assert.assertNotEquals(-1, rowId);
        return rowId;
    }

    public static int insertSampleStockroom() {
        return insertSampleStockroom(insertSampleAddress());
    }

    public static void insertSampleUser(String userName, String firstName, String lastName, String password, int idAddress) {
        Assert.assertTrue(userOperations.insertUser(userName, firstName, lastName, password, idAddress));
    }

    public static void insertSampleUser(String userName, int idAddress) {
        insertSampleUser(userName, sampleUserFirstName, sampleUserLastName, sampleUserPassword, idAddress);
    }

    public static void insertSampleUser(int idAddress) {
        insertSampleUser(sampleUserUserName, idAddress);
    }

    public static void insertSampleUser() {
        insertSampleUser(sampleUserUserName, insertSampleAddress());
    }

    public static void insertSampleCourier(String userName, String driversLicenseNumber) {
        Assert.assertTrue(courierOperations.insertCourier(userName, driversLicenseNumber));
    }

    public static void insertSampleCourier() {
        insertSampleCourier(sampleUserUserName, sampleCourierDriversLicenseNumber);
    }

    public static void insertSampleCourierRequest(String userName, String driversLicenseNumber) {
        Assert.assertTrue(courierRequestOperations.insertCourierRequest(userName, driversLicenseNumber));
    }

    public static void insertSampleCourierRequest() {
        insertSampleCourierRequest(sampleUserUserName, sampleCourierDriversLicenseNumber);
    }

    public static int insertSamplePackage(int deltaX, int deltaY, int type, BigDecimal weight) {
        int idCity = insertSampleCity();
        int idAddressFrom = insertSampleAddress(idCity);
        int idAddressTo = insertSampleAddress(idCity, sampleAddressStreet, 52,
                sampleAddressXCord + deltaX, sampleAddressYCord + deltaY);
        insertSampleUser(idAddressFrom);
        int rowId = packageOperations.insertPackage(idAddressFrom, idAddressTo, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        return rowId;
    }

    public static int insertSamplePackage() {
        return insertSamplePackage(samplePackageDeltaX, samplePackageDeltaY, samplePackageType, samplePackageWeight);
    }
}