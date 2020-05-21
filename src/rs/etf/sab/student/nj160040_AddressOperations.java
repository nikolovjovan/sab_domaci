package rs.etf.sab.student;

import rs.etf.sab.operations.AddressOperations;

import java.util.List;

public class nj160040_AddressOperations implements AddressOperations {

    @Override
    public int insertDistrict(String s, int i, int i1, int i2, int i3) {
        return 0;
    }

    @Override
    public int deleteDistricts(String s, int i) {
        return 0;
    }

    @Override
    public boolean deleteDistrict(int i) {
        return false;
    }

    @Override
    public int deleteAllAddressesFromCity(int i) {
        return 0;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int i) {
        return null;
    }

    @Override
    public List<Integer> getAllDistricts() {
        return null;
    }
}
