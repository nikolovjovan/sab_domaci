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
}