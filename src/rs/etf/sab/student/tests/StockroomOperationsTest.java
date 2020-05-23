package rs.etf.sab.student.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.student.nj160040_AddressOperations;
import rs.etf.sab.student.nj160040_CityOperations;
import rs.etf.sab.student.nj160040_GeneralOperations;
import rs.etf.sab.student.nj160040_StockroomOperations;

import java.util.List;
import java.util.Random;

public class StockroomOperationsTest {

    private static final String sampleCityName = "Belgrade";
    private static final String sampleCityPostalCode = "11000";

    private static final String sampleAddressStreet = "Bulevar Kralja Aleksandra";
    private static final int sampleAddressNumber = 73;
    private static final int sampleAddressXCord = 69;
    private static final int sampleAddressYCord = 420;

    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private StockroomOperations stockroomOperations;

    private int insertSampleCity(String name, String postalCode) {
        int idCity = cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1, idCity);
        return idCity;
    }

    private int insertSampleCity() {
        return insertSampleCity(sampleCityName, sampleCityPostalCode);
    }

    private int insertSampleAddress(int idCity, String street, int number, int xCord, int yCord) {
        int rowId = addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        Assert.assertNotEquals(-1, rowId);
        return rowId;
    }

    private int insertSampleAddress(int idCity) {
        return insertSampleAddress(idCity, sampleAddressStreet, sampleAddressNumber,
                sampleAddressXCord, sampleAddressYCord);
    }

    private int insertSampleAddress() {
        return insertSampleAddress(insertSampleCity(), sampleAddressStreet, sampleAddressNumber,
                sampleAddressXCord, sampleAddressYCord);
    }

    private void checkTwoSameAddresses(int rowIdValid, int rowIdInvalid) {
        Assert.assertNotEquals(-1, rowIdValid);
        Assert.assertEquals(-1, rowIdInvalid);
        List<Integer> list = stockroomOperations.getAllStockrooms();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowIdValid));
    }

    @Before
    public void setUp() {
        generalOperations = nj160040_GeneralOperations.getInstance();
        Assert.assertNotNull(generalOperations);
        cityOperations = nj160040_CityOperations.getInstance();
        Assert.assertNotNull(cityOperations);
        addressOperations = nj160040_AddressOperations.getInstance();
        Assert.assertNotNull(addressOperations);
        stockroomOperations = nj160040_StockroomOperations.getInstance();
        Assert.assertNotNull(stockroomOperations);
        generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    @Test
    public void insertDistrict_OnlyOne() {
        int rowId = stockroomOperations.insertDistrict(insertSampleAddress());
        List<Integer> list = stockroomOperations.getAllStockrooms();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowId));
    }

    @Test
    public void insertDistrict_TwoStockrooms_SameAddress() {
        int idAddress = insertSampleAddress();
        int rowIdValid = stockroomOperations.insertDistrict(idAddress);
        int rowIdInvalid = stockroomOperations.insertDistrict(idAddress);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoStockrooms_SameCity() {
        int idCity = insertSampleCity();
        int idAddress1 = insertSampleAddress(idCity);
        int idAddress2 = insertSampleAddress(idCity, "Knez Mihajlova", 23, 79, 52);
        int rowIdValid = stockroomOperations.insertDistrict(idAddress1);
        int rowIdInvalid = stockroomOperations.insertDistrict(idAddress2);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_MultipleStockrooms() {
        int idCity1 = insertSampleCity();
        int idCity2 = insertSampleCity("New York", "159489");
        int idAddress1 = insertSampleAddress(idCity1);
        int idAddress2 = insertSampleAddress(idCity2, "Wall Street", 789, -4897, -89);
        int rowId1 = stockroomOperations.insertDistrict(idAddress1);
        int rowId2 = stockroomOperations.insertDistrict(idAddress2);
        // Retarded method name, documentation says that it should return the primary key of the stockroom in the
        // specified city therefore here it will be used as such...
        int idStockroom1 = stockroomOperations.deleteStockroomFromCity(idCity1);
        Assert.assertEquals(rowId1, idStockroom1);
        int idStockroom2 = stockroomOperations.deleteStockroomFromCity(idCity2);
        Assert.assertEquals(rowId2, idStockroom2);
    }

    @Test
    public void deleteDistrict_WithId_OnlyOne() {
        Assert.assertTrue(stockroomOperations.deleteDistrict(
                stockroomOperations.insertDistrict(insertSampleAddress())));
        Assert.assertEquals(0, stockroomOperations.getAllStockrooms().size());
    }

    @Test
    public void deleteDistrict_WithId_OnlyOne_NotExisting() {
        Random random = new Random();
        int rowId = random.nextInt();
        Assert.assertFalse(stockroomOperations.deleteDistrict(rowId));
        Assert.assertEquals(0, stockroomOperations.getAllStockrooms().size());
    }
}