package com.business.www.serviceinterface;

import java.util.List;

import com.business.www.entity.ViewImages;

public interface ViewImagesService {
	
    ViewImages saveImageWithItem(int itemId, byte[] images);
    ViewImages getImageById(long id);
    List<ViewImages> getAllImages();
    void updateImage(long id, byte[] images);
    void deleteImage(long id); 
    
  
}
