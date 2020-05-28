package rs.etf.sab.student.operations;

import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class nj160040_PackageOperations implements PackageOperations {

    private static nj160040_PackageOperations instance;

    public static nj160040_PackageOperations getInstance() {
        if (instance == null) {
            instance = new nj160040_PackageOperations();
        }
        return instance;
    }

    @Override
    public int insertPackage(int idAddressFrom, int idAddressTo, String userName, int type, BigDecimal weight) {
        if (CommonOperations.addressNotExist(idAddressFrom)) {
            System.out.println("Invalid from address!");
            return -1;
        }
        if (CommonOperations.addressNotExist(idAddressTo)) {
            System.out.println("Invalid to address!");
            return -1;
        }
        if (CommonOperations.userNotExist(userName)) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return -1;
        }
        if (type < 0 || type > 3) {
            System.out.println("Invalid package type!");
            return -1;
        }
        if (weight.compareTo(new BigDecimal(0)) <= 0) {
            System.out.println("Invalid package weight!");
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();

        String insQuery = "insert into Package (idAddressFrom, idAddressTo, userName, type, weight, createTime) " +
                "values (?, ?, ?, ?, ?, ?)";
        String selQuery = "select top 1 idPackage from Package order by idPackage desc";

        try (PreparedStatement insStmt = conn.prepareStatement(insQuery)) {
            insStmt.setInt(1, idAddressFrom);
            insStmt.setInt(2, idAddressTo);
            insStmt.setString(3, userName);
            insStmt.setInt(4, type);
            insStmt.setBigDecimal(5, weight);
            insStmt.setDate(6, Date.valueOf(LocalDate.now()));
            if (insStmt.executeUpdate() == 1) {
                PreparedStatement selStmt = conn.prepareStatement(selQuery);
                ResultSet rs = selStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Successfully inserted a package with primary key: " + rs.getInt(1) +
                            " by user with user name '" + userName + "'.");
                    return rs.getInt(1); // idPackage
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to insert a package by user with user name '" + userName + "'!");
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int idPackage) {
        return false;
    }

    @Override
    public boolean rejectAnOffer(int idPackage) {
        return false;
    }

    @Override
    public List<Integer> getAllPackages() {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idPackage from Package")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        if (type < 0 || type > 3) {
            System.out.println("Invalid package type!");
            // TODO: Check what should be returned in this case...
            return null;
        }

        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idPackage from Package where type = ?")) {
            stmt.setInt(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement("select idPackage from Package where status != ?")) {
            stmt.setInt(1, 3); // status = 3 (delivered)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int idCity) {
        return null;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int idCity) {
        return null;
    }

    @Override
    public boolean deletePackage(int idPackage) {
        return false;
    }

    @Override
    public boolean changeWeight(int idPackage, BigDecimal weight) {
        return false;
    }

    @Override
    public boolean changeType(int idPackage, int type) {
        return false;
    }

    @Override
    public int getDeliveryStatus(int idPackage) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("select status from Package where idPackage = ?")) {
            stmt.setInt(1, idPackage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to get delivery status of package with primary key: " + idPackage + '!');
        // TODO: Check what should be returned in this case...
        return 0;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int idPackage) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("select price from Package where idPackage = ?")) {
            stmt.setInt(1, idPackage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to get price of delivery of package with primary key: " + idPackage + '!');
        // TODO: Check what should be returned in this case...
        return null;
    }

    @Override
    public int getCurrentLocationOfPackage(int idPackage) {
        return 0;
    }

    @Override
    public Date getAcceptanceTime(int idPackage) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("select acceptTime from Package where idPackage = ?")) {
            stmt.setInt(1, idPackage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to get acceptance time of package with primary key: " + idPackage + '!');
        // TODO: Check what should be returned in this case...
        return null;
    }

    private nj160040_PackageOperations() {}
}