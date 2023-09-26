package com.business.www.serviceinterface;

import java.util.List;

import com.business.www.entity.Item;

public interface Iteminter {

	Item saveItem(Item item);

	Item findItemById(int itemId);

	void deleteItem(Item item);

	Item updateItem(Item item);

	List<Item> findAllItems();
}
