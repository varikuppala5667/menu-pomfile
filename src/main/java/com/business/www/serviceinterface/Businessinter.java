package com.business.www.serviceinterface;

import java.util.List;
import java.util.Optional;

import com.business.www.entity.Business;

public interface Businessinter {

	  Business saveBusiness(Business business);
	    Business findBusinessById(String businessId);
	    List<Business> findAllBusinesses();
	    void deleteBusiness(Business business);
	    Business updateBusiness(Business business);
	    Optional<Business> updateAbout(String about);
		void deleteBusiness(String businessId);

}
