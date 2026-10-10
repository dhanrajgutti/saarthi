package com.vehical.model;

public class Vehicle {
    private String vehicleNo;
    private Long ownerId;
    private String vehicleType;
    private String make;
    private String model;
    private String fuelType;
    private String registrationDate;
    private String registrationState;
    private String insuranceStatus;
    private String pucStatus;
    private String rcStatus;
    private String engineNumber;
    private String chassisNumber;
    private Boolean blacklisted;
    private String ownerName;
    private String ownerMobile;
    private String ownerEmail;

    public Vehicle() {}

    // Getters and Setters
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getRegistrationState() { return registrationState; }
    public void setRegistrationState(String registrationState) { this.registrationState = registrationState; }

    public String getInsuranceStatus() { return insuranceStatus; }
    public void setInsuranceStatus(String insuranceStatus) { this.insuranceStatus = insuranceStatus; }

    public String getPucStatus() { return pucStatus; }
    public void setPucStatus(String pucStatus) { this.pucStatus = pucStatus; }

    public String getRcStatus() { return rcStatus; }
    public void setRcStatus(String rcStatus) { this.rcStatus = rcStatus; }

    public String getEngineNumber() { return engineNumber; }
    public void setEngineNumber(String engineNumber) { this.engineNumber = engineNumber; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public Boolean getBlacklisted() { return blacklisted; }
    public void setBlacklisted(Boolean blacklisted) { this.blacklisted = blacklisted; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerMobile() { return ownerMobile; }
    public void setOwnerMobile(String ownerMobile) { this.ownerMobile = ownerMobile; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
