package com.libertas.vipaas.services.product;

import java.util.Arrays;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/product"})
public class ProductController {

	@Autowired
	ProductService productService;
	
	@RequestMapping(value = "/{productId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getProductDetails(@PathVariable( "productId" ) String productId) throws NoSuchProductException  {
        return productService.getProductDetails(productId);
    }
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, 
			@RequestParam(value="pageNumber") Integer pageNumber, @RequestParam(value="tags") String [] tags, @RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
		return productService.findAll(pageSize, pageNumber,Arrays.asList(tags),sortField,sortOrder);
    }
	
	@RequestMapping(value = "/search", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error" )
	public @ResponseBody  JSONObject search(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber, @RequestBody JSONObject query,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return productService.search(pageSize, pageNumber,query,sortField,sortOrder);
    }
	
	 private JSONObject empty= new JSONObject();
	 
	 public JSONObject error(String text) {
	        return empty;
	    }
	 public JSONObject error( Integer pageNumber, Integer pageSize, JSONObject request) {
		 return empty;
	 }
	
}
