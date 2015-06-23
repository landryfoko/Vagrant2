package com.libertas.vipaas.services.hls;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, HLSServiceLauncher.class })
@WebAppConfiguration
public class HLSProviderControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/hls";
	
	private MockMvc mockMvc;

	@Autowired
	HLSProviderService hlsProviderService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getHLSCredentialsShouldReturnHLSProviderSuccessfully() throws Exception {		
		JSONObject hlsProvider = new JSONObject();
		hlsProvider.put("key", "samplekey");
		
		when(hlsProviderService.getHLSCredentials(hlsProvider)).thenReturn(hlsProvider);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hlsProvider))
        ).andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.key", is((String) hlsProvider.get("key"))))
        ;
	}
	
	@Test
	public void getHdLSCredentialsShouldReturnHLSProviderSuccessfully() throws Exception {		
		JSONObject hlsProvider = new JSONObject();
		hlsProvider.put("nana", "nono");
		
		when(hlsProviderService.getHLSCredentials(hlsProvider)).thenReturn(hlsProvider);
		
		mockMvc.perform(post(ROOT_END_POINT +"/ha")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hlsProvider))
        ).andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.nana", is((String) hlsProvider.get("nana"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(hlsProviderService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
