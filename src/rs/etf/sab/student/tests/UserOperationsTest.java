package rs.etf.sab.student.tests;

import org.junit.*;

import static rs.etf.sab.student.utils.TestUtils.*;

public class UserOperationsTest {

    @Before
    public void setUp() {
        generalOperations.eraseAll();
    }

    @AfterClass
    public static void tearDown() {
        generalOperations.eraseAll();
    }

    @Test
    public void insertUser() {
        insertSampleAddress();
        insertSampleUser(); // asserts that insert is successful
    }

    @Test
    public void declareAdmin() {
        insertSampleAddress();
        insertSampleUser();
        Assert.assertFalse(userOperations.declareAdmin(sampleUserUserName));
    }

    @Test
    public void declareAdmin_NoSuchUser() {
        Assert.assertTrue(userOperations.declareAdmin("Nana"));
    }

    @Test
    public void declareAdmin_AlreadyAdmin() {
        insertSampleAddress();
        insertSampleUser();
        userOperations.declareAdmin(sampleUserUserName);
        Assert.assertTrue(userOperations.declareAdmin(sampleUserUserName));
    }

    @Test
    public void getSentPackages_userExisting() {
        insertSampleAddress();
        insertSampleUser();
        Assert.assertEquals(0, userOperations.getSentPackages(sampleUserUserName));
    }

    @Test
    public void getSentPackages_userNotExisting() {
        Assert.assertEquals(-1, userOperations.getSentPackages(sampleUserUserName));
    }

    @Test
    public void deleteUsers() {
        int idAddress = insertSampleAddress();
        insertSampleUser(idAddress);
        String userName2 = sampleUserUserName + '2';
        insertSampleUser(userName2, idAddress);
        Assert.assertEquals(2, userOperations.deleteUsers(sampleUserUserName, userName2));
    }

    @Test
    public void getAllUsers() {
        int idAddress = insertSampleAddress();
        insertSampleUser(idAddress);
        String userName2 = sampleUserUserName + '2';
        insertSampleUser(userName2, idAddress);
        Assert.assertEquals(2, userOperations.getAllUsers().size());
        Assert.assertTrue(userOperations.getAllUsers().contains(sampleUserUserName));
        Assert.assertTrue(userOperations.getAllUsers().contains(userName2));
    }
}