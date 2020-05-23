package rs.etf.sab.student.tests;

import org.junit.*;

import java.util.List;
import java.util.Random;

import static rs.etf.sab.student.utils.TestUtils.*;

public class StockroomOperationsTest {

    private void checkTwoSameStockrooms(int rowIdValid, int rowIdInvalid) {
        Assert.assertNotEquals(-1, rowIdValid);
        Assert.assertEquals(-1, rowIdInvalid);
        List<Integer> list = stockroomOperations.getAllStockrooms();
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
        int rowId = insertSampleStockroom();
        List<Integer> list = stockroomOperations.getAllStockrooms();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowId));
    }

    @Test
    public void insertDistrict_TwoStockrooms_SameAddress() {
        int idAddress = insertSampleAddress();
        int rowIdValid = stockroomOperations.insertDistrict(idAddress);
        int rowIdInvalid = stockroomOperations.insertDistrict(idAddress);
        checkTwoSameStockrooms(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoStockrooms_SameCity() {
        int idCity = insertSampleCity();
        int idAddress1 = insertSampleAddress(idCity);
        int idAddress2 = insertSampleAddress(idCity, "Knez Mihajlova", 23, 79, 52);
        int rowIdValid = stockroomOperations.insertDistrict(idAddress1);
        int rowIdInvalid = stockroomOperations.insertDistrict(idAddress2);
        checkTwoSameStockrooms(rowIdValid, rowIdInvalid);
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
        Assert.assertTrue(stockroomOperations.deleteDistrict(insertSampleStockroom()));
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