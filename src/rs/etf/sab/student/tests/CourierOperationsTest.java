package rs.etf.sab.student.tests;

import org.junit.*;

import java.util.List;

import static rs.etf.sab.student.utils.TestUtils.*;

public class CourierOperationsTest {

    @Before
    public void setUp() {
        generalOperations.eraseAll();
    }

    @AfterClass
    public static void tearDown() {
        generalOperations.eraseAll();
    }

    /*
    public boolean insertCourier(String userName, String driversLicenseNumber);
    public boolean deleteCourier(String userName);
    public List<String> getCouriersWithStatus(int status);
    public List<String> getAllCouriers();
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries);
     */

    @Test
    public void insertCourier_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        List<String> list = courierOperations.getAllCouriers();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(sampleUserUserName));
    }

    @Test
    public void insertCourier_TwoCouriers_SameUserName() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        Assert.assertFalse(courierOperations.insertCourier(sampleUserUserName, "987654321"));
        List<String> list = courierOperations.getCouriersWithStatus(0);
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(sampleUserUserName));
    }

    @Test
    public void deleteCourier_WithUserName_OnlyOne() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        Assert.assertTrue(courierOperations.deleteCourier(sampleUserUserName));
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        // TODO: If deleteCourier should remove the user as well, change 1 to 0 here...
        Assert.assertEquals(1, userOperations.getAllUsers().size());
    }

    @Test
    public void deleteCourier_WithUserName_OnlyOne_NotCourier() {
        insertSampleAddress();
        insertSampleUser();
        Assert.assertFalse(courierOperations.deleteCourier(sampleUserUserName));
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        Assert.assertEquals(1, userOperations.getAllUsers().size());
    }

    @Test
    public void deleteCourier_WithUserName_OnlyOne_NotExisting() {
        Assert.assertFalse(courierOperations.deleteCourier(sampleUserUserName));
        Assert.assertEquals(0, courierOperations.getAllCouriers().size());
        Assert.assertEquals(0, userOperations.getAllUsers().size());
    }

    @Test
    public void getCouriersWithStatus() {
        insertSampleAddress();
        insertSampleUser();
        insertSampleCourier();
        String userName2 = sampleUserUserName + '2';
        String driversLicenseNumber2 = sampleCourierDriversLicenseNumber + '8';
        insertSampleUser(userName2);
        insertSampleCourier(userName2, driversLicenseNumber2);
        List<String> list = courierOperations.getCouriersWithStatus(0);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(sampleUserUserName));
        Assert.assertTrue(list.contains(userName2));
        Assert.assertEquals(0, courierOperations.getCouriersWithStatus(1).size());
    }

    @Test
    public void getAverageCourierProfit() {
        // TODO: Implement this test...
    }
}