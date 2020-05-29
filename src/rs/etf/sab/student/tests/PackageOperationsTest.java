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

    public BigDecimal calculatePriceOfDelivery(int deltaX, int deltaY, int type, BigDecimal weight) {
        BigDecimal price;
        BigDecimal distance = BigDecimal.valueOf(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
        switch (type) {
            case 0: price = BigDecimal.valueOf(115).multiply(distance); break;
            case 1: price = BigDecimal.valueOf(175).add(BigDecimal.valueOf(100).multiply(weight)).multiply(distance); break;
            case 2: price = BigDecimal.valueOf(250).add(BigDecimal.valueOf(100).multiply(weight)).multiply(distance); break;
            case 3: price = BigDecimal.valueOf(350).add(BigDecimal.valueOf(500).multiply(weight)).multiply(distance); break;
            default: price = BigDecimal.valueOf(-1); // invalid price
        }
        return price;
    }

    public void assertPricesSame(BigDecimal expectedPrice, BigDecimal actualPrice) {
        Assert.assertEquals(-1, actualPrice.compareTo(expectedPrice.multiply(BigDecimal.valueOf(1.05))));
        Assert.assertEquals(1, actualPrice.compareTo(expectedPrice.multiply(BigDecimal.valueOf(0.95))));
    }

    @Test
    public void insertPackage() {
        int rowId = insertSamplePackage();
        Assert.assertEquals(1, packageOperations.getAllPackages().size());
        Assert.assertTrue(packageOperations.getAllPackages().contains(rowId));
    }

    @Test
    public void acceptAnOffer() {
        int rowId = insertSamplePackage();
        Assert.assertEquals(0, packageOperations.getDeliveryStatus(rowId));
        Assert.assertTrue(packageOperations.acceptAnOffer(rowId));
        Assert.assertEquals(1, packageOperations.getDeliveryStatus(rowId));
        Assert.assertFalse(packageOperations.acceptAnOffer(rowId));
        Assert.assertEquals(1, packageOperations.getDeliveryStatus(rowId));
    }

    @Test
    public void rejectAnOffer() {
        int rowId = insertSamplePackage();
        Assert.assertEquals(0, packageOperations.getDeliveryStatus(rowId));
        Assert.assertTrue(packageOperations.rejectAnOffer(rowId));
        Assert.assertEquals(4, packageOperations.getDeliveryStatus(rowId));
        Assert.assertFalse(packageOperations.rejectAnOffer(rowId));
        Assert.assertEquals(4, packageOperations.getDeliveryStatus(rowId));
    }

    @Test
    public void changeWeight() {
        int rowId = insertSamplePackage();
        Assert.assertEquals(0, packageOperations.getDeliveryStatus(rowId));
        BigDecimal weight = samplePackageWeight;
        BigDecimal expectedPrice = calculatePriceOfDelivery(samplePackageDeltaX, samplePackageDeltaY,
                samplePackageType, weight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
        weight = weight.add(BigDecimal.valueOf(2.64));
        Assert.assertTrue(packageOperations.changeWeight(rowId, weight));
        expectedPrice = calculatePriceOfDelivery(samplePackageDeltaX, samplePackageDeltaY,
                samplePackageType, weight);
        actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }

    @Test
    public void changeType() {
        int rowId = insertSamplePackage();
        Assert.assertEquals(0, packageOperations.getDeliveryStatus(rowId));
        int type = samplePackageType;
        BigDecimal expectedPrice = calculatePriceOfDelivery(samplePackageDeltaX, samplePackageDeltaY,
                type, samplePackageWeight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
        type = (samplePackageType + 1) % 4;
        Assert.assertTrue(packageOperations.changeType(rowId, type));
        expectedPrice = calculatePriceOfDelivery(samplePackageDeltaX, samplePackageDeltaY,
                type, samplePackageWeight);
        actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }

    @Test
    public void getPriceOfDelivery_SmallPackage() {
        int deltaX = 6;
        int deltaY = -3;
        int type = 0; // small package
        BigDecimal weight = BigDecimal.valueOf(0.57);
        int rowId = insertSamplePackage(deltaX, deltaY, type, weight);
        BigDecimal expectedPrice = calculatePriceOfDelivery(deltaX, deltaY, type, weight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }

    @Test
    public void getPriceOfDelivery_StandardPackage() {
        int deltaX = 4;
        int deltaY = -2;
        int type = 1; // standard package
        BigDecimal weight = BigDecimal.valueOf(1.926);
        int rowId = insertSamplePackage(deltaX, deltaY, type, weight);
        BigDecimal expectedPrice = calculatePriceOfDelivery(deltaX, deltaY, type, weight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }

    @Test
    public void getPriceOfDelivery_NonStandardPackage() {
        int deltaX = -9;
        int deltaY = -2;
        int type = 2; // package with non-standard dimensions
        BigDecimal weight = BigDecimal.valueOf(7.592);
        int rowId = insertSamplePackage(deltaX, deltaY, type, weight);
        BigDecimal expectedPrice = calculatePriceOfDelivery(deltaX, deltaY, type, weight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }

    @Test
    public void getPriceOfDelivery_FragilePackage() {
        int deltaX = 0;
        int deltaY = 4;
        int type = 3; // fragile package
        BigDecimal weight = BigDecimal.valueOf(2.531);
        int rowId = insertSamplePackage(deltaX, deltaY, type, weight);
        BigDecimal expectedPrice = calculatePriceOfDelivery(deltaX, deltaY, type, weight);
        BigDecimal actualPrice = packageOperations.getPriceOfDelivery(rowId);
        assertPricesSame(expectedPrice, actualPrice);
    }
}