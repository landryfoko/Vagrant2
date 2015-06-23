package com.libertas.vipaas.services.bookmark;


import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/bookmark"})
public class BookmarkController {

	@Autowired
	BookmarkService bookmarkService;

	@RequestMapping( value="/product/{productId}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject createBookmark( @PathVariable( "productId" ) String productId,@RequestBody JSONObject  metadata) throws Exception {
		return bookmarkService.createBookmark(productId, metadata);
	}
	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getBookmarkById(@PathVariable( "bookmarkId" ) String bookmarkId) throws Exception {
        return bookmarkService.getBookmarkById(bookmarkId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return bookmarkService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{bookmarkId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateBookmark(@PathVariable( "bookmarkId" ) String bookmarkId, @RequestBody JSONObject  metadata) throws Exception {
		bookmarkService.updateBookmark(bookmarkId, metadata);
	}

	@RequestMapping( value = "/{bookmarkId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteBookmarkById(@PathVariable( "bookmarkId" ) String bookmarkId) throws Exception {
		bookmarkService.deleteBookmarkById(bookmarkId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	 }
	 public JSONObject error(String bookmarkId,String productId, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	 }
	 public JSONObject error(JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String id, Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
