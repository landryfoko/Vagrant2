package com.libertas.vipaas.services.creditcard;

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

import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchBookmarkException;
import com.libertas.vipaas.common.exceptions.NoSuchCreditCardException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, CreditCardServiceLauncher.class })
@WebAppConfiguration
public class CreditCardControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/creditcard";
	
	private MockMvc mockMvc;

	@Autowired
	CreditCardService creditCardService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenFriendlyNameIsMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("friendlyName"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenCreditCardNumberIsMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("creditCardNumber"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenLastNameIsMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("lastName"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenFirstNameIsMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("firstName"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenExpMonthMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("expMonth"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addCreditCardShouldThrowMissingFieldExceptionWhenCVVMissing() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
			
		doThrow(new MissingFieldException("CVV"))
		.when(creditCardService)
			.addCreditCard(creditCard);
		
		mockMvc.perform(post(ROOT_END_POINT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isBadRequest());
	}
	
//	@Test
//	public void addCreditCardShouldUpdateCreditCardSuccessfully() throws Exception {		
//		JSONObject creditCard = new JSONObject();
//		creditCard.put("id", "sampleid");
//		creditCard.put("customerId", "samplecustomerid");
//		creditCard.put("tenantId", "samplenantid");
//		creditCard.put("CVV", "sampleCVV");
//		creditCard.put("expMonth", "sampleexpMonth");
//		creditCard.put("expYear", "sampleexpYear");
//		creditCard.put("type", "sampletype");
//		creditCard.put("firstName", "samplefirstName");
//		creditCard.put("lastName", "samplelastName");
//		creditCard.put("creditCardNumber", "samplecreditCardNumber");
//		creditCard.put("makeDefault", "samplemakeDefault");
//		creditCard.put("friendlyName", "samplefriendlyName");
//		creditCard.put("verifyCard", "sampleverifyCard");
//		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
//		creditCard.put("tokenId", "sampletokenId");
//	
//		doNothing().when(creditCardService)
//			.addCreditCard(creditCard);
//		
//		mockMvc.perform(post(ROOT_END_POINT)
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(creditCard))
//        ).andExpect(status().isOk());
//	}
	
	@Test
	public void getCreditCardByIdShouldThrowNoSuchCreditCardExceptionWhenCardNotFound() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
				
		doThrow(new NoSuchCreditCardException("No Such CreditCard"))
		.when(creditCardService)
			.getCreditCardById((String)creditCard.get("id"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{creditcardId}" , (String) creditCard.get("id")))
        .andExpect(status().isNotFound())
        ;
	}
	
	@Test
	public void getCreditCardByIdShouldReturnCreditCardSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
		
		when(creditCardService.getCreditCardById((String)creditCard.get("id"))).thenReturn(creditCard);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{creditcardId}" , (String) creditCard.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) creditCard.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) creditCard.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) creditCard.get("tenantId"))))
        .andExpect(jsonPath("$.CVV", is((String) creditCard.get("CVV"))))
         .andExpect(jsonPath("$.expMonth", is((String) creditCard.get("expMonth"))))
        .andExpect(jsonPath("$.expYear", is((String) creditCard.get("expYear"))))
        .andExpect(jsonPath("$.type", is((String) creditCard.get("type"))))
        .andExpect(jsonPath("$.firstName", is((String) creditCard.get("firstName"))))
         .andExpect(jsonPath("$.lastName", is((String) creditCard.get("lastName"))))
        .andExpect(jsonPath("$.creditCardNumber", is((String) creditCard.get("creditCardNumber"))))
        .andExpect(jsonPath("$.makeDefault", is((String) creditCard.get("makeDefault"))))
        .andExpect(jsonPath("$.friendlyName", is((String) creditCard.get("friendlyName"))))
         .andExpect(jsonPath("$.verifyCard", is((String) creditCard.get("verifyCard"))))
        .andExpect(jsonPath("$.failOnDuplicatePaymentMethod", is((String) creditCard.get("failOnDuplicatePaymentMethod"))))
        .andExpect(jsonPath("$.tokenId", is((String) creditCard.get("tokenId"))))
        ;
	}
	
	@Test
	public void deleteCreditCardByIdShouldThrowNoSuchCreditCardExceptionWhenCreditCardNotFound() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
		creditCard.put("deleteDate", new Date().toString());
		
		doThrow(new NoSuchCreditCardException("No Such CreditCard"))
		.when(creditCardService)
			.deleteCreditCardById((String)creditCard.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{creditcardId}", (String) creditCard.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteCreditCardByIdShouldDeleteCreditCardSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
		creditCard.put("deleteDate", new Date().toString());
		
		doNothing()
		.when(creditCardService)
			.deleteCreditCardById((String)creditCard.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{creditcardId}", (String) creditCard.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void findAllShouldReturnAllCreditCardSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
		
		when(creditCardService.findAll(10,10,null,null)).thenReturn(creditCard);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is((String) creditCard.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) creditCard.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) creditCard.get("tenantId"))))
        .andExpect(jsonPath("$.CVV", is((String) creditCard.get("CVV"))))
         .andExpect(jsonPath("$.expMonth", is((String) creditCard.get("expMonth"))))
        .andExpect(jsonPath("$.expYear", is((String) creditCard.get("expYear"))))
        .andExpect(jsonPath("$.type", is((String) creditCard.get("type"))))
        .andExpect(jsonPath("$.firstName", is((String) creditCard.get("firstName"))))
         .andExpect(jsonPath("$.lastName", is((String) creditCard.get("lastName"))))
        .andExpect(jsonPath("$.creditCardNumber", is((String) creditCard.get("creditCardNumber"))))
        .andExpect(jsonPath("$.makeDefault", is((String) creditCard.get("makeDefault"))))
        .andExpect(jsonPath("$.friendlyName", is((String) creditCard.get("friendlyName"))))
         .andExpect(jsonPath("$.verifyCard", is((String) creditCard.get("verifyCard"))))
        .andExpect(jsonPath("$.failOnDuplicatePaymentMethod", is((String) creditCard.get("failOnDuplicatePaymentMethod"))))
        .andExpect(jsonPath("$.tokenId", is((String) creditCard.get("tokenId"))))
        ;
	}
	
	@Test
	public void updateCreditCardShouldUpdateCreditCardSuccessfully() throws Exception {		
		JSONObject creditCard = new JSONObject();
		creditCard.put("id", "sampleid");
		creditCard.put("customerId", "samplecustomerid");
		creditCard.put("tenantId", "samplenantid");
		creditCard.put("CVV", "sampleCVV");
		creditCard.put("expMonth", "sampleexpMonth");
		creditCard.put("expYear", "sampleexpYear");
		creditCard.put("type", "sampletype");
		creditCard.put("firstName", "samplefirstName");
		creditCard.put("lastName", "samplelastName");
		creditCard.put("creditCardNumber", "samplecreditCardNumber");
		creditCard.put("makeDefault", "samplemakeDefault");
		creditCard.put("friendlyName", "samplefriendlyName");
		creditCard.put("verifyCard", "sampleverifyCard");
		creditCard.put("failOnDuplicatePaymentMethod", "samplefailOnDuplicatePaymentMethod");
		creditCard.put("tokenId", "sampletokenId");
		
		doNothing()
		.when(creditCardService)
		.updateCreditCard((String)creditCard.get("id"),creditCard);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{creditcardId}", (String) creditCard.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(creditCard))
        ).andExpect(status().isOk());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(creditCardService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
