package rs.etf.sab.student.data;

import java.math.BigDecimal;

public class Vehicle {

    private String licensePlateNumber;
    private int fuelType;
    private BigDecimal fuelConsumption;
    private BigDecimal capacity;
    private int idStockroom;

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public int getFuelType() {
        return fuelType;
    }

    public void setFuelType(int fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public int getIdStockroom() {
        return idStockroom;
    }

    public void setIdStockroom(int idStockroom) {
        this.idStockroom = idStockroom;
    }
}