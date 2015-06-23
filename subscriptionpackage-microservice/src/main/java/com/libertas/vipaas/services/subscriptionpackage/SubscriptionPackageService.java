package com.libertas.vipaas.services.subscriptionpackage;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchSubscriptionPackageException;

public interface SubscriptionPackageService {
	   	void createSubscriptionPackage(JSONObject  metadata) throws NoSuchProductException;
		JSONObject getSubscriptionPackageById(String subscriptionPackageId) throws NoSuchSubscriptionPackageException;
		void updateSubscriptionPackage(String subscriptionPackageId,JSONObject  metadata) throws NoSuchSubscriptionPackageException;
		void deleteSubscriptionPackageById(String subscriptionPackageId) throws NoSuchSubscriptionPackageException;
		JSONObject findAll( Integer pageSize,Integer pageNumber);

}
