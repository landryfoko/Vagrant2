package com.libertas.vipaas.services.entitlement;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, EntitlementServiceLauncher.class })
@WebAppConfiguration
public class EntitlementAdminControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/admin/entitlement";
	
	private MockMvc mockMvc;

	@Autowired
	EntitlementService entitlementService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
//	@Test
//	public void updateEntitlementShouldUpdateEntitlementSuccessfully() throws Exception {		
//		JSONObject entitlement = new JSONObject();
//		entitlement.put("id", "sampleid");
//		entitlement.put("customerId", "samplecustomerId");
//		entitlement.put("tenantId", "sampletenantId");
//		entitlement.put("productId", "sampleproductId");
//		entitlement.put("offerId", "sampleofferId");
//		entitlement.put("type", "sampletype");
//		entitlement.put("creationDateMillis", "samplecreationDateMillis");
//		entitlement.put("disableDateMillis", "1000");
//		entitlement.put("entitlementDurationMillis", "sampleentitlementDurationMillis");	
//		
//		doNothing()
//		.when(entitlementService)
//		.updateEntitlement((String)entitlement.get("id"), entitlement);
//		
//		mockMvc.perform(put(ROOT_END_POINT + "/{entitlementId}", (String) entitlement.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(entitlement))
//        ).andExpect(status().isOk());
//	}
	
//	@Test
//	public void deleteShouldDeleteTenantSuccessfully() throws Exception {		
//		JSONObject entitlement = new JSONObject();
//		entitlement.put("id", "sampleid");
//		entitlement.put("customerId", "samplecustomerId");
//		entitlement.put("tenantId", "sampletenantId");
//		entitlement.put("productId", "sampleproductId");
//		entitlement.put("offerId", "sampleofferId");
//		entitlement.put("type", "sampletype");
//		entitlement.put("creationDateMillis", "samplecreationDateMillis");
//		entitlement.put("disableDateMillis", "1000");
//		entitlement.put("entitlementDurationMillis", "sampleentitlementDurationMillis");
//		
//		doNothing()
//		.when(entitlementService)
//		.disableEntitlement((String)entitlement.get("id"), Long.parseLong((String)entitlement.get("disableDateMillis")));
//		
//		mockMvc.perform(delete(ROOT_END_POINT + "/{entitlementId}?disableDateMillis=" + 
//		Long.parseLong((String)entitlement.get("disableDateMillis")) , 
//				(String) entitlement.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(entitlement))
//        ).andExpect(status().isOk());
//	}
	
	
	@Test
	public void findValidOneShouldThrowNoSuchEntitlementExceptionWhenEntitlementNotFound() throws Exception {		
		JSONObject entitleMent = new JSONObject();
		entitleMent.put("id", "sampleid");
		entitleMent.put("customerId", "samplecustomerId");
		entitleMent.put("tenantId", "sampletenantId");
		entitleMent.put("productId", "sampleproductId");
		entitleMent.put("offerId", "sampleofferId");
		entitleMent.put("type", "sampletype");
		entitleMent.put("creationDateMillis", "samplecreationDateMillis");
		entitleMent.put("disableDateMillis", "1000");
		entitleMent.put("entitlementDurationMillis", "sampleentitlementDurationMillis");		
		
		doThrow(new NoSuchEntitlementException("No valid entitlement found on product"))
		.when(entitlementService)
		.findValidOne(
			(String) entitleMent.get("productId"));
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) entitleMent.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entitleMent))
        ).andExpect(status().isNotFound())
        ;
	}
	
//	@Test
//	public void findValidOneShouldReturnEntitlementSuccessfully() throws Exception {		
//		JSONObject entitlement = new JSONObject();
//		entitlement.put("id", "sampleid");
//		entitlement.put("customerId", "samplecustomerId");
//		entitlement.put("tenantId", "sampletenantId");
//		entitlement.put("productId", "sampleproductId");
//		entitlement.put("offerId", "sampleofferId");
//		entitlement.put("type", "sampletype");
//		entitlement.put("creationDateMillis", "samplecreationDateMillis");
//		entitlement.put("disableDateMillis", "1000");
//		entitlement.put("entitlementDurationMillis", "sampleentitlementDurationMillis");		
//		
//		when(entitlementService.findValidOne((String)entitlement.get("productId"))).thenReturn(entitlement);
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) entitlement.get("productId"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(entitlement))
//        ).andExpect(status().isOk())
//        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//        .andExpect(jsonPath("$.id", is((String) entitlement.get("id"))))
//        .andExpect(jsonPath("$.customerId", is((String) entitlement.get("customerId"))))
//        .andExpect(jsonPath("$.tenantId", is((String) entitlement.get("tenantId"))))
//        .andExpect(jsonPath("$.productId", is((String) entitlement.get("productId"))))
//        .andExpect(jsonPath("$.offerId", is((String) entitlement.get("offerId"))))
//        .andExpect(jsonPath("$.type", is((String) entitlement.get("type"))))
//        .andExpect(jsonPath("$.creationDateMillis", is((String) entitlement.get("creationDateMillis"))))
//        .andExpect(jsonPath("$.disableDateMillis", is((String) entitlement.get("disableDateMillis"))))
//        .andExpect(jsonPath("$.entitlementDurationMillis", is((String) entitlement.get("entitlementDurationMillis"))));
//	}
//	

//	@Test
//	public void createEntitlementShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
//		JSONObject entitlement = new JSONObject();
//		entitlement.put("id", "sampleid");
//		entitlement.put("customerId", "samplecustomerId");
//		entitlement.put("tenantId", "sampletenantId");
//		entitlement.put("productId", "sampleproductId");
//		entitlement.put("offerId", "sampleofferId");
//		entitlement.put("type", "sampletype");
//		entitlement.put("creationDateMillis", "samplecreationDateMillis");
//		entitlement.put("disableDateMillis", "1000");
//		entitlement.put("entitlementDurationMillis", "sampleentitlementDurationMillis");	
//	
//		doThrow(new NoSuchProductException("No such product"))
//		.when(entitlementService)
//		.createEntitlement(
//			(String) entitlement.get("productId"), (String) entitlement.get("offerId"), entitlement);
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}/offer/${offerId}", 
//				(String) entitlement.get("productId"), (String) entitlement.get("offerId"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(entitlement))
//        ).andExpect(status().isNotFound());
//	}
//	
	@Test
	public void createEntitlementShouldCreateEntitlementSuccessfully() throws Exception {		
		JSONObject entitlement = new JSONObject();
		entitlement.put("id", "sampleid");
		entitlement.put("customerId", "samplecustomerId");
		entitlement.put("tenantId", "sampletenantId");
		entitlement.put("productId", "sampleproductId");
		entitlement.put("offerId", "sampleofferId");
		entitlement.put("type", "sampletype");
		entitlement.put("creationDateMillis", "samplecreationDateMillis");
		entitlement.put("disableDateMillis", "1000");
		entitlement.put("entitlementDurationMillis", "sampleentitlementDurationMillis");	
	
		doNothing()
		.when(entitlementService)
		.createEntitlement(
			(String) entitlement.get("productId"), (String) entitlement.get("offerId"), entitlement);
		
        mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}/offer/${offerId}", 
				(String) entitlement.get("productId"), (String) entitlement.get("offerId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entitlement))
        ).andExpect(status().isOk());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(entitlementService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
