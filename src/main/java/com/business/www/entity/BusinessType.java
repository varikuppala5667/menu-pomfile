package com.business.www.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Type")
public class BusinessType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int typeId;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "businessId")
	private Business business;

	@Column(nullable = false)
	private String typeName;

	@Lob
	@Column(columnDefinition="LongBlob")
	private byte[] typeImage;

	private String notice;

	@JsonManagedReference
	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items;


	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public byte[] getTypeImage() {
		return typeImage;
	}

	public void setTypeImage(byte[] typeImage) {
		this.typeImage = typeImage;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public BusinessType(int typeId, Business business, String typeName, byte[] typeImage, String notice,
			List<Item> items) {
		super();
		this.typeId = typeId;
		this.business = business;
		this.typeName = typeName;
		this.typeImage = typeImage;
		this.notice = notice;
		this.items = items;
	}

	public BusinessType() {
		super();
	}

	
	
	
	
}
