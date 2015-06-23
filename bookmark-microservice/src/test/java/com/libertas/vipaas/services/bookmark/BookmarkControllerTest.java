package com.libertas.vipaas.services.bookmark;

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

import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchBookmarkException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, BookmarkServiceLauncher.class })
@WebAppConfiguration
public class BookmarkControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/bookmark";
	
	private MockMvc mockMvc;

	@Autowired
	BookmarkService bookmarkService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Test
	public void deleteBookmarkByIdShouldDeleteBookmarkSuccessfully() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
		
		doNothing()
		.when(bookmarkService)
			.deleteBookmarkById((String)bookmark.get("id"));
		
		mockMvc.perform(delete(ROOT_END_POINT + "/{bookmarkId}", (String) bookmark.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmark))
        ).andExpect(status().isOk());
	}
	
	
	@Test
	public void updateBookmarkShouldUpdateBookmarkSuccessfully() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
		
		doNothing()
		.when(bookmarkService)
		.updateBookmark((String)bookmark.get("id"), bookmark);
		
		mockMvc.perform(put(ROOT_END_POINT + "/{bookmarkId}", (String) bookmark.get("id"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmark))
        ).andExpect(status().isOk());
	}
	
	@Test
	public void findAllShouldReturnAllSubscriptionPackageSuccessfully() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
		
		when(bookmarkService.findAll(10,10,null,null)).thenReturn(bookmark);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) bookmark.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) bookmark.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) bookmark.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) bookmark.get("productId"))))
        ;
	}
	
	
	
	@Test
	public void getBookmarkByIdShouldReturnNoSuchBookmarkExceptionWhenBookMarkNotExist() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
	
		doThrow(new NoSuchBookmarkException("No Such Bookmark"))
		.when(bookmarkService)
			.getBookmarkById((String)bookmark.get("id"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{bookmarkId}" , (String) bookmark.get("id")))
        .andExpect(status().isNotFound());
	}
	
	@Test
	public void getBookmarkByIdShouldReturnBookmarkSuccessfully() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
	
		when(bookmarkService.getBookmarkById((String)bookmark.get("id"))).thenReturn(bookmark);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{bookmarkId}" , (String) bookmark.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) bookmark.get("id"))))
        .andExpect(jsonPath("$.customerId", is((String) bookmark.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) bookmark.get("tenantId"))))
        .andExpect(jsonPath("$.productId", is((String) bookmark.get("productId"))));
	}
	
//	@Test
//	public void createBookmarkShouldReturnBookmarkSuccessfully() throws Exception {		
//		JSONObject bookmark = new JSONObject();
//		bookmark.put("id", "sampleid");
//		bookmark.put("customerId", "samplecustomerid");
//		bookmark.put("tenantId", "samplenantid");
//		bookmark.put("productId", "sampleproductid");
//	
//		doNothing().when(bookmarkService)
//			.createBookmark(
//				(String) bookmark.get("productId"), bookmark);
//		
//		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) bookmark.get("productId"))
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(bookmark))
//        ).andExpect(status().isOk());
//	}
	
	
	@Test
	public void createBookmarkShouldReturnNoSuchProductExceptionWhenProductNotExist() throws Exception {		
		JSONObject bookmark = new JSONObject();
		bookmark.put("id", "sampleid");
		bookmark.put("customerId", "samplecustomerid");
		bookmark.put("tenantId", "samplenantid");
		bookmark.put("productId", "sampleproductid");
	
		doThrow(new NoSuchProductException("No such product"))
		.when(bookmarkService)
			.createBookmark(
				(String) bookmark.get("productId"), bookmark);
		
		mockMvc.perform(post(ROOT_END_POINT + "/product/{productId}", (String) bookmark.get("productId"))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmark))
        ).andExpect(status().isNotFound());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(bookmarkService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}	
	
}
