package com.business.www.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.www.entity.BusinessType;
import com.business.www.repository.TypeRepo;
import com.business.www.serviceinterface.Typeinter;

@Service
public class Typeservice implements Typeinter {

	@Autowired
	private TypeRepo typeRepo;

	@Override
	public BusinessType saveType(BusinessType type) {
		return typeRepo.save(type);
	}

	@Override
	public BusinessType findTypeById(int typeId) {
		return typeRepo.findById(typeId).orElse(null);
	}

	@Override
	public List<BusinessType> findAllTypes() {
		return typeRepo.findAll();
	}

	@Override
	public void deleteType(BusinessType type) {
		typeRepo.delete(type);
	}

	@Override
	public BusinessType updateType(BusinessType type) {
		return typeRepo.save(type);
	}

	@Override
	public List<BusinessType> findByBusinessId(String businessId) {
		return typeRepo.findByBusinessId(businessId);
	}

	public List<String> getAllTypeNames() {
		return typeRepo.findAllTypeNames();
	}

}
