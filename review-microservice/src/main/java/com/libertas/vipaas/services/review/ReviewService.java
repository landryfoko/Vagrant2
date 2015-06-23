package com.libertas.vipaas.services.review;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchReviewException;

public interface ReviewService {
	JSONObject createReview(String productId,JSONObject  metadata) throws NoSuchProductException;
		JSONObject getReviewById(String reviewId) throws NoSuchReviewException;
		void updateReview(String reviewId,JSONObject  metadata) throws NoSuchReviewException;
		void deleteReviewById(String reviewId) throws NoSuchReviewException;
		JSONObject findAll(String productId, Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
