package rs.etf.sab.student.tests;

import org.junit.*;
import rs.etf.sab.operations.*;
import rs.etf.sab.student.nj160040_AddressOperations;
import rs.etf.sab.student.nj160040_CityOperations;
import rs.etf.sab.student.nj160040_GeneralOperations;

import java.util.List;
import java.util.Random;

public class AddressOperationsTest {

    private GeneralOperations generalOperations;
    private AddressOperations addressOperations;
    private CityOperations cityOperations;

    private int insertCity(String name, String postalCode) {
        int idCity = cityOperations.insertCity(name, postalCode);
        Assert.assertNotEquals(-1, idCity);
        return idCity;
    }

    private int insertCity() {
        return insertCity("Belgrade", "11000");
    }

    private int insertDistrict() {
        int idCity = insertCity();
        String street = "Bulevar Kralja Aleksandra";
        int number = 73;
        int xCord = 69;
        int yCord = 420;
        int rowId = addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        Assert.assertNotEquals(-1, rowId);
        return rowId;
    }

    private void checkTwoSameAddresses(int rowIdValid, int rowIdInvalid) {
        Assert.assertNotEquals(-1, rowIdValid);
        Assert.assertEquals(-1, rowIdInvalid);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowIdValid));
    }

    @Before
    public void setUp() {
        generalOperations = nj160040_GeneralOperations.getInstance();
        Assert.assertNotNull(generalOperations);
        addressOperations = nj160040_AddressOperations.getInstance();
        Assert.assertNotNull(addressOperations);
        cityOperations = nj160040_CityOperations.getInstance();
        Assert.assertNotNull(cityOperations);
        generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    @Test
    public void insertDistrict_OnlyOne() {
        int rowId = insertDistrict();
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(rowId));
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCityStreetNumberAndCoordinates() {
        int idCity = insertCity();
        String street = "Bulevar Kralja Aleksandra";
        int number = 73;
        int xCord = 69;
        int yCord = 420;
        int rowIdValid = addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        int rowIdInvalid = addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCityStreetAndNumber() {
        int idCity = insertCity();
        String street = "Bulevar Kralja Aleksandra";
        int number = 73;
        int xCord1 = 69;
        int yCord1 = 420;
        int xCord2 = 420;
        int yCord2 = 69;
        int rowIdValid = addressOperations.insertDistrict(street, number, idCity, xCord1, yCord1);
        int rowIdInvalid = addressOperations.insertDistrict(street, number, idCity, xCord2, yCord2);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_TwoAddresses_SameCoordinates() {
        int idCity = insertCity();
        String street = "Bulevar Kralja Aleksandra";
        int number1 = 73;
        int number2 = 57;
        int xCord = 69;
        int yCord = 420;
        int rowIdValid = addressOperations.insertDistrict(street, number1, idCity, xCord, yCord);
        int rowIdInvalid = addressOperations.insertDistrict(street, number2, idCity, xCord, yCord);
        checkTwoSameAddresses(rowIdValid, rowIdInvalid);
    }

    @Test
    public void insertDistrict_MultipleAddresses_SameCity() {
        int idCity = insertCity();
        String street1 = "Bulevar Kralja Aleksandra";
        String street2 = "Knez Mihajlova";
        int number1 = 73;
        int number2 = 23;
        int xCord1 = 69;
        int xCord2 = 79;
        int yCord1 = 420;
        int yCord2 = 52;
        int rowId1 = addressOperations.insertDistrict(street1, number1, idCity, xCord1, yCord1);
        int rowId2 = addressOperations.insertDistrict(street2, number2, idCity, xCord2, yCord2);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(rowId1));
        Assert.assertTrue(list.contains(rowId2));
    }

    @Test
    public void insertDistrict_MultipleAddresses_DifferentCity() {
        int idCity1 = insertCity();
        int idCity2 = insertCity("New York", "159489");
        String street1 = "Bulevar Kralja Aleksandra";
        String street2 = "Wall Street";
        int number1 = 73;
        int number2 = 789;
        int xCord1 = 69;
        int xCord2 = -4897;
        int yCord1 = 420;
        int yCord2 = -89;
        int rowId1 = addressOperations.insertDistrict(street1, number1, idCity1, xCord1, yCord1);
        int rowId2 = addressOperations.insertDistrict(street2, number2, idCity2, xCord2, yCord2);
        List<Integer> list1 = addressOperations.getAllDistrictsFromCity(idCity1);
        Assert.assertEquals(1, list1.size());
        Assert.assertTrue(list1.contains(rowId1));
        List<Integer> list2 = addressOperations.getAllDistrictsFromCity(idCity2);
        Assert.assertEquals(1, list2.size());
        Assert.assertTrue(list2.contains(rowId2));
    }

    @Test
    public void deleteDistrict_WithId_OnlyOne() {
        int rowId = insertDistrict();
        Assert.assertTrue(addressOperations.deleteDistrict(rowId));
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
        insertDistrict();
        Assert.assertEquals(1, addressOperations.deleteDistricts("Bulevar Kralja Aleksandra", 73));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteDistricts_WithStreetAndNumber_MultipleAddresses() {
        insertDistrict();
        int idCity = insertCity("Novi Sad", "21000");
        String street = "Bulevar Kralja Aleksandra";
        int number = 73;
        int xCord = 48;
        int yCord = 35;
        int rowId = addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        Assert.assertNotEquals(-1, rowId);
        List<Integer> list = addressOperations.getAllDistricts();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(2, addressOperations.deleteDistricts(street, number));
    }

    @Test
    public void deleteDistricts_WithStreetAndNumber_NotExisting() {
        String street = "Bulevar Kralja Aleksandra";
        int number = 73;
        Assert.assertEquals(0, addressOperations.deleteDistricts(street, number));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_OnlyOne() {
        insertDistrict();
        int idCity = 1;
        Assert.assertEquals(1, addressOperations.deleteAllAddressesFromCity(idCity));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_MultipleAddresses() {
        insertDistrict();
        int idCity = 1;
        String street = "Knez Mihajlova";
        int number = 23;
        int xCord = 79;
        int yCord = 52;
        addressOperations.insertDistrict(street, number, idCity, xCord, yCord);
        Assert.assertEquals(2, addressOperations.deleteAllAddressesFromCity(1));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }

    @Test
    public void deleteAllAddressesFromCity_NotExisting() {
        int idCity = 1;
        Assert.assertEquals(0, addressOperations.deleteAllAddressesFromCity(idCity));
        Assert.assertEquals(0, addressOperations.getAllDistricts().size());
    }
}