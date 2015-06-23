package com.libertas.vipaas.services.product;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/admin/product"})
public class ProductAdminController {

	@Autowired
	ProductService productService;

	
	@RequestMapping( value="/{bindId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createProduct( @PathVariable( "bindId" ) String bindId,@RequestBody JSONObject  metadata) throws Exception {
		return productService.createProduct(bindId, metadata);

	}
	@RequestMapping(value ="/{productId}/tag", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public void addTagToProduct(@PathVariable( "productId" ) String productId, @RequestBody String []  tags) throws MissingFieldException, NoSuchProductException{
         productService.addTagToProduct(productId,Arrays.asList(tags));
    }
	@RequestMapping(value = "/{productId}/tag", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public void removeTagFromProduct(@PathVariable( "productId" ) String productId, @RequestBody  String []  tags) throws MissingFieldException, NoSuchProductException  {
         productService.removeTagFromProduct(productId,Arrays.asList(tags));
    }

	@RequestMapping( value = "/{productId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteProductById(@PathVariable( "productId" ) String productId) throws Exception {
		productService.deleteProductById(productId);
	}

	@RequestMapping( value = "/{productId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public  void   updateProduct(@PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		productService.updateProduct(productId, metadata);
	}
	@RequestMapping( value = "/{productId}/video", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addVideoToProduct(@PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		productService.addVideoToProduct(productId, metadata);
	}
	@RequestMapping( value = "/{productId}/preview", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addPreviewToProduct(@PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		productService.addPreviewToProduct(productId, metadata);
	}
	@RequestMapping( value = "/{productId}/image", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addImageToProduct(@PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		productService.addImageToProduct(productId, metadata);
	}

	@RequestMapping( value = "/{productId}/offer", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addOfferToProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata) throws Exception {
		productService.addOfferToProduct(productId, Arrays.asList(metadata));
	}

	@RequestMapping( value = "/{productId}/genre", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   removeGenreFromProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata) throws Exception {
		productService.removeGenreFromProduct(productId, Arrays.asList(metadata));
	}
	@RequestMapping( value = "/{productId}/offer", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   removeOfferFromProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata) throws Exception {
		productService.removeOfferFromProduct(productId,  Arrays.asList(metadata));
	}

	@RequestMapping( value = "/{productId}/genre", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addGenreToProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata) throws Exception {
		productService.addGenreToProduct(productId,  Arrays.asList(metadata));
	}
	@RequestMapping( value = "/{productId}/{name}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   removeNamedTagFromProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata,@PathVariable( "name" ) String name) throws Exception {
		productService.removeNamedTagFromProduct(productId,  Arrays.asList(metadata),name);
	}

	@RequestMapping( value = "/{productId}/{name}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   addNamedTagToProduct(@PathVariable( "productId" ) String productId,@RequestBody String []  metadata,@PathVariable( "name" ) String name) throws Exception {
		productService.addNamedTagToProduct(productId,  Arrays.asList(metadata),name);
	}
	@RequestMapping( value = "/averageUserRatings", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
	public  void   setAvgUserRating(@RequestBody JSONObject [] ratings) throws Exception {
		productService.setAvgUserRating(ratings);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String productId,String productIdc, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error( Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
