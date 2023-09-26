package com.business.www.serviceinterface;

import java.util.List;

import com.business.www.entity.BusinessType;

public interface Typeinter {

	BusinessType saveType(BusinessType type);

	BusinessType findTypeById(int typeId);

	List<BusinessType> findAllTypes();

	void deleteType(BusinessType type);

	BusinessType updateType(BusinessType type);

	List<BusinessType> findByBusinessId(String businessId);

}
