package com.libertas.vipaas.services.recommendation;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchRecommendationException;

public interface RottenTomatoesRecommendationService {
    JSONObject getRecommendationByProductId(final String recommendationId) throws NoSuchRecommendationException, NoSuchProductException;
}
