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
public class RecommendationControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/recommendation";
	
	private MockMvc mockMvc;

	@Autowired
	RecommendationService recommendationService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getShouldReturnTenantSuccessfully() throws Exception {		
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
	
		when(recommendationService.getRecommendationByProductId((String)recommendation.get("id")))
		.thenReturn(recommendation);
		
		mockMvc.perform(get(ROOT_END_POINT + "/product/{productId}", (String) recommendation.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) recommendation.get("id"))))
        .andExpect(jsonPath("$.productId", is((String) recommendation.get("productId"))))
        .andExpect(jsonPath("$.tenantId", is((String) recommendation.get("tenantId"))))
        .andExpect(jsonPath("$.blob", is((String) recommendation.get("blob"))))
        .andExpect(jsonPath("$.deleteDate", is((String) recommendation.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) recommendation.get("links"))))
        ;
	}
	
	@Test
	public void findAllShouldReturnAllRecommendationSuccessfully() throws Exception {		
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
		
		when(recommendationService.findAll(10,10,null,null)).thenReturn(recommendation);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) recommendation.get("id"))))
        .andExpect(jsonPath("$.productId", is((String) recommendation.get("productId"))))
        .andExpect(jsonPath("$.tenantId", is((String) recommendation.get("tenantId"))))
        .andExpect(jsonPath("$.blob", is((String) recommendation.get("blob"))))
        .andExpect(jsonPath("$.deleteDate", is((String) recommendation.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) recommendation.get("links"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(recommendationService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
