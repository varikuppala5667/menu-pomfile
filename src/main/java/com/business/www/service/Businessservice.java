package com.business.www.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.www.entity.Business;
import com.business.www.repository.BusinessRepo;
import com.business.www.serviceinterface.Businessinter;

import jakarta.persistence.EntityNotFoundException;

@Service
public class Businessservice implements Businessinter {

	@Autowired
	private BusinessRepo businessRepository;

	@Override
	public Business saveBusiness(Business business) {
		return businessRepository.save(business);
	}

	@Override
	public Business findBusinessById(String businessId) {
		return businessRepository.findById(businessId).orElse(null);
	}

	@Override
	public List<Business> findAllBusinesses() {
		return businessRepository.findAll();
	}

	@Override
	public void deleteBusiness(Business business) {
		businessRepository.delete(business);
	}

	@Override
	public Business updateBusiness(Business business) {
		return businessRepository.save(business);
	}

	public List<Map<String, Object>> getBusinessTypesAndItemsByBusinessId(String businessId) {
		return businessRepository.findBusinessTypesAndItemsByBusinessId(businessId);
	}

	@Override
	public Optional<Business> updateAbout(String about) {

		return businessRepository.findById(about);
	}

	@Override
	public void deleteBusiness(String businessId) {
		businessRepository.deleteById(businessId);

	}
	
	 public Business updateAboutField(String businessId, String about) {
	        Business existingBusiness = businessRepository.findById(businessId)
	            .orElseThrow(() -> new EntityNotFoundException("Business with id " + businessId + " not found"));

	        existingBusiness.setAbout(about);
	        return businessRepository.save(existingBusiness);
	    }

}
