package com.business.www.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.business.www.entity.Business;
import com.business.www.entity.BusinessType;
import com.business.www.entity.Item;
import com.business.www.repository.BusinessRepo;
import com.business.www.service.Businessservice;
import com.business.www.service.Itemservice;
import com.business.www.service.Typeservice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.coobird.thumbnailator.Thumbnails;

@RestController
@RequestMapping("/my")
public class BusinessController {

	@Autowired
	private Businessservice businessService;

	@Autowired
	private Typeservice typeService;

	@Autowired
	private Itemservice itemService;

	@Autowired
	private BusinessRepo repo;

	@PostMapping("/save")
	public ResponseEntity<Business> saveBusiness(@ModelAttribute Business business,
			@RequestParam(name = "logoFile", required = false) MultipartFile logoFile) {
		String businessName = business.getBusinessName();
		String prefix = businessName.substring(0, 4).toUpperCase();

		// Get the last business ID from the database
		String lastBusinessId = repo.getLastBusinessId();

		// Check if the lastBusinessId is null
		if (lastBusinessId == null) {
			// Set the initial sequence number as 1
			lastBusinessId = prefix + "001";
		} else {
			// Extract the sequence number from the last business ID
			String lastSequenceNumber = lastBusinessId.substring(4);

			// Increment the sequence number
			int newSequenceNumber = Integer.parseInt(lastSequenceNumber) + 1;

			// Format the new sequence number with leading zeros
			String formattedSequenceNumber = String.format("%03d", newSequenceNumber);

			lastBusinessId = prefix + formattedSequenceNumber;
		}

		// Save the generated QR code and business ID for the business
		business.setBusinessId(lastBusinessId);
		// Generate QR code for the business
		String websiteUrl = "http://localhost:4200" + "/view" + "/" + business.getBusinessId();

		int size = 300;
		int borderSize = 1;
		int imageSize = size - (borderSize * 2);
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(websiteUrl, BarcodeFormat.QR_CODE, imageSize, imageSize, hints);
			BufferedImage qrCodeImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = qrCodeImage.createGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, size, size);
			graphics.setColor(Color.BLACK);
			for (int i = 0; i < imageSize; i++) {
				for (int j = 0; j < imageSize; j++) {
					if (bitMatrix.get(i, j)) {
						graphics.fillRect(borderSize + i, borderSize + j, 1, 1);
					}
				}
			}
			graphics.setColor(Color.BLACK);
			graphics.setStroke(new BasicStroke(borderSize));
			graphics.drawRoundRect(borderSize, borderSize, imageSize, imageSize, 20, 20);

			// Convert QR code image to byte array
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(qrCodeImage, "png", out);
			byte[] qrCodeBytes = out.toByteArray();

			business.setQrCode(qrCodeBytes);

			// Save the logo file if provided
			if (logoFile != null && !logoFile.isEmpty()) {
				try {
					byte[] logoBytes = null;
					if (logoFile.getSize() > 250 * 1024) {
						BufferedImage logoImage = ImageIO.read(logoFile.getInputStream());
						ByteArrayOutputStream logoOutputStream = new ByteArrayOutputStream();
						String logoOutputFormat = getImageOutputFormat(logoFile.getContentType());
						Thumbnails.of(logoImage).size(250, 250).outputFormat(logoOutputFormat).outputQuality(1.0)
								.toOutputStream(logoOutputStream);
						logoBytes = logoOutputStream.toByteArray();
					} else {
						// Use the original logo image bytes without compression
						logoBytes = logoFile.getBytes();
					}
					business.setLogo(logoBytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Save the business details in the database
			Business savedBusiness = businessService.saveBusiness(business);
			return new ResponseEntity<>(savedBusiness, HttpStatus.CREATED);

		} catch (Exception e) {
			throw new RuntimeException("Failed to save business");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginBusiness(@RequestBody Map<String, String> loginData) {
		String businessId = loginData.get("businessId");
		String password = loginData.get("password");

		// Find the business details from the database using the business ID
		Business business = businessService.findBusinessById(businessId);

		// If the business exists and the password matches
		if (business != null && password.equals(business.getPassword())) {
			// Generate JWT token with relevant claims
			String jwtSecret = generateSecretKey(); // Generate a dynamic secret key
			String jwtIssuer = generateIssuer(); // Generate a dynamic issuer

			Claims claims = Jwts.claims().setSubject(businessId);
			claims.put("role", "business"); // Example role/permission, modify as needed

			// Add more claims if necessary, e.g., claims.put("permission",
			// "some_permission");

			long expirationTimeInMs = System.currentTimeMillis() + 86400000; // Token expires in 24 hours
			Date expirationDate = new Date(expirationTimeInMs);

			@SuppressWarnings("deprecation")
			String token = Jwts.builder().setClaims(claims).setIssuer(jwtIssuer).setExpiration(expirationDate)
					.signWith(SignatureAlgorithm.HS256, jwtSecret).compact();

			// Create the response body containing the JWT token
			Map<String, String> response = new HashMap<>();
			response.put("token", token);

			return ResponseEntity.ok(response);
		}

		// If the business does not exist or the password is incorrect, return an error
		// message
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Invalid password");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	private String generateSecretKey() {
		// Generate a dynamic secret key using a secure random algorithm
		// Example implementation (you can choose a different algorithm and key length):
		byte[] secretBytes = new byte[64];
		new SecureRandom().nextBytes(secretBytes);
		return Base64.getEncoder().encodeToString(secretBytes);
	}

	private String generateIssuer() {
		// Generate a dynamic issuer value
		// Example implementation (you can modify it as per your requirements):
		String issuerPrefix = "Menucard";
		String timestamp = String.valueOf(System.currentTimeMillis());
		return issuerPrefix + "_" + timestamp;
	}

	@GetMapping("/find/{businessId}")
	public ResponseEntity<Business> findBusinessById(@PathVariable("businessId") String businessId) {
		Business business = businessService.findBusinessById(businessId);
		if (business == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(business, HttpStatus.OK);
		}
	}

	@GetMapping("/findall")
	public ResponseEntity<List<Business>> findAllBusinesses() {
		List<Business> businesses = businessService.findAllBusinesses();

		return new ResponseEntity<>(businesses, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteBusiness(@RequestBody Business business) {
		businessService.deleteBusiness(business);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/update/{businessId}")
	public ResponseEntity<Business> updateBusiness(@PathVariable("businessId") String businessId,
			@RequestParam("businessName") String businessName,
			@RequestParam("contactpersonName") String contactpersonName, @RequestParam("phoneNo") long phoneNo,
			@RequestParam("businessType") String businessType, @RequestParam("emailId") String emailId,
			@RequestParam("password") String password, @RequestParam("address") String address,
			@RequestParam("about") String about,
			@RequestParam("registrationDate") Date registrationDate,
			@RequestParam(required = false) MultipartFile logoFile) {
		Business existingBusiness = businessService.findBusinessById(businessId);
		if (existingBusiness == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		existingBusiness.setBusinessName(businessName);
		existingBusiness.setContactpersonName(contactpersonName);
		existingBusiness.setPhoneNo(phoneNo);
		existingBusiness.setBusinessType(businessType);
		existingBusiness.setEmailId(emailId);
		existingBusiness.setAbout(about);
		existingBusiness.setPassword(password);
		existingBusiness.setAddress(address);
		existingBusiness.setRegistrationDate(registrationDate);

		// Handle logo file if provided
		if (logoFile != null && !logoFile.isEmpty()) {
			try {
				byte[] logoBytes = null;
				if (logoFile.getSize() > 250 * 1024) {
					BufferedImage logoImage = ImageIO.read(logoFile.getInputStream());
					ByteArrayOutputStream logoOutputStream = new ByteArrayOutputStream();
					String logoOutputFormat = getImageOutputFormat(logoFile.getContentType());
					Thumbnails.of(logoImage).size(250, 250).outputFormat(logoOutputFormat).outputQuality(1.0)
							.toOutputStream(logoOutputStream);
					logoBytes = logoOutputStream.toByteArray();
				} else {
					// Use the original logo image bytes without compression
					logoBytes = logoFile.getBytes();
				}
				existingBusiness.setLogo(logoBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Business savedBusiness = businessService.updateBusiness(existingBusiness);
		return new ResponseEntity<>(savedBusiness, HttpStatus.OK);
	}



	// this method use to get all types and items
	private static final String BAR_IMAGE_URL = "/images/Bar.jpg";
	
	private static final String RESTAURANT_IMAGE_URL = "/images/Food.jpg";
	private static final String BARBAR_iMAGE_URL = "/images/barbar1.jpg";
	private static final String ICECREAM_IMAGE_URL = "/images/Icream.jpg";
	private static final String COFFEE_IMAGE_URL = "/images/coffee.jpeg";
	private static final String DEFAULT_IMAGE_URL = "/images/defalut.jpeg";

// this is workimg 
	@GetMapping("/gett/{businessId}")
	public ResponseEntity<List<Map<String, Object>>> getBusinessTypesAndItemsByBusinessIdre(
			@PathVariable String businessId) {
		List<Map<String, Object>> businessData = businessService.getBusinessTypesAndItemsByBusinessId(businessId);
		if (businessData != null && !businessData.isEmpty()) {
			String businessType = (String) businessData.get(0).get("business_type");
			String imageUrl;
			// Determine the image URL based on the business type

			if ("Bar".equalsIgnoreCase(businessType) && businessType.toLowerCase().startsWith("bar")) {
				imageUrl = BAR_IMAGE_URL;
			} else if ("restaurant".equalsIgnoreCase(businessType)
					&& businessType.toLowerCase().startsWith("restaurant")) {
				imageUrl = RESTAURANT_IMAGE_URL;
			} else if ("coffee".equalsIgnoreCase(businessType) && businessType.toLowerCase().startsWith("coffee")) {
				imageUrl = COFFEE_IMAGE_URL;
			} else if ("IcecreamPalaor".equalsIgnoreCase(businessType)
					&& businessType.toLowerCase().startsWith("icecreampalaor")) {
				imageUrl = ICECREAM_IMAGE_URL;
			} else if ("Barbarshop".equalsIgnoreCase(businessType)
					&& businessType.toLowerCase().startsWith("barbarshop")) {
				imageUrl = BARBAR_iMAGE_URL;
			} else {
				imageUrl = DEFAULT_IMAGE_URL;
			}

			// Create a list to store the response data
			List<Map<String, Object>> responseDataList = new ArrayList<>();

			// Create a new map with modified keys (removing underscores) for each business
			// data entry
			for (Map<String, Object> entry : businessData) {
				Map<String, Object> responseData = new HashMap<>();
				for (Map.Entry<String, Object> mapEntry : entry.entrySet()) {
					String key = mapEntry.getKey().replace("_", "");
					responseData.put(key, mapEntry.getValue());
				}
				// Add the image URL to each response data entry
				responseData.put("imageUrl", imageUrl);
				// Add the response data entry to the list
				responseDataList.add(responseData);
			}

			return ResponseEntity.ok().body(responseDataList);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// This is type entity

	@GetMapping("/typeid/{id}")
	public ResponseEntity<BusinessType> getTypeById(@PathVariable int id) {
		BusinessType type = typeService.findTypeById(id);
		if (type == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(type, HttpStatus.OK);
	}

	@GetMapping("alltypenames")
	public ResponseEntity<List<String>> getAllTypeNames() {
		List<String> typeNames = typeService.getAllTypeNames();
		if (typeNames.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(typeNames);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<BusinessType>> getAllTypes() {
		List<BusinessType> types = typeService.findAllTypes();
		if (types.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(types, HttpStatus.OK);
	}

	// THIS METHOD USE TO STORE TYPE ENTITY FILEDS

	@PostMapping("/create/{businessId}")
	public ResponseEntity<BusinessType> createType(@PathVariable String businessId,
			@RequestParam("typeName") String typeName) {
		Business business = businessService.findBusinessById(businessId);
		if (business == null) {
			return ResponseEntity.notFound().build();
		}
		BusinessType type = new BusinessType();
		type.setBusiness(business);

		type.setTypeName(typeName);

		BusinessType savedType = typeService.saveType(type);
		return new ResponseEntity<>(savedType, HttpStatus.CREATED);
	}

	// THIS METHOD USE TO GET ALL TYPENAMES

	@GetMapping("/types/{businessId}")
	public ResponseEntity<List<String>> getBusinessTypes(@PathVariable String businessId) {
		List<BusinessType> types = typeService.findByBusinessId(businessId);
		if (types.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<String> typeNames = types.stream().map(BusinessType::getTypeName).collect(Collectors.toList());
		return ResponseEntity.ok(typeNames);
	}

	@PutMapping("/typeupdate/{id}")
	public ResponseEntity<BusinessType> updateType(@PathVariable int id,
			@RequestParam(value = "typeName") String typeName,
			@RequestParam(value = "typeImage", required = false) MultipartFile typeImage,
			@RequestParam(value = "notice", required = false) String notice,
			@RequestParam(value = "items", required = false) List<Integer> itemIds) {

		BusinessType currentType = typeService.findTypeById(id);
		if (currentType == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		try {
			if (typeName != null) {
				currentType.setTypeName(typeName);
			}

			if (typeImage != null) {
				byte[] imageBytes = typeImage.getBytes();
				currentType.setTypeImage(imageBytes);
			}

			if (notice != null) {
				currentType.setNotice(notice);
			}

			if (itemIds != null) {
				List<Item> items = new ArrayList<>();
				for (Integer itemId : itemIds) {
					Item item = itemService.findItemById(itemId);
					if (item != null) {
						items.add(item);
					}
				}
				currentType.setItems(items);
			}

			BusinessType updatedType = typeService.updateType(currentType);
			return new ResponseEntity<>(updatedType, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteType(@PathVariable int id) {
		BusinessType typeToDelete = typeService.findTypeById(id);
		if (typeToDelete == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		try {
			// Delete the type by its ID
			typeService.deleteType(typeToDelete);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// GET all items
	@GetMapping("/")
	public ResponseEntity<List<Item>> getAllItems() {
		List<Item> items = itemService.findAllItems();
		return new ResponseEntity<>(items, HttpStatus.OK);
	}



	@GetMapping("/{itemId}")
	public Item getItemById(@PathVariable int itemId) {
		return itemService.findItemById(itemId);
	}

	@PostMapping("/saving/{typeId}")
	public ResponseEntity<Item> createItem(@PathVariable int typeId,
			@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("price") double price,
			@RequestParam("description") String description, @RequestParam("itemName") String itemName) {

		BusinessType type = typeService.findTypeById(typeId);
		if (type == null) {
			return ResponseEntity.notFound().build();
		}

		try {
			byte[] imageBytes = null;
			if (file != null && !file.isEmpty()) {
				if (file.getSize() > 250 * 1024) {
					BufferedImage image = ImageIO.read(file.getInputStream());
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					String outputFormat = getImageOutputFormat(file.getContentType());
					Thumbnails.of(image).size(250, 250).outputFormat(outputFormat).outputQuality(1.0)
							.toOutputStream(outputStream);
					imageBytes = outputStream.toByteArray();
				} else {
					// Use the original image bytes without compression
					imageBytes = file.getBytes();
				}
			}

			Item item = new Item(0, type, null, itemName, imageBytes, price, description);
			Item savedItem = itemService.saveItem(item);
			return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private String getImageOutputFormat(String contentType) {
		if (contentType != null && !contentType.isEmpty()) {
			if (contentType.contains("jpeg") || contentType.contains("jpg")) {
				return "jpg";
			} else if (contentType.contains("png")) {
				return "png";
			}
		}
		return "jpg";
	}

	// DELETE an existing item by ID
	@DeleteMapping("/delete/{itemId}")
	public ResponseEntity<Void> deleteItem(@PathVariable int itemId) {
		Item item = itemService.findItemById(itemId);
		if (item != null) {
			itemService.deleteItem(item);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/updateitem/{itemId}")
	public ResponseEntity<Item> updateItem(@PathVariable int itemId,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("price") double price,
			@RequestParam("description") String description, @RequestParam("itemName") String itemName) {

		Item existingItem = itemService.findItemById(itemId);
		if (existingItem != null) {
			try {
				byte[] imageBytes = existingItem.getItemImage(); // Get the current image bytes

				if (file != null && !file.isEmpty()) {
					// Compress the new image if its size exceeds 250KB
					if (file.getSize() > 250 * 1024) {
						// Read the new image from the input stream
						BufferedImage image = ImageIO.read(file.getInputStream());

						// Create an output stream to store the compressed image
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

						// Determine the output format (JPG or PNG) based on the new image's content
						// type
						String outputFormat = getImageOutputFormat(file.getContentType());

						// Use the Thumbnails library to resize and compress the new image
						Thumbnails.of(image).size(250, 250) // Resize the image to fit within 250x250 pixels
								.outputFormat(outputFormat) // Set the output format (JPG or PNG)
								.outputQuality(1.0) // Set the output quality (1.0 means maximum quality)
								.toOutputStream(outputStream); // Write the compressed image to the output stream

						// Get the compressed image bytes from the output stream
						imageBytes = outputStream.toByteArray();
					} else {
						// Use the new image if it does not need compression
						imageBytes = file.getBytes();
					}
				}

				existingItem.setItemImage(imageBytes);
				existingItem.setPrice(price);
				existingItem.setDescription(description);
				existingItem.setItemName(itemName);
				Item updatedItem = itemService.updateItem(existingItem);
				return new ResponseEntity<>(updatedItem, HttpStatus.OK);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.badRequest().build();
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deletebusiness/{businessId}")
	public ResponseEntity<String> deleteBusiness(@PathVariable("businessId") String businessId) {
		businessService.deleteBusiness(businessId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> count() {
		long count = repo.count();
		return ResponseEntity.ok().body(count);
	}

	@PutMapping("/updateAbout/{businessId}")
	public ResponseEntity<Business> updateAbout(@PathVariable String businessId, @RequestBody String about) {
	  Business updatedBusiness = businessService.updateAboutField(businessId, about);
	  return ResponseEntity.ok(updatedBusiness);
	}
}
