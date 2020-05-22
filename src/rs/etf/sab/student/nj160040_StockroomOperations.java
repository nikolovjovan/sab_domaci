package rs.etf.sab.student;

import rs.etf.sab.operations.StockroomOperations;

import java.util.List;

public class nj160040_StockroomOperations implements StockroomOperations {

    private static nj160040_StockroomOperations instance;

    public static nj160040_StockroomOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_StockroomOperations();
        }
        return instance;
    }

    @Override
    public int insertDistrict(int i) {
        return 0;
    }

    @Override
    public boolean deleteDistrict(int i) {
        return false;
    }

    @Override
    public int deleteStockroomFromCity(int i) {
        return 0;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        return null;
    }

    private nj160040_StockroomOperations() {}
}