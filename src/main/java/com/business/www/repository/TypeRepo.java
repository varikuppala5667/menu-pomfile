package com.business.www.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.business.www.entity.BusinessType;

@Repository
public interface TypeRepo extends JpaRepository<BusinessType, Integer> {

	@Query("SELECT bt FROM BusinessType bt WHERE bt.business.id = :businessId")
	List<BusinessType> findByBusinessId(@Param("businessId") String businessId);

	@Query("SELECT typeName FROM BusinessType")
	List<String> findAllTypeNames();
}
