package com.libertas.vipaas.services.subscriptionpackage;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, SubscriptionPackageServiceLauncher.class })
@WebAppConfiguration
public class SubscriptionPackageControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/subscriptionPackage";
	
	private MockMvc mockMvc;

	@Autowired
	SubscriptionPackageService subscriptionPackageService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getShouldReturnAllSubscriptionPackageSuccessfully() throws Exception {		
		JSONObject review = new JSONObject();
		review.put("id", "sampleid");
		review.put("customerId", "samplecustomerId");
		review.put("deviceId", "sampledeviceId");
		review.put("tenantId", "sampletenantId");
		review.put("deleteDate", "sampledeleteDate");
		
		when(subscriptionPackageService.findAll(10,10)).thenReturn(review);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) review.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) review.get("customerId"))))
        .andExpect(jsonPath("$.deviceId", is((String) review.get("deviceId"))))
        .andExpect(jsonPath("$.tenantId", is((String) review.get("tenantId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) review.get("deleteDate"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(subscriptionPackageService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
