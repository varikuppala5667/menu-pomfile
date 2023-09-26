package com.business.www.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class ViewImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	
	@Lob
	@Column(columnDefinition = "LongBlob")
	private byte[] images;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name="item_id")
	private Item item;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public byte[] getImages() {
		return images;
	}

	public void setImages(byte[] images) {
		this.images = images;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public ViewImages() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ViewImages(long id, byte[] images, Item item) {
		super();
		Id = id;
		this.images = images;
		this.item = item;
	}
}
