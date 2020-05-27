package rs.etf.sab.student.tests;

import org.junit.*;

import java.math.BigDecimal;

import static rs.etf.sab.student.utils.TestUtils.*;

public class PackageOperationsTest {

    @Before
    public void setUp() {
        generalOperations.eraseAll();
    }

    @AfterClass
    public static void tearDown() {
        generalOperations.eraseAll();
    }

    /*
    TODO: Add other method signatures...
    public int insertPackage(int idAddressFrom, int idAddressTo, String userName, int type, BigDecimal weight);
     */

    @Test
    public void insertPackage_OnlyOne() {
        int idAddress = insertSampleAddress();
        insertSampleUser();
        int type = 2; // package with non-standard dimensions
        BigDecimal weight = BigDecimal.valueOf(12.3);
        int rowId = packageOperations.insertPackage(idAddress, idAddress, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
    }

    @Test
    public void getPriceOfDelivery_SmallPackage() {
        int idAddress = insertSampleAddress();
        insertSampleUser();
        int type = 0; // small package
        BigDecimal weight = BigDecimal.valueOf(0.57);
        int rowId = packageOperations.insertPackage(idAddress, idAddress, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
        BigDecimal expectedPrice = BigDecimal.valueOf(115);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        Assert.assertEquals(0, expectedPrice.compareTo(actualPrice));
    }

    @Test
    public void getPriceOfDelivery_StandardPackage() {
        int idAddress = insertSampleAddress();
        insertSampleUser();
        int type = 1; // standard package
        BigDecimal weight = BigDecimal.valueOf(1.926);
        int rowId = packageOperations.insertPackage(idAddress, idAddress, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
        BigDecimal expectedPrice = BigDecimal.valueOf(175).add(BigDecimal.valueOf(100).multiply(weight));
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        Assert.assertEquals(0, expectedPrice.compareTo(actualPrice));
    }

    @Test
    public void getPriceOfDelivery_NonStandardPackage() {
        int idAddress = insertSampleAddress();
        insertSampleUser();
        int type = 2; // package with non-standard dimensions
        BigDecimal weight = BigDecimal.valueOf(7.592);
        int rowId = packageOperations.insertPackage(idAddress, idAddress, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
        BigDecimal expectedPrice = BigDecimal.valueOf(250).add(BigDecimal.valueOf(100).multiply(weight));
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        System.out.println(expectedPrice + ":" + actualPrice);
        Assert.assertEquals(0, expectedPrice.compareTo(actualPrice));
    }

    @Test
    public void getPriceOfDelivery_FragilePackage() {
        int idAddress = insertSampleAddress();
        insertSampleUser();
        int type = 3; // fragile package
        BigDecimal weight = BigDecimal.valueOf(2.531);
        int rowId = packageOperations.insertPackage(idAddress, idAddress, sampleUserUserName, type, weight);
        Assert.assertNotEquals(-1, rowId);
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
        BigDecimal expectedPrice = BigDecimal.valueOf(350).add(BigDecimal.valueOf(500).multiply(weight));
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        Assert.assertEquals(0, expectedPrice.compareTo(actualPrice));
    }
}