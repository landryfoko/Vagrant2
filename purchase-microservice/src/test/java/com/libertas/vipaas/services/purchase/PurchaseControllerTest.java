package com.libertas.vipaas.services.purchase;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, PurchaseServiceLauncher.class })
@WebAppConfiguration
public class PurchaseControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/purchase";
	
	private MockMvc mockMvc;

	@Autowired
	PurchaseService purchaseService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void purchaseShouldPurchaseSuccesffully() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		when(purchaseService.purchase(purchase)).thenReturn(purchase);
		
		mockMvc.perform(post(ROOT_END_POINT)
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(purchase))
		).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is((String) purchase.get("id"))))
        .andExpect(jsonPath("$.offerId", is((String) purchase.get("offerId"))))
        .andExpect(jsonPath("$.productId", is((String) purchase.get("productId"))))
        .andExpect(jsonPath("$.creditCardId", is((String) purchase.get("creditCardId"))))
        .andExpect(jsonPath("$.offers", is((String) purchase.get("offers"))))
        .andExpect(jsonPath("$.price", is((String) purchase.get("price"))))
        .andExpect(jsonPath("$.title", is((String) purchase.get("title"))))
        .andExpect(jsonPath("$.description", is((String) purchase.get("description"))))
        .andExpect(jsonPath("$.item", is((String) purchase.get("item"))))
        .andExpect(jsonPath("$.braintreeId", is((String) purchase.get("braintreeId"))))
        .andExpect(jsonPath("$.braintreeOrderIId", is((String) purchase.get("braintreeOrderIId"))))
        ;
	}
	
	@Test
	public void purchaseShouldThrowMissingFieldExceptionWhenProductIdIsEmpty() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "");
		purchase.put("creditCardId", "samplecreditCardId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		doThrow(new MissingFieldException("Missing productId field in request"))
		.when(purchaseService)
			.purchase(purchase);
		
		mockMvc.perform(post(ROOT_END_POINT)
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(purchase))
		).andExpect(status().isBadRequest())
        ;
	}
	
	@Test
	public void purchaseShouldThrowMissingFieldExceptionWhenOfferIsEmpty() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("offers", "");
		purchase.put("creditCardId", "samplecreditCardId");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		doThrow(new MissingFieldException("Missing offerId field in request"))
		.when(purchaseService)
			.purchase(purchase);
		
		mockMvc.perform(post(ROOT_END_POINT)
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(purchase))
		).andExpect(status().isBadRequest())
        ;
	}	
	
	@Test
	public void purchaseShouldThrowMissingFieldExceptionWhenCreditCardIsEmpty() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("creditCardId", "");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		doThrow(new MissingFieldException("Missing creditCardId field in request"))
		.when(purchaseService)
			.purchase(purchase);
		
		mockMvc.perform(post(ROOT_END_POINT)
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(purchase))
		).andExpect(status().isBadRequest())
        ;
	}	
	
	@Test
	public void purchaseShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("creditCardId", "samplecreditCardId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		doThrow(new NoSuchProductException("No such product"))
		.when(purchaseService)
			.purchase(purchase);
		
		mockMvc.perform(post(ROOT_END_POINT)
        .contentType(TestUtil.APPLICATION_JSON_UTF8)
        .content(TestUtil.convertObjectToJsonBytes(purchase))
		).andExpect(status().isNotFound())
        ;
	}	
	
	@Test
	public void purchaseShouldThrowIllegalStateExceptionWhenOffersNotFound() throws Exception {		
		// not valid http response
	}
	
	@Test
	public void purchaseShouldThrowIllegalStateExceptionWhenOfferNotFound() throws Exception {		
		// not valid http response
	}
	
	@Test
	public void getShouldReturnPurchaseSuccessfully() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		when(purchaseService.getPurchaseById((String)purchase.get("id"))).thenReturn(purchase);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{purchaseId}" , (String) purchase.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) purchase.get("id"))))
        .andExpect(jsonPath("$.offerId", is((String) purchase.get("offerId"))))
        .andExpect(jsonPath("$.productId", is((String) purchase.get("productId"))))
        .andExpect(jsonPath("$.creditCardId", is((String) purchase.get("creditCardId"))))
        .andExpect(jsonPath("$.offers", is((String) purchase.get("offers"))))
        .andExpect(jsonPath("$.price", is((String) purchase.get("price"))))
        .andExpect(jsonPath("$.title", is((String) purchase.get("title"))))
        .andExpect(jsonPath("$.description", is((String) purchase.get("description"))))
        .andExpect(jsonPath("$.item", is((String) purchase.get("item"))))
        .andExpect(jsonPath("$.braintreeId", is((String) purchase.get("braintreeId"))))
        .andExpect(jsonPath("$.braintreeOrderIId", is((String) purchase.get("braintreeOrderIId"))))
        ;
	}
	
	@Test
	public void getPurchaseByIdShouldThrowNoSuchOfferExceptionWhenPurchaseNotFound() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		when(purchaseService.getPurchaseById((String)purchase.get("id"))).thenThrow(new NoSuchOfferException("No Such purchase"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{purchaseId}" , (String) purchase.get("id"))
         .contentType(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNotFound());
        ;
	}
	
	@Test
	public void findAllShouldReturnAllPurchaseSuccessfully() throws Exception {		
		JSONObject purchase = new JSONObject();
		purchase.put("id", "sampleid");
		purchase.put("offerId", "sampleofferId");
		purchase.put("customerId", "sampledeviceId");
		purchase.put("tenantId", "sampletenantId");
		purchase.put("productId", "sampleproductId");
		purchase.put("offers", "sampleoffers");
		purchase.put("price", "sampleprice");
		purchase.put("title", "sampletitle");
		purchase.put("description", "sampledescription");
		purchase.put("item", "sampleitem");
		purchase.put("transactionId", "sampletransactionId");
		purchase.put("braintreeId", "samplebraintreeId");
		purchase.put("braintreeOrderIId", "samplebraintreeOrderIId");
		
		when(purchaseService.findAll(10,10,null,null)).thenReturn(purchase);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) purchase.get("id"))))
        .andExpect(jsonPath("$.offerId", is((String) purchase.get("offerId"))))
        .andExpect(jsonPath("$.customerId", is((String) purchase.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) purchase.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) purchase.get("productId"))))
        .andExpect(jsonPath("$.offers", is((String) purchase.get("offers"))))
        .andExpect(jsonPath("$.price", is((String) purchase.get("price"))))
        .andExpect(jsonPath("$.title", is((String) purchase.get("title"))))
        .andExpect(jsonPath("$.description", is((String) purchase.get("description"))))
        .andExpect(jsonPath("$.item", is((String) purchase.get("item"))))
        .andExpect(jsonPath("$.transactionId", is((String) purchase.get("transactionId"))))
        .andExpect(jsonPath("$.braintreeId", is((String) purchase.get("braintreeId"))))
        .andExpect(jsonPath("$.braintreeOrderIId", is((String) purchase.get("braintreeOrderIId"))))
        ;
	}
	
	@Before
	public void setUp() {
		Mockito.reset(purchaseService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
