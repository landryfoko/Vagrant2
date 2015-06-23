package com.libertas.vipaas.services.tenant;

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

import com.libertas.vipaas.common.exceptions.AuthenticationException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, TenantServiceLauncher.class })
@WebAppConfiguration
public class TenantControllerTest{
	private MockMvc mockMvc;

	@Autowired
	TenantService tenantServiceMock;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void createShouldCreateTenantSuccessfully() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
	
		when(tenantServiceMock
			.createTenant(
				(String) tenant.get("email"), (String) tenant.get("password"), tenant)).thenReturn(tenant);
		
		mockMvc.perform(post("/v1/tenant")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isOk());
		
	}
	
	@Test
	public void createShouldReturnAuthenticationExceptionWhenPasswordIsEmpty() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "");
		tenant.put("email", "sampleemail");
	
		mockMvc.perform(post("/v1/tenant")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void createShouldReturnDuplicateUserExceptionWhenEmailAlreadyExist() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
	
		doThrow(new DuplicateUserException("User already exists with given email"))
			.when(tenantServiceMock)
			.createTenant(
				(String) tenant.get("email"), (String) tenant.get("password"), tenant);
		
		mockMvc.perform(post("/v1/tenant")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isPreconditionFailed());
		
	}
	
	@Test
	public void createShouldReturnNoEmailInRequestExceptionWhenEmailIsEmpty() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "");
	
		mockMvc.perform(post("/v1/tenant")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void deleteShouldDeleteTenantSuccessfully() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		tenant.put("deleteDate", new Date().toString());
		
		doNothing()
		.when(tenantServiceMock)
		.deleteTenantById((String)tenant.get("id"));
		
		mockMvc.perform(delete("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void deleteShouldReturnNoUserExceptionWhenIdIsEmpty() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		
		doThrow(new NoSuchUserException("No user with given Id"))
		.when(tenantServiceMock)
		.deleteTenantById((String)tenant.get("id"));
		
		mockMvc.perform(delete("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isMethodNotAllowed());
		
	}
	
	
	@Test
	public void deleteShouldReturnNoUserExceptionWhenIdIsNotFound() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "fdsafdsafdsafdsa");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		
		doThrow(new NoSuchUserException("No user with given Id"))
		.when(tenantServiceMock)
		.deleteTenantById((String)tenant.get("id"));
		
		mockMvc.perform(delete("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void getShouldReturnNoUserExceptionWhenIdIsEmpty() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", " ");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
	
		when(tenantServiceMock.getTenantById((String)tenant.get("id"))).thenThrow(new NoSuchUserException("No user with given Id"));
		
		mockMvc.perform(get("/v1/tenant/{tenantId}", (String) tenant.get("id")))
        .andExpect(status().isMethodNotAllowed()); //this will return 405 error
	}
	
	@Test
	public void getShouldReturnNoUserExceptionWhenIdIsNotFound() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "notFound");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
	
		when(tenantServiceMock.getTenantById((String)tenant.get("id"))).thenThrow(new NoSuchUserException("No user with given Id"));
		
		mockMvc.perform(get("/v1/tenant/{tenantId}", (String) tenant.get("id")))
        .andExpect(status().isNotFound());
		
	}
	
	@Test
	public void getShouldReturnTenantSuccessfully() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
	
		when(tenantServiceMock.getTenantById((String)tenant.get("id"))).thenReturn(tenant);
		
		mockMvc.perform(get("/v1/tenant/{tenantId}", (String) tenant.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) tenant.get("id"))))
        .andExpect(jsonPath("$.firstName", is((String) tenant.get("firstName"))))
        .andExpect(jsonPath("$.password", is((String) tenant.get("password"))))
        .andExpect(jsonPath("$.email", is((String) tenant.get("email"))));
		
	}
	
	@Test
	public void logoutShouldLogoutTenantSuccessfully() throws Exception {		
		//no implementation yet, service method doesn't throw exception yet
	}
	
	
	@Test
	public void logoutShouldReturnNoUserExceptionWhenIdIsEmpty() throws Exception {		
		//no implementation yet, service method doesn't throw exception yet
	}
	
	@Test
	public void logoutShouldReturnNoUserExceptionWhenIdIsNotFound() throws Exception {		
		//no implementation yet, service method doesn't throw exception yet
	}
	
	@Before
	public void setUp() {
		Mockito.reset(tenantServiceMock);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void updateShouldReturnNoUserExceptionWhenIdIsEmpty() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", " ");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		
		doThrow(new NoSuchUserException("No user with given Id"))
		.when(tenantServiceMock)
		.updateTenant((String)tenant.get("id"), tenant);
		
		mockMvc.perform(put("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isMethodNotAllowed());
		
	}
	
	@Test
	public void updateShouldReturnNoUserExceptionWhenIdIsNotFound() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "fdjklasfkjlads");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		
		doThrow(new NoSuchUserException("No user with given Id"))
		.when(tenantServiceMock)
		.updateTenant((String)tenant.get("id"), tenant);
		
		mockMvc.perform(put("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldUpdateTenantInfoSuccessfully() throws Exception {		
		JSONObject tenant = new JSONObject();
		tenant.put("id", "sampleid");
		tenant.put("firstName", "samplefname");
		tenant.put("lastName", "samplelname");
		tenant.put("password", "samplepassword");
		tenant.put("email", "sampleemail");
		
		doNothing()
		.when(tenantServiceMock)
		.updateTenant((String)tenant.get("id"), tenant);
		
		mockMvc.perform(put("/v1/tenant/{tenantId}", (String) tenant.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant))
        ).andExpect(status().isOk());
	}
}
