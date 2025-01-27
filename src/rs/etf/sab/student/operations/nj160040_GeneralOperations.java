package rs.etf.sab.student.operations;

import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.student.utils.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_GeneralOperations implements GeneralOperations {

    private static nj160040_GeneralOperations instance;

    public static nj160040_GeneralOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_GeneralOperations();
        }
        return instance;
    }

    private static final String[] tables = { "Stop", "IsDelivering", "IsPickingUp", "IsDriving", "Drove", "Vehicle", "Package", "Courier", "User", "Stockroom", "Address", "City" };
    private static final String[] tablesWithIdentityColumn = { "Package", "Stockroom", "Address", "City" };

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            // Remove all rows from all tables.
            for (String tableName : tables) {
                stmt.executeUpdate("delete from [" + tableName + ']');
            }
            // Reseed identities to 0 (in tables that have them).
            for (String tableName : tablesWithIdentityColumn) {
                stmt.executeUpdate("dbcc checkident ('[" + tableName + "]', reseed, 0)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private nj160040_GeneralOperations() {}
}