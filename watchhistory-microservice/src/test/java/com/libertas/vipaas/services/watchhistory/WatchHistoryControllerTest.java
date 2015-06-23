package com.libertas.vipaas.services.watchhistory;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, WatchHistoryServiceLauncher.class })
@WebAppConfiguration
public class WatchHistoryControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/watchhistory";
	
	private MockMvc mockMvc;

	@Autowired
	WatchHistoryService watchHistoryService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getShouldReturnAllWatchHistorySuccessfully() throws Exception {		
		JSONObject watchHistory = new JSONObject();
		watchHistory.put("id", "sampleid");
		watchHistory.put("customerId", "samplecustomerId");
		watchHistory.put("tenantId", "sampletenantId");
		watchHistory.put("productId", "sampleproductId");
		watchHistory.put("completed", "samplecompleted");
		watchHistory.put("lastUpdateDate", "samplelastUpdateDate");
		watchHistory.put("deleteDate", "sampledeleteDate");
		
		when(watchHistoryService.findAll(10,10,null,null)).thenReturn(watchHistory);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) watchHistory.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) watchHistory.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) watchHistory.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) watchHistory.get("productId"))))
        .andExpect(jsonPath("$.completed", is((String) watchHistory.get("completed"))))
        .andExpect(jsonPath("$.lastUpdateDate", is((String) watchHistory.get("lastUpdateDate"))))
        .andExpect(jsonPath("$.deleteDate", is((String) watchHistory.get("deleteDate"))))
        ;
	}
	
	@Test
	public void createShouldCreateTenantSuccessfully() throws Exception {		
		JSONObject watchHistory = new JSONObject();
		watchHistory.put("id", "sampleid");
		watchHistory.put("customerId", "samplecustomerId");
		watchHistory.put("tenantId", "sampletenantId");
		watchHistory.put("productId", "sampleproductId");
		watchHistory.put("completed", "samplecompleted");
		watchHistory.put("lastUpdateDate", "samplelastUpdateDate");
		watchHistory.put("deleteDate", "sampledeleteDate");
	
		doNothing().when(watchHistoryService)
			.createWatchHistoryEntry(
				(String) watchHistory.get("productId"), watchHistory);
		
		mockMvc.perform(post(ROOT_END_POINT + "?" +"productId={productId}", watchHistory.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchHistory))
        ).andExpect(status().isOk());
	}
	
	
	@Test
	public void deleteShouldDeleteTenantSuccessfully() throws Exception {		
		JSONObject watchHistory = new JSONObject();
		watchHistory.put("id", "sampleid");
		watchHistory.put("customerId", "samplecustomerId");
		watchHistory.put("tenantId", "sampletenantId");
		watchHistory.put("productId", "sampleproductId");
		watchHistory.put("completed", "samplecompleted");
		watchHistory.put("lastUpdateDate", "samplelastUpdateDate");
		watchHistory.put("deleteDate", "sampledeleteDate");
		
		doNothing()
		.when(watchHistoryService)
		.deleteWatchHistoryEntryById((String)watchHistory.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{watchHistoryId}", (String) watchHistory.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(watchHistory))
        ).andExpect(status().isOk());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(watchHistoryService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
