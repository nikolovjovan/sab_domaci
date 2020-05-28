package rs.etf.sab.student.tests;

import org.junit.*;

import static rs.etf.sab.student.utils.TestUtils.*;

public class CourierRequestOperationsTest {

    @Before
    public void setUp() {
        generalOperations.eraseAll();
    }

    @AfterClass
    public static void tearDown() {
        generalOperations.eraseAll();
    }

    /*
    public boolean insertCourierRequest(String userName, String driversLicenseNumber);
    public boolean deleteCourierRequest(String userName);
    public boolean changeVehicleInCourierRequest(String userName, String driversLicenseNumber);
    public List<String> getAllCourierRequests();
    public boolean grantRequest(String userName);
     */

    @Test
    public void insertCourierRequest_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameUserNameAndDriversLicenseNumber() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameUserName() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameDriversLicenseNumber() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName + '2', sampleCourierDriversLicenseNumber));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void deleteCourierRequest_WithUserName_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertTrue(courierRequestOperations.deleteCourierRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void deleteCourierRequest_WithUserName_OnlyOne_NotExisting() {
        Assert.assertFalse(courierRequestOperations.deleteCourierRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    // changeDriversLicenseNumber...
    @Test
    public void changeVehicleInCourierRequest_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertTrue(courierRequestOperations.changeDriverLicenceNumberInCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
    }

    @Test
    public void changeVehicleInCourierRequest_OnlyOne_AlreadyCourier() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        Assert.assertTrue(courierRequestOperations.changeDriverLicenceNumberInCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
    }

    @Test
    public void grantRequest_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        Assert.assertTrue(courierRequestOperations.grantRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(1, courierOperations.getAllCouriers().size());
    }

    @Test
    public void grantRequest_OnlyOne_AlreadyCourier() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(1, courierOperations.getAllCouriers().size());
        Assert.assertFalse(courierRequestOperations.grantRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(1, courierOperations.getAllCouriers().size());
    }

    @Test
    public void grantRequest_OnlyOne_NotExisting() {
        insertSampleAddress();
        insertSampleUser();
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        Assert.assertFalse(courierRequestOperations.grantRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }
}