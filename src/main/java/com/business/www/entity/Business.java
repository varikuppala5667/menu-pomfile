package com.business.www.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "business")
public class Business implements Serializable {

	@Id
	private String businessId;

	@Column(nullable = false)
	private String businessName;

	@Column(nullable = false)
	private String contactpersonName;

	private long phoneNo;

	@Column(nullable = false)
	private String businessType;

	@Column(nullable = false)
	private String emailId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String address;

	private String about;

	@Lob
	@Column(length = 500000)
	private byte[] qrCode;

	private Date registrationDate;

	@Lob
	@Column(columnDefinition="LongBlob")
	private byte[] logoFile;

	@JsonManagedReference
	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BusinessType> types = new ArrayList<>();

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getContactpersonName() {
		return contactpersonName;
	}

	public void setContactpersonName(String contactpersonName) {
		this.contactpersonName = contactpersonName;
	}

	public long getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(long phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public byte[] getQrCode() {
		return qrCode;
	}

	public void setQrCode(byte[] qrCode) {
		this.qrCode = qrCode;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public byte[] getLogo() {
		return logoFile;
	}

	public void setLogo(byte[] logo) {
		this.logoFile = logo;
	}

	public List<BusinessType> getTypes() {
		return types;
	}

	public void setTypes(List<BusinessType> types) {
		this.types = types;
	}

	public Business(String businessId, String businessName, String contactpersonName, long phoneNo, String businessType,
			String emailId, String password, String address, String about, byte[] qrCode, Date registrationDate,
			byte[] logoFile, List<BusinessType> types) {
		super();
		this.businessId = businessId;
		this.businessName = businessName;
		this.contactpersonName = contactpersonName;
		this.phoneNo = phoneNo;
		this.businessType = businessType;
		this.emailId = emailId;
		this.password = password;
		this.address = address;
		this.about = about;
		this.qrCode = qrCode;
		this.registrationDate = registrationDate;
		this.logoFile = logoFile;
		this.types = types;
	}

	public Business() {
		super();
	}

}
