package rs.etf.sab.student.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.student.nj160040_AddressOperations;
import rs.etf.sab.student.nj160040_CityOperations;
import rs.etf.sab.student.nj160040_GeneralOperations;
import rs.etf.sab.student.nj160040_UserOperations;

public class UserOperationsTest {

    private GeneralOperations generalOperations;
    private CityOperations cityOperations;
    private AddressOperations addressOperations;
    private UserOperations userOperations;

    private void insertSampleAddress() {
        int idCity = cityOperations.insertCity("Belgrade", "11000");
        Assert.assertNotEquals(-1, idCity);
        int idAddress = addressOperations.insertDistrict("Bulevar Kralja Aleksandra", 73, idCity, 69, 420);
        Assert.assertNotEquals(-1, idAddress);
    }

    @Before
    public void setUp() {
        generalOperations = nj160040_GeneralOperations.getInstance();
        Assert.assertNotNull(generalOperations);
        cityOperations = nj160040_CityOperations.getInstance();
        Assert.assertNotNull(cityOperations);
        addressOperations = nj160040_AddressOperations.getInstance();
        Assert.assertNotNull(addressOperations);
        userOperations = nj160040_UserOperations.getInstance();
        Assert.assertNotNull(userOperations);
        generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        generalOperations.eraseAll();
    }

    @Test
    public void insertUser() {
        insertSampleAddress();
        String username = "crno.dete";
        String firstName = "Svetislav";
        String lastName = "Kisprdilov";
        String password = "Sisatovac123@";
        Assert.assertTrue(this.userOperations.insertUser(username, firstName, lastName, password));
    }

    @Test
    public void declareAdmin() {
        insertSampleAddress();
        String username = "rope";
        String firstName = "Pero";
        String lastName = "Simic";
        String password = "Tralalalala123%";
        this.userOperations.insertUser(username, firstName, lastName, password);
        Assert.assertEquals(0L, (long)this.userOperations.declareAdmin(username));
    }

    @Test
    public void declareAdmin_NoSuchUser() {
        Assert.assertEquals(2L, (long)this.userOperations.declareAdmin("Nana"));
    }

    @Test
    public void declareAdmin_AlreadyAdmin() {
        insertSampleAddress();
        String username = "rope";
        String firstName = "Pero";
        String lastName = "Simic";
        String password = "Tralalalala123%";
        this.userOperations.insertUser(username, firstName, lastName, password);
        this.userOperations.declareAdmin(username);
        Assert.assertEquals(1L, (long)this.userOperations.declareAdmin(username));
    }

    @Test
    public void getSentPackages_userExisting() {
        insertSampleAddress();
        String username = "rope";
        String firstName = "Pero";
        String lastName = "Simic";
        String password = "Tralalalala123%";
        this.userOperations.insertUser(username, firstName, lastName, password);
        Assert.assertEquals(new Integer(0), this.userOperations.getSentPackages(new String[]{username}));
    }

    @Test
    public void getSentPackages_userNotExisting() {
        String username = "rope";
        Assert.assertEquals(new Integer(-1), this.userOperations.getSentPackages(new String[]{username}));
    }

    @Test
    public void deleteUsers() {
        insertSampleAddress();
        String username1 = "rope";
        String firstName1 = "Pero";
        String lastName1 = "Simic";
        String password1 = "Tralalalala123^";
        this.userOperations.insertUser(username1, firstName1, lastName1, password1);
        String username2 = "rope_2";
        String firstName2 = "Pero";
        String lastName2 = "Simic";
        String password2 = "Tralalalala321$";
        this.userOperations.insertUser(username2, firstName2, lastName2, password2);
        Assert.assertEquals(2L, (long)this.userOperations.deleteUsers(new String[]{username1, username2}));
    }

    @Test
    public void getAllUsers() {
        insertSampleAddress();
        String username1 = "rope";
        String firstName1 = "Pero";
        String lastName1 = "Simic";
        String password1 = "traLalalala221@";
        this.userOperations.insertUser(username1, firstName1, lastName1, password1);
        String username2 = "rope_2";
        String firstName2 = "Pero";
        String lastName2 = "Simic";
        String password2 = "tralalAlala222&";
        this.userOperations.insertUser(username2, firstName2, lastName2, password2);
        Assert.assertEquals(2L, (long)this.userOperations.getAllUsers().size());
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username1));
        Assert.assertTrue(this.userOperations.getAllUsers().contains(username2));
    }
}