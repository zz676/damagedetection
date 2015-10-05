package com.aig.science.damagedetection.models;

public class PolicyMaster {
	
	private String firstName,lastName,phoneNo,emailId,address,make,model,color,licenseNo,vin,policyNo;
	
	
	public PolicyMaster(String firstName,String lastName,String phoneNo,String emailId,String address,String make,String model,String color,String licenseNo,String vin,String policyNo){
		this.firstName=firstName;
		this.lastName=lastName;
		this.phoneNo=phoneNo;
		this.emailId = emailId;
		this.address=address;
		this.make=make;
		this.model=model;
		this.color=color;
		this.licenseNo=licenseNo;
		this.vin=vin;
		this.policyNo=policyNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

}
