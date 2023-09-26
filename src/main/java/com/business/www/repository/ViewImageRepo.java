package com.business.www.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.business.www.entity.ViewImages;

public interface ViewImageRepo extends JpaRepository<ViewImages, Long>{

    @Query(value = "SELECT COUNT(*) FROM view_images WHERE item_id = ?1", nativeQuery = true)
	int countByItemId(int itemId);

}
