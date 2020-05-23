package rs.etf.sab.student.operations;

import rs.etf.sab.operations.VehicleOperations;

import java.math.BigDecimal;
import java.util.List;

public class nj160040_VehicleOperations implements VehicleOperations {

    private static nj160040_VehicleOperations instance;

    public static nj160040_VehicleOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_VehicleOperations();
        }
        return instance;
    }

    @Override
    public boolean insertVehicle(String s, int i, BigDecimal bigDecimal, BigDecimal bigDecimal1) {
        return false;
    }

    @Override
    public int deleteVehicles(String... strings) {
        return 0;
    }

    @Override
    public List<String> getAllVehichles() {
        return null;
    }

    @Override
    public boolean changeFuelType(String s, int i) {
        return false;
    }

    @Override
    public boolean changeConsumption(String s, BigDecimal bigDecimal) {
        return false;
    }

    @Override
    public boolean changeCapacity(String s, BigDecimal bigDecimal) {
        return false;
    }

    private nj160040_VehicleOperations() {}
}