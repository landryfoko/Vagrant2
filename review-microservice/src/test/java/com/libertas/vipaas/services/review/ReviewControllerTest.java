package com.libertas.vipaas.services.review;

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
import com.libertas.vipaas.common.exceptions.NoSuchReviewException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, ReviewServiceLauncher.class })
@WebAppConfiguration
public class ReviewControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/review";
	
	private MockMvc mockMvc;

	@Autowired
	ReviewService reviewService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getShouldReturnReviewSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		when(reviewService.getReviewById((String)review.get("id"))).thenReturn(review);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{reviewId}" , (String) review.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) review.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) review.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) review.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) review.get("productId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) review.get("deleteDate"))))
        ;
	}
	
	@Test
	public void getReviewByIdShouldThrowNoSuchReviewExceptionWhenReviewNotFound() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		when(reviewService.getReviewById((String)review.get("id"))).thenThrow(new NoSuchReviewException("No Such Review"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{reviewId}" , (String) review.get("id")))
        .andExpect(status().isNotFound())
        ;
	}
	
	@Test
	public void createShouldCreateTenantSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
	
		doNothing().when(reviewService)
			.createReview(
				(String) review.get("productId"), review);
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isOk());
		
	}
	
	@Test
	public void createReviewShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
	
		doThrow(new NoSuchProductException("No such product"))
		.when(reviewService)
			.createReview(
				(String) review.get("productId"), review);
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isNotFound());
		
	}
	
	@Test
	public void getShouldReturnAllSubscriptionPackageSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		when(reviewService.findAll((String)review.get("productId"), 10,10,null,null)).thenReturn(review);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll/product/{productId}?" +"pageSize={pageSize}&pageNumber={pageNumber}", 
				(String)review.get("productId"), 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) review.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) review.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) review.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) review.get("productId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) review.get("deleteDate"))))
        ;
	}
	
	@Test
	public void updateShouldUpdateTenantInfoSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		doNothing()
		.when(reviewService)
		.updateReview((String)review.get("productId"), review);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{reviewId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void updateReviewShouldThrowNoSuchReviewExceptionWhenReviewNotFound() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		doThrow(new NoSuchReviewException("No such review"))
		.when(reviewService)
		.updateReview((String)review.get("productId"), review);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{reviewId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldDeleteTenantSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		doNothing()
		.when(reviewService)
		.deleteReviewById((String)review.get("productId"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{reviewId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void deleteReviewByIdShouldThrowNoSuchReviewExceptionWhenReviewNotFound() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("tenantId", "sampletenantId");
		review.put("productId", "sampleproductId");
		review.put("deleteDate", "sampledeleteDate");
		
		doThrow(new NoSuchReviewException("No such review"))
		.when(reviewService)
		.deleteReviewById((String)review.get("productId"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{reviewId}", (String) review.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review))
        ).andExpect(status().isNotFound());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(reviewService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
