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
public class PromotionAdminControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/admin/promotion";
	
	private MockMvc mockMvc;

	@Autowired
	PromotionService promotionService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void createShouldCreatePromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
	
		doNothing().when(promotionService)
		.createPromotion(
			(String) promotion.get("name"), promotion);
	
		mockMvc.perform(post(ROOT_END_POINT + "/{name}", (String) promotion.get("name"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotion))
			).andExpect(status().isOk());
	}
	
	@Test
	public void createPromotionShouldThrowDuplicatePromotionExceptionWhenPromotionAlreadyExists() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
	
		doThrow(new DuplicatePromotionException("Promotion with given name already exists"))
		.when(promotionService)
		.createPromotion(
			(String) promotion.get("name"), promotion);
	
		mockMvc.perform(post(ROOT_END_POINT + "/{name}", (String) promotion.get("name"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(promotion))
			).andExpect(status().isPreconditionFailed());
	}
	
	/*	@Test
	public void untagPromotionShouldUntagPromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doNothing()
		.when(promotionService)
		.untagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/untag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isOk());
		
	}	
	
	@Test
	public void untagPromotionShouldThrowMissingFieldExceptionWhenTagsIsEmpty() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new MissingFieldException("Field tags is missing in request body or is not of type List"))
		.when(promotionService)
		.untagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/untag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void untagPromotionShouldThrowNoSuchPromotionExceptionWhenPromotionNotFound() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new NoSuchPromotionException("No Such Promotion"))
		.when(promotionService)
		.untagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/untag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isNotFound());
	}
	
	
	@Test
	public void tagPromotionShouldTagPromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doNothing()
		.when(promotionService)
		.tagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/tag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isOk());
	}	
	
	@Test
	public void tagPromotionShouldThrowMissingFieldExceptionWhenTagsIsEmpty() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new MissingFieldException("Field tags is missing in request body or is not of type List"))
		.when(promotionService)
		.tagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/tag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void tagPromotionShouldThrowNoSuchPromotionExceptionWhenPromotionNotFound() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new NoSuchPromotionException("No Such Promotion"))
		.when(promotionService)
		.tagPromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}/tag", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isNotFound());
	}
	*/
	@Test
	public void updatePromotionShouldUpdatePromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doNothing()
		.when(promotionService)
		.updatePromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isOk());
	}	
	
	@Test
	public void updatePromotionShouldThrowNoSuchPromotionExceptionWhenPromotionNotFound() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new NoSuchPromotionException("No Such Promotion"))
		.when(promotionService)
		.updatePromotion((String)promotion.get("id"), promotion);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isNotFound());
	}
	
	
	@Test
	public void deletePromotionByIdShouldDeletePromotionSuccessfully() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doNothing()
		.when(promotionService)
		.deletePromotionById((String)promotion.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isOk());
	}	
	
	@Test
	public void deletePromotionShouldThrowNoSuchPromotionExceptionWhenPromotionNotFound() throws Exception {		
		JSONObject promotion = new JSONObject();
		promotion.put("id", "sampleid");
		promotion.put("tenantId", "sampletenantId");
		promotion.put("name", "samplename");
		promotion.put("tags", "sampletags");
		promotion.put("deleteDate", "sampledeleteDate");
		promotion.put("links", "samplelinks");
		
		doThrow(new NoSuchPromotionException("No Such Promotion"))
		.when(promotionService)
		.deletePromotionById((String)promotion.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{promotionId}", (String) promotion.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(promotion))
        ).andExpect(status().isNotFound());
	}	
	
	@Before
	public void setUp() {
		Mockito.reset(promotionService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
