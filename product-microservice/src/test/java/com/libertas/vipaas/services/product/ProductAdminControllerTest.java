package com.libertas.vipaas.services.product;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
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
public class ProductAdminControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/admin/product";
	
	private MockMvc mockMvc;

	@Autowired
	ProductService productService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void addGenreToProductShouldAddGenreToProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.addGenreToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	

	@Test
	public void addGenreToProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("genres"))
		.when(productService)
			.addGenreToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addGenreToProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addGenreToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void addImageToProductShouldAddImageToProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void addImageToProductShouldThrowMissingFieldExceptionWhenImageHeightIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","sampleimageWidth");
		product.put("imageHeight","");
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
		
		doThrow(new MissingFieldException("imageHeight"))
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addImageToProductShouldThrowMissingFieldExceptionWhenImageTypeIsEmpty() throws Exception {		
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
		product.put("imageType","");
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
		
		doThrow(new MissingFieldException("imageType"))
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addImageToProductShouldThrowMissingFieldExceptionWhenImageURLIsEmpty() throws Exception {		
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
		product.put("imageURL","");
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
		
		doThrow(new MissingFieldException("imageURL"))
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addImageToProductShouldThrowMissingFieldExceptionWhenImageWidthIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("imageWidth","");
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
		
		doThrow(new MissingFieldException("imageWidth"))
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addImageToProductShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addImageToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/image", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void addOfferToProductShouldAddOfferToProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.addOfferToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void addOfferToProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("offers"))
		.when(productService)
			.addOfferToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addOfferToProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addOfferToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void addPreviewToProductShouldAddPreviewToProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing().when(productService)
			.addPreviewToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/preview", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void addPreviewToProductShouldThrowMissingFieldExceptionWhenAspectRatioIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("aspectRation"))
		.when(productService)
			.addPreviewToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/preview", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addPreviewToProductShouldThrowMissingFieldExceptionWhenScreenFormatIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("screenFormat"))
		.when(productService)
			.addPreviewToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/preview", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addPreviewToProductShouldThrowMissingFieldExceptionWhenTargetDeviceIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("targetDevice"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addPreviewToProductShouldThrowMissingFieldExceptionWhenURLIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspertRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("url"))
		.when(productService)
			.addPreviewToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/preview", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addPreviewToProductShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addPreviewToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/preview", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void addTagToProductShouldAddProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing().when(productService)
			.addTagToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void addTagToProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		
		doThrow(new MissingFieldException("tags"))
		.when(productService)
			.addTagToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addTagToProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("tags"))
		.when(productService)
			.addTagToProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addVideoToProductShouldAddVideoSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing().when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void addVideoToProductShouldThrowMissingFieldExceptionWhenAspectRatioIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("aspectRation"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addVideoToProductShouldThrowMissingFieldExceptionWhenScreenFormatIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectratio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("screenFormat"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addVideoToProductShouldThrowMissingFieldExceptionWhenTargetDeviceIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectratio");
		product.put("targetDevice", "");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("targetDevice"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addVideoToProductShouldThrowMissingFieldExceptionWhenURLIsEMpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectratio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new MissingFieldException("url"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addVideoToProductShouldThrowNoSuchProductExceptionWhenProductNotFound() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addVideoToProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/video", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void createShouldCreateProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
		
		doNothing().when(productService)
		.createProduct(
			(String) product.get("bindId"), product);
	
		mockMvc.perform(post(ROOT_END_POINT + "/{bindId}", (String) product.get("bindId"))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(product))
			).andExpect(status().isOk());
	}
	
	@Test
	public void deleteProductByIdShouldDeleteProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing()
		.when(productService)
			.removeTagFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void deleteProductByIdShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.deleteProductById(
				(String) product.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void removeGenreFromProductShouldRemoveGenreFromProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.removeGenreFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	

	@Test
	public void removeGenreFromProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("genres"))
		.when(productService)
			.removeGenreFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void removeGenreFromProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.removeGenreFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void removeNamedTagFromProductShouldRemoveNamedTagFromProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.removeNamedTagFromProduct(
				(String) product.get("id"), tags, "tags");
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/genre", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void removeNamedTagFromProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("tags"))
		.when(productService)
			.removeNamedTagFromProduct(
					(String) product.get("id"), tags, "tags");
			
			mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/{name}", (String) product.get("id"), "tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}	
	
	
	@Test
	public void removeNamedTagFromProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.removeNamedTagFromProduct(
				(String) product.get("id"), tags, "tags");
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/{name}", (String) product.get("id"), "tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void removeOfferFromProductShouldRemoveOfferFromProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.removeOfferFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void removeOfferFromProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("offers"))
		.when(productService)
			.removeOfferFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void removeOfferFromProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.removeOfferFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/offer", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Test
	public void removeTagFromProductShouldRemoveTagFromProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing().when(productService)
			.removeTagFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void removeTagFromProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		
		doThrow(new MissingFieldException("tags"))
		.when(productService)
			.removeTagFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	

	@Test
	public void removeTagFromProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.removeTagFromProduct(
				(String) product.get("id"), tags);
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{productId}/tag", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(productService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void updateProductShouldUpdateProductSuccessfully() throws Exception {		
		JSONObject product = new JSONObject();
		product.put("id", "sampleid");
		product.put("bindId", "samplebindId");
		product.put("tenantId", "sampletenantId");
		product.put("numFound", "samplenumFound");
		product.put("blob", "sampleblob");
		product.put("details", "sampledetails");
		product.put("images", "sampleimages");
		product.put("previews", "samplepreviews");
		product.put("videos", "samplevideos");
		product.put("tags", "sampletags");
		product.put("aspectRatio", "sampleaspectRatio");
		product.put("targetDevice", "sampletargetDevice");
		product.put("screenFormat", "samplescreenFormat");
		product.put("url", "sampleurl");
	
		List<String> tags = new ArrayList<String>();
		tags.add("sampletag1");
		tags.add("sampletag2");
		
		doNothing()
		.when(productService)
			.updateProduct(
				(String) product.get("id"), product);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{productId}", (String) product.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(product))
        ).andExpect(status().isOk());
	}
	
	
	
	@Test
	public void addNamedTagToProductShouldAddTagToProductSuccessfully() throws Exception {		
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
		
		doNothing()
		.when(productService)
			.addNamedTagToProduct(
				(String) product.get("id"), tags, "tags");
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/{name}", (String) product.get("id"), "tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isOk());
	}
	

	@Test
	public void addNamedTagToProductShouldThrowMissingFieldExceptionWhenTagsRequestIsEmpty() throws Exception {		
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
		
		doThrow(new MissingFieldException("genres"))
		.when(productService)
			.addNamedTagToProduct(
				(String) product.get("id"), tags, "tags");
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/{name}", (String) product.get("id"), "tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isBadRequest());
	}
	
	@Test
	public void addNamedTagToProductShouldThrowNoSuchProductExceptionWhenProductNotExist() throws Exception {		
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
		
		doThrow(new NoSuchProductException("No such product"))
		.when(productService)
			.addNamedTagToProduct(
				(String) product.get("id"), tags, "tags");
		
		mockMvc.perform(post(ROOT_END_POINT + "/{productId}/{name}", (String) product.get("id"), "tags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tags))
        ).andExpect(status().isNotFound());
	}
}
