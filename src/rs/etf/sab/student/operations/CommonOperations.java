package rs.etf.sab.student.operations;

import rs.etf.sab.student.data.Address;
import rs.etf.sab.student.data.Package;
import rs.etf.sab.student.data.Vehicle;
import rs.etf.sab.student.utils.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommonOperations {

    public static final int userTypeDefault = 0b00;
    public static final int userTypeCourierFlag = 0b01;
    public static final int userTypeAdminFlag = 0b10;

    public static boolean isNullOrEmpty(String s, String paramName) {
        if (s == null || s.isEmpty()) {
            System.out.print("Null or empty " + paramName + "! ");
            return true;
        }
        return false;
    }

    public static int getAddressCity(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idCity from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Address does not exist or query failed
        return -1;
    }

    public static int getStockroomAddress(int idStockroom) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from Stockroom where idStockroom = ?")) {
            stmt.setInt(1, idStockroom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Stockroom does not exist or query failed
        return -1;
    }

    public static int getUserAddress(String userName) {
        Connection conn = DB.getInstance().getConnection();
        if (isNullOrEmpty(userName, "user name")) return -1;
        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from [User] where userName = ?")) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        // User does not exist or query failed
        return -1;
    }

    public static int getUserType(String userName) {
        Connection conn = DB.getInstance().getConnection();
        if (isNullOrEmpty(userName, "user name")) return -1;
        try (PreparedStatement stmt = conn.prepareStatement("select type from [User] where userName = ?")) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        // User does not exist or query failed
        return -1;
    }

    public static int getUserTypeAndCheckIfCourier(String userName) {
        int type = CommonOperations.getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return -1;
        }
        if ((type & userTypeCourierFlag) != 0) {
            System.out.println("User with user name '" + userName + "' is already a courier.");
            return -1;
        }
        return type;
    }

    public static int getUserTypeAndCheckIfNotCourier(String userName) {
        int type = CommonOperations.getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return -1;
        }
        if ((type & CommonOperations.userTypeCourierFlag) == 0) {
            System.out.println("User with user name '" + userName + "' is not a courier!");
            return -1;
        }
        return type;
    }

    public static boolean cityNotExist(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idCity from City where idCity = ?")) {
            stmt.setInt(1, idCity);
            return !stmt.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean addressNotExist(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idAddress from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            return !stmt.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean stockroomNotExist(int idStockroom) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idStockroom from Stockroom where idStockroom = ?")) {
            stmt.setInt(1, idStockroom);
            return !stmt.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean userNotExist(String userName) {
        return getUserType(userName) == -1;
    }

    public static int getStockroomInCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        // Check if city with specified primary key exists...
        if (cityNotExist(idCity)) {
            System.out.println("City with primary key: " + idCity + " does not exist!");
            return -1;
        }
        try (PreparedStatement stmt = conn.prepareStatement("select idStockroom from " +
                "((Stockroom inner join Address on Stockroom.idAddress = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity)" +
                "where City.idCity = ?")) {
            stmt.setInt(1, idCity);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static boolean insertCourierOrRequest(String userName, String driversLicenseNumber, boolean request) {
        int type = getUserTypeAndCheckIfCourier(userName);
        if (type == -1) return false;

        Connection conn = DB.getInstance().getConnection();

        String selQuery = "select status from Courier where userName = ? or driversLicenseNumber = ?";
        String insQuery = "insert into Courier (userName, driversLicenseNumber, status) values (?, ?, ?)";

        // Check if courier or courier request with the same user name or driver's license number exists...
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, userName);
            stmt.setString(2, driversLicenseNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int status = rs.getInt(1); // courier status (0 - not driving, 1 - driving, 2 - request)
                System.out.println("Courier " + (status == 2 ? "request " : "") + "with the same user name or " +
                        "driver's license number already exists!");
                return false;
            }
            // If it does not insert a new courier or courier request depending on parameter 'request'.
            PreparedStatement insStmt = conn.prepareStatement(insQuery);
            insStmt.setString(1, userName);
            insStmt.setString(2, driversLicenseNumber);
            insStmt.setInt(3, request ? 2 : 0); // status = 0 (not currently driving) or status = 2 (request)
            if (insStmt.executeUpdate() == 1) {
                if (request) {
                    System.out.println("Successfully inserted a courier request for user with user name '" +
                            userName + "'.");
                } else {
                    PreparedStatement updStmt = conn.prepareStatement("update [User] set type = ? where userName = ?");
                    updStmt.setInt(1, type | userTypeCourierFlag);
                    updStmt.setString(2, userName);
                    if (updStmt.executeUpdate() == 1) {
                        System.out.println("Successfully made user with user name '" + userName + "' a courier.");
                        return true;
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static boolean deleteCourierOrRequest(String userName, boolean request) {
        int type = getUserType(userName);
        if (type == -1) {
            System.out.println("User with user name '" + userName + "' does not exist!");
            return false;
        }

        Connection conn = DB.getInstance().getConnection();

        String delQuery = "delete from Courier where userName = ? and status " + (request ? "= ?" : "!= ?");

        try (PreparedStatement stmt = conn.prepareStatement(delQuery)) {
            stmt.setString(1, userName);
            stmt.setInt(2, 2); // status = 2 (request)
            if (stmt.executeUpdate() == 1) {
                if (request) {
                    System.out.println("Successfully deleted courier request with user name '" + userName + "'.");
                    return true;
                }
                // TODO: If user should be deleted altogether then change this to a delete statement...
                PreparedStatement updStmt = conn.prepareStatement("update [User] set type = ? where userName = ?");
                updStmt.setInt(1, type & ~userTypeCourierFlag);
                updStmt.setString(2, userName);
                if (updStmt.executeUpdate() == 1) {
                    System.out.println("Successfully deleted courier with user name '" + userName + "'.");
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Failed to delete courier " + (request ? "request " : "") +
                "with user name '" + userName + "'.");
        return false;
    }

    public static int getCourierStatus(String courierUserName) {
        if (getUserTypeAndCheckIfNotCourier(courierUserName) == -1) return -1;
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select status from Courier where userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, courierUserName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static boolean setCourierStatus(String courierUserName, int status) {
        if (getUserTypeAndCheckIfNotCourier(courierUserName) == -1) return false;
        Connection conn = DB.getInstance().getConnection();
        String updQuery = "update Courier set status = ? where userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updQuery)) {
            stmt.setInt(1, status);
            stmt.setString(2, courierUserName);
            return stmt.executeUpdate() == 1;
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean isBeingDriven(String licensePlateNumber) {
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select userName from IsDriving where licensePlateNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, licensePlateNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean notInStockroom(String licensePlateNumber) {
        if (isBeingDriven(licensePlateNumber)) return true;
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select idStockroom from Vehicle where licensePlateNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setString(1, licensePlateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rs.getInt(1);
                return rs.wasNull();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static List<Vehicle> getVehiclesInStockroom(int idStockroom) {
        List<Vehicle> list = new ArrayList<>();
        int idAddress = getStockroomAddress(idStockroom);
        if (idAddress == -1) return list;
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select licensePlateNumber, fuelType, fuelConsumption, capacity, idStockroom from Vehicle " +
                "where idStockroom = ? and licensePlateNumber not in (select licensePlateNumber from IsDriving)";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idAddress);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setLicensePlateNumber(rs.getString(1));
                v.setFuelType(rs.getInt(2));
                v.setFuelConsumption(rs.getBigDecimal(3));
                v.setCapacity(rs.getBigDecimal(4));
                v.setIdStockroom(rs.getInt(5));
                list.add(v);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static List<Package> getPackagesInStockroom(int idStockroom) {
        List<Package> list = new ArrayList<>();
        int idAddress = getStockroomAddress(idStockroom);
        if (idAddress == -1) return list;
        Connection conn = DB.getInstance().getConnection();
        String selQuery = "select idPackage, type, idAddressFrom, idAddressTo, idAddress, status, acceptTime, " +
                "weight, price, senderUserName, courierUserName from Package where idAddress = ? and " +
                "idPackage not in (select idPackage from IsPickingUp) order by acceptTime asc";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idAddress);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static List<Package> getNonPickedUpPackagesInCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        List<Package> list = new ArrayList<>();
        String selQuery = "select idPackage, type, idAddressFrom, idAddressTo, Package.idAddress, status, acceptTime, " +
                "weight, price, senderUserName, courierUserName from " +
                "((Package inner join Address on Package.idAddress = Address.idAddress) " +
                "inner join City on Address.idCity = City.idCity) where City.idCity = ? and Package.status = ? and " +
                "idPackage not in (select idPackage from IsPickingUp) order by acceptTime asc";
        try (PreparedStatement stmt = conn.prepareStatement(selQuery)) {
            stmt.setInt(1, idCity);
            stmt.setInt(2, 1); // status = 1 (offer accepted)
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                list.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public static Address getAddressById(int idAddress) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement("select idCity, xCord, yCord from Address where idAddress = ?")) {
            stmt.setInt(1, idAddress);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Address a = new Address();
                a.setIdAddress(idAddress);
                a.setIdCity(rs.getInt(1));
                a.setxCord(rs.getInt(2));
                a.setyCord(rs.getInt(3));
                return a;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommonOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}