package com.libertas.vipaas.services.entitlement;

import java.util.Collection;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchEntitlementException;
import com.libertas.vipaas.common.exceptions.NoSuchOfferException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface EntitlementService {
	   	void createEntitlement(String productId,String purchaseOptionId, JSONObject  metadata) throws NoSuchProductException, NoSuchOfferException;
		JSONObject findById(String entitlementId) throws NoSuchEntitlementException;
		void updateEntitlement(String entitlementId,JSONObject  metadata) throws NoSuchEntitlementException;
		JSONObject findAll(String customerId,Integer pageSize,Integer pageNumber, Collection<String> productIds, String sortField, String sortOrder);
		JSONObject findValidOne(String productId) throws NoSuchEntitlementException, NoSuchProductException;
		void disableEntitlement(String entitlementId, Long disableDate)	throws NoSuchEntitlementException;

}
