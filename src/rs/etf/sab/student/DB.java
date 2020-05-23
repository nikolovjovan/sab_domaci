package rs.etf.sab.student;

import java.sql.*;
import java.util.logging.*;

public class DB {

    private static final String server = "localhost";
    private static final String database = "nj160040";
    private static final int port = 1433;

    private static final String connectionURL =
            "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database + ";integratedSecurity=true";

    private static DB db = null;

    private Connection connection;

    public static DB getInstance() {
        if (db == null) {
            db = new DB();
        }
        return db;
    }

    public Connection getConnection() {
        return connection;
    }

    private DB() {
        try {
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String generateColumnValueList(Object[] values) {
        if (values == null || values.length == 0) return null;

        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < values.length - 1; i++) {
            listBuilder.append('\'').append(values[i]).append("', ");
        }
        listBuilder.append('\'').append(values[values.length - 1]).append('\'');

        return listBuilder.toString();
    }

}