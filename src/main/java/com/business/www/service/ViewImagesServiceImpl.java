package com.business.www.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.www.entity.Item;
import com.business.www.entity.ViewImages;
import com.business.www.repository.ItemRepo;
import com.business.www.repository.ViewImageRepo;
import com.business.www.serviceinterface.ViewImagesService;

@Service
public class ViewImagesServiceImpl implements ViewImagesService{
	@Autowired
	 private ViewImageRepo viewImagesRepository;
	@Autowired
	    private ItemRepo itemRepository;

	    @Override
	    public ViewImages saveImageWithItem(int itemId, byte[] images) {
	        Optional<Item> optionalItem = itemRepository.findById(itemId);
	        if (optionalItem.isPresent()) {
	            Item item = optionalItem.get();
	            ViewImages viewImages = new ViewImages();
	            viewImages.setImages(images);
	            viewImages.setItem(item);
	            return viewImagesRepository.save(viewImages);
	        } else {
	            throw new IllegalArgumentException("Item with ID " + itemId + " not found.");
	        }
	    }

	    @Override
	    public ViewImages getImageById(long id) {
	        return viewImagesRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("ViewImages with ID " + id + " not found."));
	    }

	    @Override
	    public List<ViewImages> getAllImages() {
	        return viewImagesRepository.findAll();
	    }

	    @Override
	    public void updateImage(long id, byte[] images) {
	        ViewImages viewImages = viewImagesRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("ViewImages with ID " + id + " not found."));
	        viewImages.setImages(images);
	        viewImagesRepository.save(viewImages);
	    }

	    @Override
	    public void deleteImage(long id) {
	        viewImagesRepository.deleteById(id);
	    }

		public int countImagesByItemIsd(int itemId) {
			return viewImagesRepository.countByItemId(itemId) ;
		}

	
	}

