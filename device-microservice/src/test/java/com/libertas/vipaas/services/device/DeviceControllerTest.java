package com.libertas.vipaas.services.device;

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

import com.libertas.vipaas.common.exceptions.DuplicateDeviceException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchDeviceException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, DeviceServiceLauncher.class })
@WebAppConfiguration
public class DeviceControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/device";
	
	private MockMvc mockMvc;

	@Autowired
	DeviceService deviceService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
//	@Test
//	public void createShouldCreateTenantSuccessfully() throws Exception {		
//		JSONObject device = new JSONObject();
//		device.put("id", "sampleid");
//		device.put("deviceId", "sampledeviceId");
//		device.put("customerId", "samplecustomerId");
//		device.put("tenantId", "sampletenantId");
//		device.put("deleteDate", "sampledeleteDate");
//		device.put("links", "samplelinks");
//	
//		doNothing().when(deviceService)
//			.registerDevice(
//				(String) device.get("deviceId"), device);
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/{deviceId}", (String) device.get("deviceId"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(device))
//        ).andExpect(status().isOk());
//	}
//	
	@Test
	public void registerDeviceShouldThrowDuplicateDeviceExceptionWhenDeviceNotFound() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
	
		doThrow(new DuplicateDeviceException("Device already exists"))
		.when(deviceService)
		.registerDevice(
				(String) device.get("deviceId"), device);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{deviceId}", (String) device.get("deviceId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device))
        ).andExpect(status().isPreconditionFailed());
	}
	
	@Test
	public void getShouldReturnDeviceSuccessfully() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		when(deviceService.getDeviceById((String)device.get("deviceId"))).thenReturn(device);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{deviceId}" , (String) device.get("deviceId")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) device.get("id"))))
        .andExpect(jsonPath("$.deviceId", is((String) device.get("deviceId"))))
        .andExpect(jsonPath("$.customerId", is((String) device.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) device.get("tenantId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) device.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) device.get("links"))))
        ;
	}
	
	@Test
	public void getDeviceByIdShouldThrowNoSuchDeviceExceptionWhenDeviceNotFound() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		doThrow(new NoSuchDeviceException("No such device"))
		.when(deviceService)
		.getDeviceById(
				(String) device.get("deviceId"));
				
		mockMvc.perform(get(ROOT_END_POINT + "/{deviceId}", (String) device.get("deviceId")))
        .andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnAllDeviceSuccessfully() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		when(deviceService.findAll(10,10,null,null)).thenReturn(device);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) device.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) device.get("customerId"))))
        .andExpect(jsonPath("$.deviceId", is((String) device.get("deviceId"))))
        .andExpect(jsonPath("$.tenantId", is((String) device.get("tenantId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) device.get("deleteDate"))))
         .andExpect(jsonPath("$.links", is((String) device.get("links"))))
        ;
	}
	
	@Test
	public void updateShouldReturnNoUserExceptionWhenIdIsNotFound() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		doThrow(new NoSuchDeviceException("No such device"))
		.when(deviceService)
		.updateDevice((String)device.get("deviceId"), device);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{deviceId}", (String) device.get("deviceId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldUpdateTenantInfoSuccessfully() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		doNothing()
		.when(deviceService)
		.updateDevice((String)device.get("deviceId"), device);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{deviceId}", (String) device.get("deviceId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void deleteDeviceByIdShouldDeleteDeviceSuccessfully() throws Exception {		
		JSONObject device = new JSONObject();
		device.put("id", "sampleid");
		device.put("deviceId", "sampledeviceId");
		device.put("customerId", "samplecustomerId");
		device.put("tenantId", "sampletenantId");
		device.put("deleteDate", "sampledeleteDate");
		device.put("links", "samplelinks");
		
		doNothing()
			.when(deviceService)
		.updateDevice((String)device.get("deviceId"), device);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{deviceId}", (String) device.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(device))
        ).andExpect(status().isOk());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(deviceService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
