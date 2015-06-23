package com.libertas.vipaas.services.recommendation;

import java.util.List;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateRecommendationException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRecommendationException;

public interface RecommendationService {
		JSONObject createRecommendation(String productId,List<JSONObject>  list) throws  DuplicateRecommendationException;
		JSONObject getRecommendationByProductId(String recommendationId) throws NoSuchRecommendationException, NoSuchProductException;
		void updateRecommendation(String recommendationId,JSONObject  metadata) throws NoSuchRecommendationException;
		void deleteRecommendationByProductId(String productId) throws NoSuchRecommendationException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
