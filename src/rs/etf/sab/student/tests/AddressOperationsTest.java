package rs.etf.sab.student.tests;

import org.junit.*;

import java.util.List;
import java.util.Random;

import static rs.etf.sab.student.utils.TestUtils.*;

public class AddressOperationsTest {

    private void checkTwoSameAddresses(int rowIdValid, int rowIdInvalid) {
        Assert.assertNotEquals(-1, rowIdValid);
        Assert.assertEquals(-1, rowIdInvalid);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowIdValid));
    }

    @Before
    public void setUp() {
        generalOperations.eraseAll();
    }

    @AfterClass
    public static void tearDown() {
        generalOperations.eraseAll();
    }

    @Test
    public void insertDistrict_OnlyOne() {
        int rowId = insertSampleAddress();
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowId));
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCityStreetNumberAndCoordinates() {
        int idCity = insertSampleCity();
        int rowIdValid = insertSampleAddress(idCity);
        int rowIdInvalid = addressOperations.insertDistrict(
                sampleAddressStreet, sampleAddressNumber, idCity, sampleAddressXCord, sampleAddressYCord);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCityStreetAndNumber() {
        int idCity = insertSampleCity();
        int rowIdValid = insertSampleAddress(idCity);
        int rowIdInvalid = addressOperations.insertDistrict(
                sampleAddressStreet, sampleAddressNumber, idCity, 78, 25);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCoordinates() {
        int idCity = insertSampleCity();
        int rowIdValid = insertSampleAddress(idCity);
        int rowIdInvalid = addressOperations.insertDistrict(
                "Cara Dusana", 59, idCity, sampleAddressXCord, sampleAddressYCord);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_MultipleAddresses_SameCity() {
        int idCity = insertSampleCity();
        int rowId1 = insertSampleAddress(idCity);
        int rowId2 = insertSampleAddress(idCity, "Knez Mihajlova", 23, 79, 52);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(rowId1));
        Assert.assertTrue(list.contains(rowId2));
    }

    @Test
    public void insertDistrict_MultipleAddresses_DifferentCity() {
        int idCity1 = insertSampleCity();
        int idCity2 = insertSampleCity("New York", "159489");
        int rowId1 = insertSampleAddress(idCity1);
        int rowId2 = insertSampleAddress(idCity2, "Wall Street", 789, -4897, -89);
        List<Integer> list1 = addressOperations.getAllDistrictsFromCity(idCity1);
        Assert.assertEquals(1, list1.size());
        Assert.assertTrue(list1.contains(rowId1));
        List<Integer> list2 = addressOperations.getAllDistrictsFromCity(idCity2);
        Assert.assertEquals(1, list2.size());
        Assert.assertTrue(list2.contains(rowId2));
    }

    @Test
    public void deleteDistrict_WithId_OnlyOne() {
        Assert.assertTrue(addressOperations.deleteDistrict(insertSampleAddress()));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteDistrict_WithId_OnlyOne_NotExisting() {
        Random random = new Random();
        int rowId = random.nextInt();
        Assert.assertFalse(addressOperations.deleteDistrict(rowId));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteDistricts_WithStreetAndNumber_OnlyOne() {
        insertSampleAddress();
        Assert.assertEquals(1, addressOperations.deleteDistricts(sampleAddressStreet, sampleAddressNumber));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteDistricts_WithStreetAndNumber_MultipleAddresses() {
        insertSampleAddress();
        int idCity = insertSampleCity("Novi Sad", "21000");
        int rowId = insertSampleAddress(idCity, sampleAddressStreet, sampleAddressNumber, 48, 35);
        Assert.assertNotEquals(-1, rowId);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2, addressOperations.deleteDistricts(sampleAddressStreet, sampleAddressNumber));
    }

    @Test
    public void deleteDistricts_WithStreetAndNumber_NotExisting() {
        Assert.assertEquals(0, addressOperations.deleteDistricts(sampleAddressStreet, sampleAddressNumber));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_OnlyOne() {
        int idCity = insertSampleCity();
        insertSampleAddress(idCity);
        Assert.assertEquals(1, addressOperations.deleteAllAddressesFromCity(idCity));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_MultipleAddresses() {
        int idCity = insertSampleCity();
        insertSampleAddress(idCity);
        insertSampleAddress(idCity, "Kneza Milosa", 23, 79, 52);
        Assert.assertEquals(2, addressOperations.deleteAllAddressesFromCity(idCity));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_NotExisting() {
        Assert.assertEquals(0, addressOperations.deleteAllAddressesFromCity(insertSampleCity()));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }
}