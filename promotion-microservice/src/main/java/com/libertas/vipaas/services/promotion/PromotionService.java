package com.libertas.vipaas.services.promotion;

import java.util.List;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicatePromotionException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchPromotionException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface PromotionService {
	JSONObject createPromotion(String productId,JSONObject  metadata) throws  DuplicatePromotionException;
		JSONObject getPromotionById(String promotionId) throws NoSuchPromotionException;
		void updatePromotion(String promotionId,JSONObject  metadata) throws NoSuchPromotionException;
		void deletePromotionById(String promotionId) throws NoSuchPromotionException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String tag, String sortField, String sortOrder);
		void untagPromotion(String promotionId, List<String> tags) throws NoSuchPromotionException, MissingFieldException;
		void tagPromotion(String promotionId, List<String> tags) throws NoSuchPromotionException, MissingFieldException;

}
