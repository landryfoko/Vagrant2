package com.libertas.vipaas.services.recommendation;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.libertas.vipaas.common.exceptions.DuplicateRecommendationException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, RecommendationServiceLauncher.class })
@WebAppConfiguration
public class RecommendationAdminControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/admin/recommendation";
	
	private MockMvc mockMvc;

	@Autowired
	RecommendationService recommendationService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
/*	@Test
	public void createShouldCreateRecommendationSuccessfully() throws Exception {		
		JSONObject recommendation = new JSONObject();
		recommendation.put("id", "sampleid");
		recommendation.put("productId", "sampleproductId");
		recommendation.put("tenantId", "sampletenantId");
		recommendation.put("blob", "sampleblob");
		recommendation.put("deleteDate", "sampledeleteDate");
		recommendation.put("links", "samplelinks");
				
		List<String> productIds = new ArrayList<String>();
		productIds.add("sampleid1");
		productIds.add("sampleid2");
		productIds.add("sampleid3");
		
		doNothing().when(recommendationService)
		.createRecommendation(
			(String) recommendation.get("id"), productIds);
	
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) recommendation.get("id"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productIds))
			).andExpect(status().isOk());
	}
	*/
	/*@Test
	public void createShouldThrowDuplicateRecommendationExceptionWhenRecommendationAlreadyExists() throws Exception {		
		JSONObject recommendation = new JSONObject();
		recommendation.put("id", "sampleid");
		recommendation.put("productId", "sampleproductId");
		recommendation.put("tenantId", "sampletenantId");
		recommendation.put("blob", "sampleblob");
		recommendation.put("deleteDate", "sampledeleteDate");
		recommendation.put("links", "samplelinks");
				
		List<String> productIds = new ArrayList<String>();
		productIds.add("sampleid1");
		productIds.add("sampleid2");
		productIds.add("sampleid3");
		
		doThrow(new DuplicateRecommendationException("Recommendation with given name already exists"))
			.when(recommendationService)
		.createRecommendation(
			(String) recommendation.get("productId"), productIds);
	
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) recommendation.get("productId"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productIds))
		).andExpect(status().isPreconditionFailed());
	}
	*/
	
	@Test
	public void updateRecommendationShouldUpdateRecommendationSuccessfully() throws Exception {		
		JSONObject recommendation = new JSONObject();
		recommendation.put("id", "sampleid");
		recommendation.put("productId", "sampleproductId");
		recommendation.put("tenantId", "sampletenantId");
		recommendation.put("blob", "sampleblob");
		recommendation.put("deleteDate", "sampledeleteDate");
		recommendation.put("links", "samplelinks");
				
		List<String> productIds = new ArrayList<String>();
		productIds.add("sampleid1");
		productIds.add("sampleid2");
		productIds.add("sampleid3");
		
		doNothing()
		.when(recommendationService)
		.updateRecommendation((String)recommendation.get("id"), recommendation);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{recommendationId}", (String) recommendation.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recommendation))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void deleteRecommendationByProductIdShouldDeleteRecommendationSuccessfully() throws Exception {		
		JSONObject recommendation = new JSONObject();
		recommendation.put("id", "sampleid");
		recommendation.put("productId", "sampleproductId");
		recommendation.put("tenantId", "sampletenantId");
		recommendation.put("blob", "sampleblob");
		recommendation.put("deleteDate", "sampledeleteDate");
		recommendation.put("links", "samplelinks");
		
		doNothing()
		.when(recommendationService)
		.deleteRecommendationByProductId((String)recommendation.get("productId"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/product/{productId}", (String) recommendation.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recommendation))
        ).andExpect(status().isOk());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(recommendationService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
