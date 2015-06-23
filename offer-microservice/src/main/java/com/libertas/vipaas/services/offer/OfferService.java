package com.libertas.vipaas.services.offer;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateOfferException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;

public interface OfferService {
		JSONObject createOffer(String name, JSONObject  metadata) throws DuplicateOfferException, MissingFieldException ;
		JSONObject findById(String offerId) throws NoSuchOfferException;
		void updateOffer(String offerId,JSONObject  metadata) throws NoSuchOfferException;
		void deleteOfferById(String offerId) throws NoSuchOfferException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
