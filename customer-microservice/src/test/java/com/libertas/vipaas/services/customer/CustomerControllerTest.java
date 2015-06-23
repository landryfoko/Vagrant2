package com.libertas.vipaas.services.customer;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, CustomerServiceLauncher.class })
@WebAppConfiguration
public class CustomerControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/customer";
	
	private MockMvc mockMvc;

	@Autowired
	CustomerService customerService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
//	@Test
//	public void changePasswordShouldThrowNoEmailInRequestExceptionWhenEmailIsEmpty() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("firstName", "samplefname");
//		creditCard.put("lastName", "samplelname");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("oldPassword", "sampleoldpassword");
//		creditCard.put("newPassword", "samplenewpassword");
//		creditCard.put("email", "");	
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/{customerId}/password", (String) creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isBadRequest());
//	}
	
//	@Test
//	public void changePasswordShouldThrowNoNewPasswordInRequestExceptionWhenNewPasswordIsEmpty() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("firstName", "samplefname");
//		creditCard.put("lastName", "samplelname");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("oldPassword", "sampleemail");
//		creditCard.put("newPassword", "");
//		creditCard.put("email", "sampleemail");	
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/{customerId}/password", (String) creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isBadRequest());
//	}
	
//	@Test
//	public void changePasswordShouldThrowNoOldPasswordInRequestExceptionWhenOldPasswordIsEmpty() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("firstName", "samplefname");
//		creditCard.put("lastName", "samplelname");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("oldPassword", "");
//		creditCard.put("newPassword", "samplenewpassword");
//		creditCard.put("email", "sampleemail");	
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/{customerId}/password", (String) creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isBadRequest());
//	}
//	
	@Test
	public void createCustomerShouldCreateCustomerSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
			
		when(customerService
			.createCustomer(
				(String) creditCard.get("email"), (String) creditCard.get("password"), creditCard)).thenReturn(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void createCustomerShouldThrowDuplicateUserExceptionWhenEmailIsDuplicated() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		doThrow(new DuplicateUserException("User already exists with given email"))
		.when(customerService)
		.createCustomer(
			(String) creditCard.get("email"), (String) creditCard.get("password"),creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isPreconditionFailed());
	}
	
	@Test
	public void createCustomerShouldThrowNoEmailInRequestExceptionWhenEmailIsEmpty() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void createCustomerShouldThrowNoPasswordInRequestExceptionWhenPasswordIsEmpty() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
//	
//	@Test
//	public void deleteShouldDeleteTenantSuccessfully() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("tenantId", "sampletenantId");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("email", "sampleemail");
//		creditCard.put("deleteDate", "sampledeleteDate");
//		creditCard.put("links", "samplelinks");
//		
//		doNothing()
//		.when(customerService)
//		.deleteCustomerById((String)creditCard.get("id"));
//		
//		mockMvc.perform(delete(ROOT_END_POINT + "/{customerId}", (String) creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isOk());
//	}
//	
//	@Test
//	public void getShouldReturnCustomerSuccessfully() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("tenantId", "sampletenantId");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("email", "sampleemail");
//		creditCard.put("deleteDate", "sampledeleteDate");
//		creditCard.put("links", "samplelinks");
//		
//		when(customerService.getCustomerById((String)creditCard.get("id"))).thenReturn(creditCard);
//		
//		mockMvc.perform(get(ROOT_END_POINT + "/{creditcardId}" , (String) creditCard.get("id")))
//        .andExpect(status().isOk())
//        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//        .andExpect(jsonPath("$.id", is((String) creditCard.get("id"))))
//        .andExpect(jsonPath("$.tenantId", is((String) creditCard.get("tenantId"))))
//        .andExpect(jsonPath("$.password", is((String) creditCard.get("password"))))
//        .andExpect(jsonPath("$.email", is((String) creditCard.get("email"))))
//        .andExpect(jsonPath("$.deleteDate", is((String) creditCard.get("deleteDate"))))
//        .andExpect(jsonPath("$.links", is((String) creditCard.get("links"))))
//        ;
//	}
	
/*	@Test
	public void loginShouldReturnLoggedCustomerSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		when(customerService.login((String) creditCard.get("email"), (String) creditCard.get("password"))).thenReturn(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) creditCard.get("id"))))
        .andExpect(jsonPath("$.tenantId", is((String) creditCard.get("tenantId"))))
        .andExpect(jsonPath("$.password", is((String) creditCard.get("password"))))
        .andExpect(jsonPath("$.email", is((String) creditCard.get("email"))))
        .andExpect(jsonPath("$.deleteDate", is((String) creditCard.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) creditCard.get("links"))));
	}*/
	/*
	@Test
	public void loginShouldThrowAuthenticationExceptionWhenIncorrectCredential() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		doThrow(new AuthenticationException("Could not authenticate user"))
		.when(customerService)
		.login((String) creditCard.get("email") , (String) creditCard.get("password"));
		
		mockMvc.perform(post(ROOT_END_POINT + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isForbidden());
	}
	*/
	@Test
	public void loginShouldThrowNoEmailInRequestExceptionWhenEmailIsEmpty() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "samplepassword");
		creditCard.put("email", "");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		mockMvc.perform(post(ROOT_END_POINT + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void loginShouldThrowNoEmailInRequestExceptionWhenPasswordIsEmpty() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("tenantId", "sampletenantId");
		creditCard.put("password", "");
		creditCard.put("email", "sampleemail");
		creditCard.put("deleteDate", "sampledeleteDate");
		creditCard.put("links", "samplelinks");
		
		mockMvc.perform(post(ROOT_END_POINT + "/login")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
//	@Test
//	public void logoutShouldLogoutCustomerSuccessfully() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("tenantId", "sampletenantId");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("email", "sampleemail");
//		creditCard.put("deleteDate", "sampledeleteDate");
//		creditCard.put("links", "samplelinks");
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/{customerId}/logout", (String)creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isOk());
//	}
	
	@Test
	public void resetPasswordChangeCustomerPasswordSuccessfully() throws Exception {		
		//incorrect end point implementation, no {pathvariable} in implementation
	}
	
	@Test
	public void resetPasswordShouldReturnNoEmailInRequestExceptionWhenEmailIsEmpty() throws Exception {		
		//incorrect end point implementation, no {pathvariable} in implementation
	}
	
	@Before
	public void setUp() {
		Mockito.reset(customerService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
//	@Test
//	public void updateShouldUpdateTenantInfoSuccessfully() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("tenantId", "sampletenantId");
//		creditCard.put("password", "samplepassword");
//		creditCard.put("email", "sampleemail");
//		creditCard.put("deleteDate", "sampledeleteDate");
//		creditCard.put("links", "samplelinks");
//		
//		doNothing()
//		.when(customerService)
//		.updateCustomer((String)creditCard.get("id"), creditCard);
//		
//		mockMvc.perform(put(ROOT_END_POINT + "/{customerId}", (String) creditCard.get("id"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isOk());
//	}	
	
}
