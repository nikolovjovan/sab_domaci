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

    @Test
    public void insertCourierRequest_OnlyOne() {
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameUserNameAndDriversLicenseNumber() {
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameUserName() {
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void insertCourierRequest_TwoCourierRequests_SameDriversLicenseNumber() {
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertFalse(courierRequestOperations.insertCourierRequest(
                sampleUserUserName + '2', sampleCourierDriversLicenseNumber));
        Assert.assertEquals(1, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }

    @Test
    public void deleteCourierRequest_WithUserName_OnlyOne() {
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

    @Test
    public void changeDriverLicenseNumberInCourierRequest_OnlyOne() {
        insertSampleUser();
        insertSampleCourierRequest();
        Assert.assertTrue(courierRequestOperations.changeDriverLicenceNumberInCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
    }

    @Test
    public void changeDriverLicenseNumberInCourierRequest_OnlyOne_AlreadyCourier() {
        insertSampleUser();
        insertSampleCourier();
        Assert.assertFalse(courierRequestOperations.changeDriverLicenceNumberInCourierRequest(
                sampleUserUserName, sampleCourierDriversLicenseNumber + '8'));
    }

    @Test
    public void grantRequest_OnlyOne() {
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
        insertSampleUser();
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        Assert.assertFalse(courierRequestOperations.grantRequest(sampleUserUserName));
        Assert.assertEquals(0, courierRequestOperations.getAllCourierRequests().size());
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
    }
}