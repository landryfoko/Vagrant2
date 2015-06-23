package com.libertas.vipaas.services.rating;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRatingException;

public interface RatingService {
		JSONObject createRating(String productId,JSONObject  metadata) throws NoSuchProductException;
		JSONObject getRatingById(String ratingId) throws NoSuchRatingException;
		void updateRating(String ratingId,JSONObject  metadata) throws NoSuchRatingException;
		void deleteRatingById(String ratingId) throws NoSuchRatingException;
		JSONObject findAll(String productId, Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
