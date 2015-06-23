package com.libertas.vipaas.services.genre;

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
@RequestMapping({ "/v1/admin/genre"})
public class GenreAdminController {

	@Autowired
	GenreService genreService;

	@RequestMapping( value="/{name}", method = RequestMethod.POST )
	@HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject  createGenre( @PathVariable( "name" ) String name,@RequestBody JSONObject  metadata) throws Exception {
		return genreService.createGenre(name, metadata);
	}
	@RequestMapping(value = "/{genreId}", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject getGenreById(@PathVariable( "genreId" ) String genreId) throws Exception {
        return genreService.getGenreById(genreId);
    }

	@RequestMapping(value = "/findAll", method = RequestMethod.GET )
	@HystrixCommand(fallbackMethod = "error")
	public @ResponseBody  JSONObject findAll(@RequestParam(value="pageSize") Integer pageSize, @RequestParam(value="pageNumber") Integer pageNumber,@RequestParam(value="sortField",required=false) String sortField,@RequestParam(value="sortOrder",required=false) String sortOrder) throws Exception {
        return genreService.findAll(pageSize, pageNumber,sortField,sortOrder);
    }

	@RequestMapping(value = "/{genreId}", method = RequestMethod.PUT )
	@HystrixCommand(fallbackMethod = "error")
	public void updateGenre(@PathVariable( "genreId" ) String genreId, @RequestBody JSONObject  metadata) throws Exception {
		genreService.updateGenre(genreId, metadata);
	}

	@RequestMapping( value = "/{genreId}", method = RequestMethod.DELETE )
	@HystrixCommand(fallbackMethod = "error")
	public  void   deleteGenreById(@PathVariable( "genreId" ) String genreId) throws Exception {
		genreService.deleteGenreById(genreId);
	}

	 public JSONObject error(String text) {
	        return new JSONObject();
	    }
	 public JSONObject error(String genreId,String productId, JSONObject  device) {
		 return new JSONObject();
	 }
	 public JSONObject error(String text,JSONObject  device) {
	        return new JSONObject();
	    }
	 public JSONObject error( Integer pageNumber, Integer pageSize) {
		 return new JSONObject();
	 }

}
