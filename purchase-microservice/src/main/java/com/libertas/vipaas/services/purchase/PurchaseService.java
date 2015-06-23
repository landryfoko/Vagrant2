package com.libertas.vipaas.services.purchase;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateOfferException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface PurchaseService {
		JSONObject getPurchaseById(String purchaseId) throws NoSuchOfferException, NoSuchProductException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);
		JSONObject purchase(JSONObject details) throws MissingFieldException, NoSuchOfferException, NoSuchProductException;
		
}
