package com.libertas.vipaas.services.genre;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateGenreException;
import com.libertas.vipaas.common.exceptions.NoSuchGenreException;

public interface GenreService {
		JSONObject createGenre(String name, JSONObject  metadata) throws DuplicateGenreException ;
		JSONObject getGenreById(String genreId) throws NoSuchGenreException;
		void updateGenre(String genreId,JSONObject  metadata) throws NoSuchGenreException;
		void deleteGenreById(String genreId) throws NoSuchGenreException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
