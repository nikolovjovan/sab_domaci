package rs.etf.sab.student;

import rs.etf.sab.operations.UserOperations;

import java.util.List;

public class nj160040_UserOperations implements UserOperations {

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
}
