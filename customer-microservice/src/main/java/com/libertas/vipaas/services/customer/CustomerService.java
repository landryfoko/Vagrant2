package com.libertas.vipaas.services.customer;

import org.json.simple.*;

import com.libertas.vipaas.common.exceptions.AuthenticationException;
import com.libertas.vipaas.common.exceptions.DuplicateUserException;
import com.libertas.vipaas.common.exceptions.NoSuchUserException;

public interface CustomerService {

	JSONObject createCustomer(String email, String password, JSONObject properties) throws DuplicateUserException;
	void updateCustomer(String customerId, JSONObject properties) throws NoSuchUserException;
	JSONObject getCustomerById(String customerId) throws NoSuchUserException;
	void changePassword(String email, String oldPassword, String newPassword) throws AuthenticationException;
	void resetPassword(String email) throws NoSuchUserException;
	void logout(String customerId,JSONObject credentials);
	void   deleteCustomerById(String customerId) throws NoSuchUserException;
	JSONObject getCustomerByEmail(String email) throws NoSuchUserException;
	JSONObject login(String email, String password)throws AuthenticationException;
	JSONObject validateCredentials(String email, String password)throws AuthenticationException;
}
