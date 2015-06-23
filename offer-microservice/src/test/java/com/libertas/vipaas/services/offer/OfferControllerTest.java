package com.libertas.vipaas.services.offer;

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

import com.libertas.vipaas.common.exceptions.DuplicateOfferException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, OfferServiceLauncher.class })
@WebAppConfiguration
public class OfferControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/admin/offer";
	
	private MockMvc mockMvc;

	@Autowired
	OfferService offerService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;

	@Test
	public void getOfferByIdShouldReturnOfferSuccessfully() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("customerId", "samplecustomerId");
		offer.put("tenantId", "sampletenantId");
		offer.put("regex", "sampleregex");
		offer.put("offerType", "sampleofferType");
		offer.put("startDateTimestampMillis", "samplestartDateTimestampMillis");
		offer.put("endDateTimestampMillis", "sampleendDateTimestampMillis");
		offer.put("entitlementDurationMillis", "entitlementDurationMillis");
		offer.put("name", "samplename");
		offer.put("deleteDate", "sampledeleteDate");
		offer.put("links", "samplelinks");
		
		when(offerService.findById((String)offer.get("id"))).thenReturn(offer);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{offerId}" , (String) offer.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) offer.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) offer.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) offer.get("tenantId"))))
        .andExpect(jsonPath("$.regex", is((String) offer.get("regex"))))
        .andExpect(jsonPath("$.offerType", is((String) offer.get("offerType"))))
        .andExpect(jsonPath("$.startDateTimestampMillis", is((String) offer.get("startDateTimestampMillis"))))
        .andExpect(jsonPath("$.endDateTimestampMillis", is((String) offer.get("endDateTimestampMillis"))))
        .andExpect(jsonPath("$.entitlementDurationMillis", is((String) offer.get("entitlementDurationMillis"))))
        .andExpect(jsonPath("$.name", is((String) offer.get("name"))))
        .andExpect(jsonPath("$.deleteDate", is((String) offer.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) offer.get("links"))))
        ;
	}
	
	@Test
	public void getOfferByIdShouldThrowNoSuchOfferExceptionWhenOfferNotFound() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("customerId", "samplecustomerId");
		offer.put("tenantId", "sampletenantId");
		offer.put("regex", "sampleregex");
		offer.put("offerType", "sampleofferType");
		offer.put("startDateTimestampMillis", "samplestartDateTimestampMillis");
		offer.put("endDateTimestampMillis", "sampleendDateTimestampMillis");
		offer.put("entitlementDurationMillis", "entitlementDurationMillis");
		offer.put("name", "samplename");
		offer.put("deleteDate", "sampledeleteDate");
		offer.put("links", "samplelinks");
		
		when(offerService.findById(
				(String)offer.get("id")))
				.thenThrow(new NoSuchOfferException("No Such Offer"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{offerId}", (String) offer.get("id")))
        .andExpect(status().isNotFound()); 
	}
	
	@Test
	public void findAllShouldReturnAllSubscriptionPackageSuccessfully() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("customerId", "samplecustomerId");
		offer.put("tenantId", "sampletenantId");
		offer.put("regex", "sampleregex");
		offer.put("offerType", "sampleofferType");
		offer.put("startDateTimestampMillis", "samplestartDateTimestampMillis");
		offer.put("endDateTimestampMillis", "sampleendDateTimestampMillis");
		offer.put("entitlementDurationMillis", "entitlementDurationMillis");
		offer.put("name", "samplename");
		offer.put("deleteDate", "sampledeleteDate");
		offer.put("links", "samplelinks");
		
		when(offerService.findAll(10,10,null,null)).thenReturn(offer);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) offer.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) offer.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) offer.get("tenantId"))))
        .andExpect(jsonPath("$.regex", is((String) offer.get("regex"))))
        .andExpect(jsonPath("$.offerType", is((String) offer.get("offerType"))))
          .andExpect(jsonPath("$.startDateTimestampMillis", is((String) offer.get("startDateTimestampMillis"))))
            .andExpect(jsonPath("$.endDateTimestampMillis", is((String) offer.get("endDateTimestampMillis"))))
              .andExpect(jsonPath("$.entitlementDurationMillis", is((String) offer.get("entitlementDurationMillis"))))
                .andExpect(jsonPath("$.name", is((String) offer.get("name"))))
                 .andExpect(jsonPath("$.deleteDate", is((String) offer.get("deleteDate"))))
                  .andExpect(jsonPath("$.links", is((String) offer.get("links"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(offerService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
}
