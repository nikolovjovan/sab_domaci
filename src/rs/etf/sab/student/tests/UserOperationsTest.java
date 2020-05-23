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
        Assert.assertEquals(0, userOperations.declareAdmin(sampleUserUserName));
    }

    @Test
    public void declareAdmin_NoSuchUser() {
        Assert.assertEquals(2, userOperations.declareAdmin("Nana"));
    }

    @Test
    public void declareAdmin_AlreadyAdmin() {
        insertSampleAddress();
        insertSampleUser();
        userOperations.declareAdmin(sampleUserUserName);
        Assert.assertEquals(1, userOperations.declareAdmin(sampleUserUserName));
    }

    @Test
    public void getSentPackages_userExisting() {
        insertSampleAddress();
        insertSampleUser();
        Assert.assertEquals(new Integer(0), userOperations.getSentPackages(sampleUserUserName));
    }

    @Test
    public void getSentPackages_userNotExisting() {
        Assert.assertEquals(new Integer(-1), userOperations.getSentPackages(sampleUserUserName));
    }

    @Test
    public void deleteUsers() {
        insertSampleAddress();
        insertSampleUser();
        String userName2 = sampleUserUserName + '2';
        insertSampleUser(userName2);
        Assert.assertEquals(2, userOperations.deleteUsers(sampleUserUserName, userName2));
    }

    @Test
    public void getAllUsers() {
        insertSampleAddress();
        insertSampleUser();
        String userName2 = sampleUserUserName + '2';
        insertSampleUser(userName2);
        Assert.assertEquals(2, userOperations.getAllUsers().size());
        Assert.assertTrue(userOperations.getAllUsers().contains(sampleUserUserName));
        Assert.assertTrue(userOperations.getAllUsers().contains(userName2));
    }
}