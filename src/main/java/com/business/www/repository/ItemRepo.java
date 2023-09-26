package com.business.www.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.business.www.entity.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer> {

	Optional<Item> findById(int itemId);

}
