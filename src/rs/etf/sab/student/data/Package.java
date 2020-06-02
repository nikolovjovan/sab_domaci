package rs.etf.sab.student.data;

import rs.etf.sab.student.operations.CommonOperations;
import rs.etf.sab.student.utils.DB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Package {

    // Table attributes
    private int idPackage;
    private int type;
    private int idAddressFrom;
    private int idAddressTo;
    private int idAddress;
    private int status;
    private Timestamp acceptTime;
    private BigDecimal weight;
    private BigDecimal price;
    private String senderUserName;
    private String courierUserName;

    // Added fields
    private int pickupStopNumber; // stop number for package pickup
    private int deliveryStopNumber; // stop number for package delivery

    public int getIdPackage() {
        return idPackage;
    }

    public void setIdPackage(int idPackage) {
        this.idPackage = idPackage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIdAddressFrom() {
        return idAddressFrom;
    }

    public void setIdAddressFrom(int idAddressFrom) {
        this.idAddressFrom = idAddressFrom;
    }

    public int getIdAddressTo() {
        return idAddressTo;
    }

    public void setIdAddressTo(int idAddressTo) {
        this.idAddressTo = idAddressTo;
    }

    public int getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(int idAddress) {
        this.idAddress = idAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Timestamp acceptTime) {
        this.acceptTime = acceptTime;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getCourierUserName() {
        return courierUserName;
    }

    public void setCourierUserName(String courierUserName) {
        this.courierUserName = courierUserName;
    }

    public int getPickupStopNumber() {
        return pickupStopNumber;
    }

    public void setPickupStopNumber(int pickupStopNumber) {
        this.pickupStopNumber = pickupStopNumber;
    }

    public int getDeliveryStopNumber() {
        return deliveryStopNumber;
    }

    public void setDeliveryStopNumber(int deliveryStopNumber) {
        this.deliveryStopNumber = deliveryStopNumber;
    }

    public static String getPackageSelectAttributes() {
        // TODO: Reduce this once DriveOperations are implemented...
        return "Package.idPackage, Package.type, Package.idAddressFrom, Package.idAddressTo, Package.idAddress, " +
                "Package.status, Package.acceptTime, Package.weight, Package.price, " +
                "Package.senderUserName, Package.courierUserName";
    }

    public static Package getPackageFromResultSet(ResultSet rs) throws SQLException {
        Package p = new Package();
        p.setIdPackage(rs.getInt(1));
        p.setType(rs.getInt(2));
        p.setIdAddressFrom(rs.getInt(3));
        p.setIdAddressTo(rs.getInt(4));
        p.setIdAddress(rs.getInt(5));
        p.setStatus(rs.getInt(6));
        p.setAcceptTime(rs.getTimestamp(7));
        p.setWeight(rs.getBigDecimal(8));
        p.setPrice(rs.getBigDecimal(9));
        p.setSenderUserName(rs.getString(10));
        p.setCourierUserName(rs.getString(11));
        return p;
    }

    public static List<Package> getNonPickedUpPackagesInCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        List<Package> list = new ArrayList<>();
        // TODO: Check if ordering should be by createTime or acceptTime
        String selQuery = "select " + getPackageSelectAttributes() + " from " +
                "(Package inner join Address on Package.idAddress = Address.idAddress) " +
                "where Address.idCity = ? and Package.status = ? and " +
                "idPackage not in (select idPackage from IsPickingUp) order by acceptTime asc";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idCity);
            stmt.setInt(2, 1); // status = 1 (offer accepted)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getPackageFromResultSet(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static List<Package> getPackagesForDelivery(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        List<Package> list = new ArrayList<>();
        String selQuery = "select " + getPackageSelectAttributes() + " from " +
                "(IsDelivering inner join Package on IsDelivering.idPackage = Package.idPackage) " +
                "where IsDelivering.userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getPackageFromResultSet(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static boolean setPackagesForDelivery(String courierUserName, List<Package> packagesToPickup,
                                                 int numberOfPackagesForDelivery) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsDelivering " +
                "(userName, idPackage, stopNumber) values (?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            for (int i = 0; i < numberOfPackagesForDelivery; i++) {
                Package p = packagesToPickup.get(i);
                stmt.setInt(2, p.getIdPackage());
                stmt.setInt(3, p.getDeliveryStopNumber());
                if (stmt.executeUpdate() != 1) return false;
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void clearPackagesForDelivery(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("delete from IsDelivering where userName = ?")) {
            stmt.setString(1, courierUserName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean setPackagesForPickup(String courierUserName, List<Package> packagesToPickup) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("insert into IsPickingUp " +
                "(userName, idPackage, stopNumber) values (?, ?, ?)")) {
            stmt.setString(1, courierUserName);
            for (Package p : packagesToPickup) {
                stmt.setInt(2, p.getIdPackage());
                stmt.setInt(3, p.getPickupStopNumber());
                if (stmt.executeUpdate() != 1) return false;
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void clearPackagesForPickup(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("delete from IsPickingUp where userName = ?")) {
            stmt.setString(1, courierUserName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<Package> getPackagesForPickupOrDeliveryAtStop(String courierUserName, int stopNumber, boolean pickup) {
        Connection conn = DB.getInstance().getConnection();
        List<Package> list = new ArrayList<>();
        String tableName = pickup ? "IsPickingUp" : "IsDelivering";
        String selQuery = "select " + getPackageSelectAttributes() + " from " +
                "(" + tableName + " inner join Package on " + tableName + ".idPackage = Package.idPackage) " +
                "where " + tableName + ".userName = ? and " + tableName +".stopNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, courierUserName);
            stmt.setInt(2, stopNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getPackageFromResultSet(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static List<Package> getPackagesForPickupAtStop(String courierUserName, int stopNumber) {
        return getPackagesForPickupOrDeliveryAtStop(courierUserName, stopNumber, true);
    }

    public static List<Package> getPackagesForDeliveryAtStop(String courierUserName, int stopNumber) {
        return getPackagesForPickupOrDeliveryAtStop(courierUserName, stopNumber, false);
    }
}