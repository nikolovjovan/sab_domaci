package rs.etf.sab.student.operations;

import rs.etf.sab.operations.PackageOperations;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class nj160040_PackageOperations implements PackageOperations {

    private static nj160040_PackageOperations instance;

    public static nj160040_PackageOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_PackageOperations();
        }
        return instance;
    }

    @Override
    public int insertPackage(int i, int i1, String s, int i2, BigDecimal bigDecimal) {
        return 0;
    }

    @Override
    public boolean acceptAnOffer(int i) {
        return false;
    }

    @Override
    public boolean rejectAnOffer(int i) {
        return false;
    }

    @Override
    public List<Integer> getAllPackages() {
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        return null;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        return null;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int i) {
        return null;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int i) {
        return null;
    }

    @Override
    public boolean deletePackage(int i) {
        return false;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bigDecimal) {
        return false;
    }

    @Override
    public boolean changeType(int i, int i1) {
        return false;
    }

    @Override
    public Integer getDeliveryStatus(int i) {
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        return null;
    }

    @Override
    public BigDecimal getCurrentLocationOfPackage(int i) {
        return null;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        return null;
    }

    private nj160040_PackageOperations() {}
}