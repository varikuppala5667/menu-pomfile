package com.business.www.entity;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "items")
public class Item  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int itemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	@JsonBackReference
	private BusinessType type;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ViewImages> viewImages;

	@Column(nullable = true)
	private String itemName;

	@Lob
	@Column(columnDefinition="LongBlob")
	private byte[] itemImage;

	@Column(nullable = false)
	private double price;

	private String description;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public BusinessType getType() {
		return type;
	}

	public void setType(BusinessType type) {
		this.type = type;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public byte[] getItemImage() {
		return itemImage;
	}

	public void setItemImage(byte[] itemImage) {
		this.itemImage = itemImage;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<ViewImages> getViewImages() {
		return viewImages;
	}

	public void setViewImages(List<ViewImages> viewImages) {
		this.viewImages = viewImages;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Item() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Item(int itemId, BusinessType type, List<ViewImages> viewImages, String itemName,
			byte[] itemImage, double price, String description) {
		super();
		this.itemId = itemId;
		this.type = type;
		this.viewImages = viewImages;
		this.itemName = itemName;
		this.itemImage = itemImage;
		this.price = price;
		this.description = description;
	}
}
