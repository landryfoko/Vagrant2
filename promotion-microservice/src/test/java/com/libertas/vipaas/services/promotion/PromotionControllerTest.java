package com.libertas.vipaas.services.promotion;

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

import com.libertas.vipaas.common.exceptions.DuplicatePromotionException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchPromotionException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, PromotionServiceLauncher.class })
@WebAppConfiguration
public class PromotionControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/promotion";
	
	private MockMvc mockMvc;

	@Autowired
	PromotionService promotionService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	
	
	@Test
	public void getPromotionByIdShouldReturnPromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		when(promotionService.getPromotionById((String)promotion.get("id"))).thenReturn(promotion);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) promotion.get("id"))))
        .andExpect(jsonPath("$.tenantId", is((String) promotion.get("tenantId"))))
        .andExpect(jsonPath("$.name", is((String) promotion.get("name"))))
        .andExpect(jsonPath("$.tags", is((String) promotion.get("tags"))))
        .andExpect(jsonPath("$.deleteDate", is((String) promotion.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) promotion.get("links"))))
        ;
	}	

	@Test
	public void getPromotionByIdShouldThrowNoSuchPromotionExceptionWhenPromotionNotFound() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		when(promotionService.getPromotionById((String)promotion.get("id"))).thenThrow(new NoSuchPromotionException("No Such Promotion"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id"))
	            .contentType(TestUtil.APPLICATION_JSON_UTF8)
	            .content(TestUtil.convertObjectToJsonBytes(promotion))
				).andExpect(status().isNotFound());
	}	
	
	@Test
	public void findAllShouldReturnAllPromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		when(promotionService.findAll(10,10, "tag",null,null)).thenReturn(promotion);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}&tag={tag}", 10, 10, "tag"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) promotion.get("id"))))
        .andExpect(jsonPath("$.tenantId", is((String) promotion.get("tenantId"))))
        .andExpect(jsonPath("$.name", is((String) promotion.get("name"))))
        .andExpect(jsonPath("$.tags", is((String) promotion.get("tags"))))
        .andExpect(jsonPath("$.deleteDate", is((String) promotion.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) promotion.get("links"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(promotionService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
