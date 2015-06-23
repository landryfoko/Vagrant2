package com.libertas.vipaas.services.playback;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;
import com.libertas.vipaas.services.notification.NotificationServiceLauncher;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, NotificationServiceLauncher.class })
@WebAppConfiguration
public class PlaybackControllerTest{
	
	/*private static final String ROOT_END_POINT = "/v1/playback";
	
	private MockMvc mockMvc;

	@Autowired
	NotificationService playbackService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void getShouldReturnPlaybackLocationSuccessfully() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("tenantId", "sampletenantId");
		offer.put("location", "samplelocation");
		offer.put("productId", "sampleproductId");
		offer.put("customerId", "samplecustomerId");
		offer.put("offers", "sampleoffers");
		offer.put("details", "sampledetails");
		offer.put("videos", "samplevideos");
		offer.put("url", "sampleurl");
		offer.put("mediaList", "samplemediaList");
		offer.put("targetDevice", "sampletargetDevice");
		offer.put("aspectRatio", "sampleaspectRatio");
		offer.put("screenFormat", "samplescreenFormat");
		offer.put("componentId", "samplecomponentId");
		
		when(playbackService.getPlaybackLocation((String)offer.get("id"))).thenReturn(offer);
		
		mockMvc.perform(get(ROOT_END_POINT + "/location/product/{productId}" , (String) offer.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) offer.get("id"))))
        .andExpect(jsonPath("$.tenantId", is((String) offer.get("tenantId"))))
        .andExpect(jsonPath("$.location", is((String) offer.get("location"))))
        .andExpect(jsonPath("$.productId", is((String) offer.get("productId"))))
        .andExpect(jsonPath("$.customerId", is((String) offer.get("customerId"))))
        .andExpect(jsonPath("$.offers", is((String) offer.get("offers"))))
        .andExpect(jsonPath("$.details", is((String) offer.get("details"))))
        .andExpect(jsonPath("$.videos", is((String) offer.get("videos"))))
        .andExpect(jsonPath("$.url", is((String) offer.get("url"))))
        .andExpect(jsonPath("$.mediaList", is((String) offer.get("mediaList"))))
        .andExpect(jsonPath("$.targetDevice", is((String) offer.get("targetDevice"))))
        .andExpect(jsonPath("$.aspectRatio", is((String) offer.get("aspectRatio"))))
        .andExpect(jsonPath("$.screenFormat", is((String) offer.get("screenFormat"))))
        .andExpect(jsonPath("$.componentId", is((String) offer.get("componentId"))))
        ;
	}
	
	@Test
	public void setPlaybackLocationShouldSetPlayBackSuccessfully() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("tenantId", "sampletenantId");
		offer.put("location", "1000");
		offer.put("productId", "sampleproductId");
		offer.put("customerId", "samplecustomerId");
		offer.put("offers", "sampleoffers");
		offer.put("details", "sampledetails");
		offer.put("videos", "samplevideos");
		offer.put("url", "sampleurl");
		offer.put("mediaList", "samplemediaList");
		offer.put("targetDevice", "sampletargetDevice");
		offer.put("aspectRatio", "sampleaspectRatio");
		offer.put("screenFormat", "samplescreenFormat");
		offer.put("componentId", "samplecomponentId");
	
		doNothing().when(playbackService)
			.setPlaybackLocation(
				Long.parseLong((String)offer.get("location")) , (String) offer.get("productId"), offer);
		
		mockMvc.perform(post(ROOT_END_POINT + "/location/{location}/product/{productId}",
				Long.parseLong((String)offer.get("location")) , (String) offer.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isOk());
		
	}
	
	@Test
	public void setPlaybackLocationShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject offer = new JSONObject();
		offer.put("id", "sampleid");
		offer.put("tenantId", "sampletenantId");
		offer.put("location", "1000");
		offer.put("productId", "sampleproductId");
		offer.put("customerId", "samplecustomerId");
		offer.put("offers", "sampleoffers");
		offer.put("details", "sampledetails");
		offer.put("videos", "samplevideos");
		offer.put("url", "sampleurl");
		offer.put("mediaList", "samplemediaList");
		offer.put("targetDevice", "sampletargetDevice");
		offer.put("aspectRatio", "sampleaspectRatio");
		offer.put("screenFormat", "samplescreenFormat");
		offer.put("componentId", "samplecomponentId");
	
		doThrow(new NoSuchProductException("No such product"))
		.when(playbackService)
			.setPlaybackLocation(
				Long.parseLong((String)offer.get("location")) , (String) offer.get("productId"), offer);
		
		mockMvc.perform(post(ROOT_END_POINT + "/location/{location}/product/{productId}",
				Long.parseLong((String)offer.get("location")) , (String) offer.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(offer))
        ).andExpect(status().isNotFound());
		
	}
	
	@Test
	public void getPlaybackURLShouldReturnPlaybackSuccessfully() throws Exception {		
		//invalid end point, path variable are not defined in the url
	}
	
	@Test
	public void getPlaybackURLShouldThrowNoDeviceSpecMappingExceptionWhenSpecNotFound() throws Exception {		
		//invalid end point, path variable are not defined in the url
	}
	
	@Test
	public void getPlaybackURLShouldThrowNoDeviceSpecMappingExceptionWhenMediaNotFound() throws Exception {		
		//invalid end point, path variable are not defined in the url
	}
	
	@Test
	public void getPlaybackURLShouldThrowNoSuchEntitlementExceptionWhenEntitlementNotFound() throws Exception {		
		//invalid end point, path variable are not defined in the url
	}
	
	@Test
	public void getPlaybackLocationShouldReturnPlaybackSuccessfully() throws Exception {		
		//cannot test because it is not in the interface
	}
	
	@Test
	public void getPlaybackLocationShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		//cannot test because it is not in the interface
	}
	
	@Before
	public void setUp() {
		Mockito.reset(playbackService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	*/
}
