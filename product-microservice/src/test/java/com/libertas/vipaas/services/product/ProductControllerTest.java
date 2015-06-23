package com.libertas.vipaas.services.product;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, ProductServiceLauncher.class })
@WebAppConfiguration
public class ProductControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/product";
	
	private MockMvc mockMvc;

	@Autowired
	ProductService productService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Before
	public void setUp() {
		Mockito.reset(productService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void getShouldReturnTenantSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","sampleimageWidth");
		product.put("imageHeight","sampleimageHeight");
		product.put("imageType","sampleimageType");
		product.put("imageURL","sampleimageURL");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleUrl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
	
		when(productService.getProductDetails((String)product.get("id"))).thenReturn(product);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{productId}", (String) product.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) product.get("id"))))
        .andExpect(jsonPath("$.bindId", is((String) product.get("bindId"))))
        .andExpect(jsonPath("$.tenantId", is((String) product.get("tenantId"))))
        .andExpect(jsonPath("$.numFound", is((String) product.get("numFound"))))
        .andExpect(jsonPath("$.blob", is((String) product.get("blob"))))
        .andExpect(jsonPath("$.details", is((String) product.get("details"))))
        .andExpect(jsonPath("$.images", is((String) product.get("images"))))
        .andExpect(jsonPath("$.imageWidth", is((String) product.get("imageWidth"))))
        .andExpect(jsonPath("$.imageHeight", is((String) product.get("imageHeight"))))
        .andExpect(jsonPath("$.imageType", is((String) product.get("imageType"))))
        .andExpect(jsonPath("$.imageURL", is((String) product.get("imageURL"))))
        .andExpect(jsonPath("$.previews", is((String) product.get("previews"))))
        .andExpect(jsonPath("$.videos", is((String) product.get("videos"))))
        .andExpect(jsonPath("$.tags", is((String) product.get("tags"))))
        .andExpect(jsonPath("$.aspectRatio", is((String) product.get("aspectRatio"))))
        .andExpect(jsonPath("$.targetDevice", is((String) product.get("targetDevice"))))
        .andExpect(jsonPath("$.screenFormat", is((String) product.get("screenFormat"))))
        .andExpect(jsonPath("$.url", is((String) product.get("url"))))
        ;
	}
	
	@Test
	public void getProductDetailsShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","sampleimageWidth");
		product.put("imageHeight","sampleimageHeight");
		product.put("imageType","sampleimageType");
		product.put("imageURL","sampleimageURL");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleUrl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		when(productService.getProductDetails((String)product.get("id")))
			.thenThrow(new NoSuchProductException("No such product"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{productId}", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void getProductDetailsShouldThrowIllegalStateExceptionWhenOffersNotFound() throws Exception {		
		//not a valid http error response		
	}

	@Test
	public void getProductDetailsShouldThrowIllegalStateExceptionWhenDetailsNotFound() throws Exception {		
		//not a valid http error response		
	}

	@Test
	public void getProductDetailsShouldThrowIllegalStateExceptionWhenVideosNotFound() throws Exception {		
		//not a valid http error response		
	}
	
	@Test
	public void findAllShouldReturnAllProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","sampleimageWidth");
		product.put("imageHeight","sampleimageHeight");
		product.put("imageType","sampleimageType");
		product.put("imageURL","sampleimageURL");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleUrl");
	
		String[] tags = {"tags1","tags2"};
		
		when(productService.findAll(10,10, Arrays.asList(tags),null,null)).thenReturn(product);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}&tags={tags}", 10, 10, 
				Arrays.toString(tags).replace("[", "").replace("]", "")))
	        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is((String) product.get("id"))))
        .andExpect(jsonPath("$.bindId", is((String) product.get("bindId"))))
        .andExpect(jsonPath("$.tenantId", is((String) product.get("tenantId"))))
        .andExpect(jsonPath("$.numFound", is((String) product.get("numFound"))))
        .andExpect(jsonPath("$.blob", is((String) product.get("blob"))))
        .andExpect(jsonPath("$.details", is((String) product.get("details"))))
        .andExpect(jsonPath("$.images", is((String) product.get("images"))))
        .andExpect(jsonPath("$.imageWidth", is((String) product.get("imageWidth"))))
        .andExpect(jsonPath("$.imageHeight", is((String) product.get("imageHeight"))))
        .andExpect(jsonPath("$.imageType", is((String) product.get("imageType"))))
        .andExpect(jsonPath("$.imageURL", is((String) product.get("imageURL"))))
        .andExpect(jsonPath("$.previews", is((String) product.get("previews"))))
        .andExpect(jsonPath("$.videos", is((String) product.get("videos"))))
        .andExpect(jsonPath("$.tags", is((String) product.get("tags"))))
        .andExpect(jsonPath("$.aspectRatio", is((String) product.get("aspectRatio"))))
        .andExpect(jsonPath("$.targetDevice", is((String) product.get("targetDevice"))))
        .andExpect(jsonPath("$.screenFormat", is((String) product.get("screenFormat"))))
        .andExpect(jsonPath("$.url", is((String) product.get("url"))))
        ;
	}

	@Test
	public void findAllShouldThrowMissingFieldExceptionWhenTagsNotFound() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","sampleimageWidth");
		product.put("imageHeight","sampleimageHeight");
		product.put("imageType","sampleimageType");
		product.put("imageURL","sampleimageURL");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleUrl");
	
		String[] tags = {"tags1","tags2"};
				
		when(productService.findAll(10,10, Arrays.asList(tags),null,null)).thenThrow(new MissingFieldException("tags"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}&tags={tags}", 10, 10, 
				Arrays.toString(tags).replace("[", "").replace("]", ""))
	        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
}
