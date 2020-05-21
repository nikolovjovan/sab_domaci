package rs.etf.sab.student;

import rs.etf.sab.operations.GeneralOperations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_GeneralOperations implements GeneralOperations {

    // TODO: Add table names here once they are in the database.
    private static final String[] tables = { "Address", "City" };

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            for (String tableName : tables) {
                stmt.executeUpdate("delete from " + tableName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}