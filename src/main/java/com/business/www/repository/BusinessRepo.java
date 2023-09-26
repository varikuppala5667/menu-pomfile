package com.business.www.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.business.www.entity.Business;


public interface BusinessRepo extends JpaRepository<Business, String> {

//	
//	@Query(value = "SELECT b.logo,b.about,t.notice,b.address, t.type_id, t.type_Name, i.item_id,i.item_Name ,i.item_image,i.price,i.description "
//	        + "FROM business b "
//	        + "LEFT JOIN type t ON b.business_id = t.business_id "
//	        + "LEFT JOIN items i ON t.type_id = i.type_id "
//	        + "WHERE b.business_id = ?1", nativeQuery = true)
//	List<Map<String, Object>> findBusinessTypesAndItemsByBusinessId(@PathVariable String businessId);

//	@Query(value = "SELECT b.logo,b.about,t.notice,b.address,b.business_name, t.type_id, t.type_Name, i.item_id,i.item_Name ,i.item_image,i.price,i.description "
//			+ "FROM business b " + "LEFT JOIN type t ON b.business_id = t.business_id "
//			+ "LEFT JOIN items i ON t.type_id = i.type_id " + "WHERE b.business_id = ?1", nativeQuery = true)
//	List<Map<String, Object>> findBusinessTypesAndItemsByBusinessId(@PathVariable String businessId);
//
//	@Query(value = "SELECT MAX(b.businessId) FROM Business b")
//	String getLastBusinessId();
	
	
	@Query(value = "SELECT b.logo_file,b.about,t.type_image,t.notice,b.address,b.business_name, t.type_id, t.type_Name, i.item_id,i.item_Name ,i.item_image,i.price,i.description "
	         + "FROM business b "
	         + "LEFT JOIN type t ON b.business_id = t.business_id "
	         + "LEFT JOIN items i ON t.type_id = i.type_id "
	         + "WHERE b.business_id = ?1", nativeQuery = true)
	 List<Map<String, Object>> findBusinessTypesAndItemsByBusinessId(@PathVariable String businessId);

	 
	 
	 @Query(value = "SELECT MAX(b.businessId) FROM Business b")
	   String getLastBusinessId();
	 
	 
	 
	 
	}


