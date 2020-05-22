package rs.etf.sab.student;

import rs.etf.sab.operations.UserOperations;

import java.util.List;

public class nj160040_UserOperations implements UserOperations {

    private static nj160040_UserOperations instance;

    public static nj160040_UserOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_UserOperations();
        }
        return instance;
    }

    @Override
    public boolean insertUser(String s, String s1, String s2, String s3) {
        return false;
    }

    @Override
    public int declareAdmin(String s) {
        return 0;
    }

    @Override
    public Integer getSentPackages(String... strings) {
        return null;
    }

    @Override
    public int deleteUsers(String... strings) {
        return 0;
    }

    @Override
    public List<String> getAllUsers() {
        return null;
    }

    private nj160040_UserOperations() {}
}