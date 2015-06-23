package com.libertas.vipaas.services.rating;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRatingException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, RatingServiceLauncher.class })
@WebAppConfiguration
public class RatingControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/rating";
	
	private MockMvc mockMvc;

	@Autowired
	RatingService ratingService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void createRatingShouldCreateRatingSuccessfully() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
	
		doNothing().when(ratingService)
			.createRating(
				(String) rating.get("productId"), rating);
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) rating.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rating))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void createRatingShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");

		doThrow(new NoSuchProductException("No such product"))
		.when(ratingService)
			.createRating(
				(String) rating.get("productId"), rating);
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) rating.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rating))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteRatingByIdShouldDeleteRatingSuccessfully() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		doNothing()
		.when(ratingService)
		.deleteRatingById((String)rating.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{ratingId}", (String) rating.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rating))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void findAllShouldReturnAllRatingSuccessfully() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		when(ratingService.findAll((String) rating.get("productId") ,10,10,null,null)).thenReturn(rating);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll/product/{productId}?" +"pageSize={pageSize}&pageNumber={pageNumber}", rating.get("productId"), 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) rating.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) rating.get("customerId"))))
        .andExpect(jsonPath("$.deviceId", is((String) rating.get("deviceId"))))
        .andExpect(jsonPath("$.tenantId", is((String) rating.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) rating.get("productId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) rating.get("deleteDate"))))
        ;
	}
	
	@Test
	public void getRatingByIdShouldThrowNoSuchRatingExceptionWhenRatingNotFound() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		when(ratingService.getRatingById((String)rating.get("id"))).thenThrow(new NoSuchRatingException("No Such Rating"));;
		
		mockMvc.perform(get(ROOT_END_POINT + "/{ratingId}" , (String) rating.get("id")))
        .andExpect(status().isNotFound())
        ;
	}
	
	@Test
	public void getShouldReturnRatingSuccessfully() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		when(ratingService.getRatingById((String)rating.get("id"))).thenReturn(rating);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{ratingId}" , (String) rating.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) rating.get("id"))))
        .andExpect(jsonPath("$.deviceId", is((String) rating.get("deviceId"))))
        .andExpect(jsonPath("$.customerId", is((String) rating.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) rating.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) rating.get("productId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) rating.get("deleteDate"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(ratingService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void updateRatingShouldThrowNoSuchRatingExceptionWhenOldRatingNotFound() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		doThrow(new NoSuchRatingException("No such rating"))
		.when(ratingService)
		.updateRating((String)rating.get("id"), rating);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{ratingId}", (String) rating.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rating))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void updateRatingShouldUpdateRatingSuccessfully() throws Exception {		
		JSONObject rating = new JSONObject();
		rating.put("id", "sampleid");
		rating.put("deviceId", "sampledeviceId");
		rating.put("customerId", "samplecustomerId");
		rating.put("tenantId", "sampletenantId");
		rating.put("productId", "sampleproductId");
		rating.put("deleteDate", "sampledeleteDate");
		
		doNothing()
		.when(ratingService)
		.updateRating((String)rating.get("id"), rating);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{ratingId}", (String) rating.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rating))
        ).andExpect(status().isOk());
	}	
	
}
