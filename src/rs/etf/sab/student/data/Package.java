package rs.etf.sab.student.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Package {

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
}