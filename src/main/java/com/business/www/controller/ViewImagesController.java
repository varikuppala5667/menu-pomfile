package com.business.www.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.business.www.entity.ViewImages;
import com.business.www.service.ViewImagesServiceImpl;

@RestController
@RequestMapping("/api")
public class ViewImagesController {
	
	@Autowired
	private ViewImagesServiceImpl viewImagesService;

//	@PostMapping("/view/save/{itemId}")
//	public ViewImages saveImageWithItem(@PathVariable int itemId, @RequestParam("file") MultipartFile imageFile)
//	        throws IOException {
//	    byte[] imageBytes = imageFile.getBytes();
//	    return viewImagesService.saveImageWithItem(itemId, imageBytes);
//	}

	
	 @PostMapping("/view/save/{itemId}")
	    public ViewImages saveImageWithItem(@PathVariable int itemId, @RequestParam("file") MultipartFile imageFile)
	            throws IOException {
	        byte[] imageBytes = imageFile.getBytes();
	        
	        // Check the number of images already stored for the given itemId
	        int imageCount = viewImagesService.countImagesByItemIsd(itemId);
	        
	        if (imageCount >= 5) {
	            throw new MaxImagesReachedException("Maximum of 5 images allowed for the item with ID: " + itemId);
	        }
	        
	        return viewImagesService.saveImageWithItem(itemId, imageBytes);
	    }

	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public class MaxImagesReachedException extends RuntimeException {
	        public MaxImagesReachedException(String message) {
	            super(message);
	        }
	    }
	
	
	
	
	
	
	@GetMapping("/view/{id}")
	public ViewImages getImageById(@PathVariable long id) {
		return viewImagesService.getImageById(id);
	}

	@GetMapping("/view/all")
	public List<ViewImages> getAllImages() {
		return viewImagesService.getAllImages();
	}

	@PutMapping("/view/{id}")
	public void updateImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		byte[] imageBytes = imageFile.getBytes();
		viewImagesService.updateImage(id, imageBytes);
	}

	@DeleteMapping("/view/{id}")
	public void deleteImage(@PathVariable long id) {
		viewImagesService.deleteImage(id);
	}
}
