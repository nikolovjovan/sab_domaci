package rs.etf.sab.student.operations;

import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.*;
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
        if (weight.compareTo(BigDecimal.valueOf(0)) <= 0) {
            System.out.println("Invalid package weight!");
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();

        String insQuery = "insert into Package (idAddress, idAddressFrom, idAddressTo, senderUserName, type, weight, " +
                "createTime) values (?, ?, ?, ?, ?, ?, ?)";
        String selQuery = "select top 1 idPackage from Package order by idPackage desc";

        try (PreparedStatement insStmt = conn.prepareStatement(insQuery)) {
            insStmt.setInt(1, idAddressFrom);
            insStmt.setInt(2, idAddressFrom);
            insStmt.setInt(3, idAddressTo);
            insStmt.setString(4, userName);
            insStmt.setInt(5, type);
            insStmt.setBigDecimal(6, weight);
            insStmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
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
        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Package set acceptTime = ?, status = ? where idPackage = ? and status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, 1); // new status = 1 (offer accepted)
            stmt.setInt(3, idPackage);
            stmt.setInt(4, 0); // old status = 0 (request created)
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully accepted the offer for package with primary key: " + idPackage + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to accept the offer for package with primary key: " + idPackage + '!');
        return false;
    }

    @Override
    public boolean rejectAnOffer(int idPackage) {
        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Package set status = ? where idPackage = ? and status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setInt(1, 4); // new status = 4 (offer rejected)
            stmt.setInt(2, idPackage);
            stmt.setInt(3, 0); // old status = 0 (request created)
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully rejected the offer for package with primary key: " + idPackage + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to reject the offer for package with primary key: " + idPackage + '!');
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

        String selQuery = "select idPackage from Package where status in (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, 1); // status = 1 (offer accepted)
            stmt.setInt(2, 2); // status = 2 (package picked up)
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
        if (CommonOperations.cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            // TODO: Check what should be returned in this case...
            return null;
        }

        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        String selQuery = "select idPackage from " +
                "((Package inner join Address on Package.idAddressFrom = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity)" +
                "where City.idCity = ? and Package.status in (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idCity);
            stmt.setInt(2, 1); // status = 1 (offer accepted)
            stmt.setInt(3, 2); // status = 2 (package picked up)
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
    public List<Integer> getAllPackagesCurrentlyAtCity(int idCity) {
        if (CommonOperations.cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            // TODO: Check what should be returned in this case...
            return null;
        }

        Connection conn = DB.getInstance().getConnection();

        List<Integer> list = new ArrayList<>();

        int idStockroom = CommonOperations.getStockroomInCity(idCity);

        String selQuery = "select idPackage from " +
                "((Package inner join Address on Package.idAddress = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity)" +
                "where City.idCity = ? and Package.idAddress in (Package.idAddressFrom, Package.idAddressTo, ?) " +
                "and Package.courierUserName is null and Package.status in (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idCity);
            stmt.setInt(2, idStockroom);
            stmt.setInt(3, 1); // status = 1 (offer accepted)
            stmt.setInt(4, 2); // status = 2 (package picked up)
            stmt.setInt(5, 3); // status = 3 (delivered)
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
    public boolean deletePackage(int idPackage) {
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement("delete from Package where idPackage = ?")) {
            stmt.setInt(1, idPackage);
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully deleted package with primary key: " + idPackage + ".");
                return true;
            } else {
                System.out.println("Package with primary key: " + idPackage + " does not exist!");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete package with primary key: " + idPackage + "!");
        return false;
    }

    @Override
    public boolean changeWeight(int idPackage, BigDecimal weight) {
        if (weight.compareTo(BigDecimal.valueOf(0)) <= 0) {
            System.out.println("Invalid package weight!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Package set weight = ? where idPackage = ? and status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setBigDecimal(1, weight);
            stmt.setInt(2, idPackage);
            stmt.setInt(3, 0); // old status = 0 (request created)
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed the weight of the package with primary key: " + idPackage + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change the weight of the package with primary key: " + idPackage + '!');
        return false;
    }

    @Override
    public boolean changeType(int idPackage, int type) {
        if (type < 0 || type > 3) {
            System.out.println("Invalid package type!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String updQuery = "update Package set type = ? where idPackage = ? and status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setInt(1, type);
            stmt.setInt(2, idPackage);
            stmt.setInt(3, 0); // old status = 0 (request created)
            if (stmt.executeUpdate() == 1) {
                System.out.println("Successfully changed the type of the package with primary key: " + idPackage + '.');
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to change the type of the package with primary key: " + idPackage + '!');
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
        return -1;
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
        Connection conn = DB.getInstance().getConnection();

        String selQuery = "select City.idCity from " +
                "((Package inner join Address on Package.idAddress = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity)" +
                "where Package.idPackage = ? and Package.courierUserName is null";

        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idPackage);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(nj160040_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
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