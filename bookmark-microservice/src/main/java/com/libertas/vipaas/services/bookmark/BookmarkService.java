package com.libertas.vipaas.services.bookmark;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchBookmarkException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface BookmarkService {
		JSONObject createBookmark(String productId,JSONObject  metadata) throws NoSuchProductException;
		JSONObject getBookmarkById(String bookmarkId) throws NoSuchBookmarkException, NoSuchProductException;
		void updateBookmark(String bookmarkId,JSONObject  metadata) throws NoSuchBookmarkException, NoSuchProductException;
		void deleteBookmarkById(String bookmarkId) throws NoSuchBookmarkException, NoSuchProductException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
