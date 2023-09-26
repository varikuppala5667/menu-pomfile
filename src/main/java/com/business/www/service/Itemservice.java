package com.business.www.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.www.entity.Item;
import com.business.www.repository.ItemRepo;
import com.business.www.serviceinterface.Iteminter;

@Service
public class Itemservice implements Iteminter {

	 @Autowired
	    private ItemRepo itemRepo;

	    @Override
	    public Item saveItem(Item item) {
	        return itemRepo.save(item);
	    }

	    @Override
	    public Item findItemById(int itemId) {
	        return itemRepo.findById(itemId).orElse(null);
	    }

	    @Override
	    public void deleteItem(Item item) {
	        itemRepo.delete(item);
	    }

	    @Override
	    public Item updateItem(Item item) {
	        return itemRepo.save(item);
	    }
	    
	    @Override
	    public List<Item> findAllItems() {
	        return itemRepo.findAll();
	    }

}
