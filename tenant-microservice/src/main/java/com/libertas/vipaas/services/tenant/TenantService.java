package com.libertas.vipaas.services.tenant;

import java.util.List;

import org.json.simple.*;

import com.libertas.vipaas.common.exceptions.AuthenticationException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

public interface TenantService {

	JSONObject createTenant(String email, String password, JSONObject properties) throws DuplicateUserException, AuthenticationException, MissingFieldException;
	void updateTenant(String tenantId, JSONObject properties) throws NoSuchUserException;
	JSONObject getTenantById(String tenantId) throws NoSuchUserException;
	void   deleteTenantById(String tenantId) throws NoSuchUserException;
	void addAdministrators(String string, List<String> emails) throws NoSuchUserException;
	List<JSONObject> findTenantByOwnerId() throws NoSuchUserException;
	void removeAdministrators(String tenantId, List<String> emails)
			throws NoSuchUserException;
}
