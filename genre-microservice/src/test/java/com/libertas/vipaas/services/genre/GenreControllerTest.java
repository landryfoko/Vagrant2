package com.libertas.vipaas.services.genre;

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

import com.libertas.vipaas.common.exceptions.DuplicateGenreException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchGenreException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebAppContext.class, GenreServiceLauncher.class })
@WebAppConfiguration
public class GenreControllerTest{
	
	private static final String ROOT_END_POINT = "/v1/genre";
	
	private MockMvc mockMvc;

	@Autowired
	GenreService genreService;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
		
	@Test
	public void findAllShouldReturnAllGenreSuccessfully() throws Exception {		
		JSONObject genre = new JSONObject();
		genre.put("id", "sampleid");
		genre.put("deviceId", "sampledeviceId");
		genre.put("name", "samplename");
		genre.put("customerId", "samplecustomerId");
		genre.put("tenantId", "sampletenantId");
		genre.put("deleteDate", "sampledeleteDate");
		genre.put("links", "samplelinks");
		
		when(genreService.findAll(10,10,null,null)).thenReturn(genre);
		
		mockMvc.perform(get(ROOT_END_POINT + "/findAll?" +"pageSize={pageSize}&pageNumber={pageNumber}", 10, 10))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) genre.get("id"))))
        .andExpect(jsonPath("$.deviceId", is((String) genre.get("deviceId"))))
        .andExpect(jsonPath("$.name", is((String) genre.get("name"))))
        .andExpect(jsonPath("$.customerId", is((String) genre.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) genre.get("tenantId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) genre.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) genre.get("links"))))
        ;
	}
	
	@Test
	public void getGenreByIdShouldReturnGenreSuccessfully() throws Exception {		
		JSONObject genre = new JSONObject();
		genre.put("id", "sampleid");
		genre.put("deviceId", "sampledeviceId");
		genre.put("name", "samplename");
		genre.put("customerId", "samplecustomerId");
		genre.put("tenantId", "sampletenantId");
		genre.put("deleteDate", "sampledeleteDate");
		genre.put("links", "samplelinks");
		
		when(genreService.getGenreById((String)genre.get("id"))).thenReturn(genre);
		
		mockMvc.perform(get(ROOT_END_POINT + "/{genreId}", (String) genre.get("id")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id", is((String) genre.get("id"))))
        .andExpect(jsonPath("$.deviceId", is((String) genre.get("deviceId"))))
        .andExpect(jsonPath("$.name", is((String) genre.get("name"))))
        .andExpect(jsonPath("$.customerId", is((String) genre.get("customerId"))))
        .andExpect(jsonPath("$.tenantId", is((String) genre.get("tenantId"))))
        .andExpect(jsonPath("$.deleteDate", is((String) genre.get("deleteDate"))))
        .andExpect(jsonPath("$.links", is((String) genre.get("links"))));
	}
	
	@Test
	public void getGenreByIdShouldThrowNoSuchGenreExceptionWhenGenreNotFound() throws Exception {		
		JSONObject genre = new JSONObject();
		genre.put("id", "sampleid");
		genre.put("deviceId", "sampledeviceId");
		genre.put("name", "samplename");
		genre.put("customerId", "samplecustomerId");
		genre.put("tenantId", "sampletenantId");
		genre.put("deleteDate", "sampledeleteDate");
		genre.put("links", "samplelinks");
	
		when(genreService.getGenreById((String)genre.get("id")))
		.thenThrow(new NoSuchGenreException("No Such Genre"));
		
		mockMvc.perform(get(ROOT_END_POINT + "/{genreId}", (String) genre.get("id")))
        .andExpect(status().isNotFound());
	}
	
	@Before
	public void setUp() {
		Mockito.reset(genreService);		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
}
