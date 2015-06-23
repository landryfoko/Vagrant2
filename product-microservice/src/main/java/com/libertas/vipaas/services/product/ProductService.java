package com.libertas.vipaas.services.product;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateProductException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface ProductService {
	   	JSONObject createProduct(String name, JSONObject  metadata) throws DuplicateProductException ;
		void updateProduct(String productId,JSONObject  metadata) throws NoSuchProductException;
		void deleteProductById(String productId) throws NoSuchProductException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, List<String> tags, String sortField, String sortOrder) throws MissingFieldException;
		JSONObject getProductDetails(String productId) throws NoSuchProductException;
		JSONObject search(Integer pageSize, Integer pageNumber, JSONObject query, String sortField, String sortOrder);
		void addImageToProduct(String productId, JSONObject asset)	throws MissingFieldException, NoSuchProductException;
		void addVideoToProduct(String productId, JSONObject asset)	throws MissingFieldException, NoSuchProductException;
		void addPreviewToProduct(String productId, JSONObject asset)	throws MissingFieldException, NoSuchProductException;
		void addOfferToProduct(String productId, List<String>  offersRequest)throws MissingFieldException, NoSuchProductException;
		void addGenreToProduct(String productId, List<String>  genresRequest)throws MissingFieldException, NoSuchProductException;
		void removeTagFromProduct(String productId, List<String>  tags) throws MissingFieldException, NoSuchProductException;
		void addTagToProduct(String productId, List<String>  tags) throws MissingFieldException, NoSuchProductException;
		void removeOfferFromProduct(String productId, List<String>  offers)throws MissingFieldException, NoSuchProductException;
		void removeGenreFromProduct(String productId, List<String>  genres)throws MissingFieldException, NoSuchProductException;
		JSONObject getProductsByOfferId(String offerId, Integer pageSize, Integer pageNumber);
		void removeNamedTagFromProduct(String productId, List<String>  metadata,String name)throws MissingFieldException, NoSuchProductException;
		void addNamedTagToProduct(String productId, List<String>  metadata,String name)throws MissingFieldException, NoSuchProductException;
		void setAvgUserRating(JSONObject [] ratings)throws MissingFieldException, NoSuchProductException;

}
