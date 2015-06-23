package com.libertas.vipaas.services.creditcard;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.CreditCardProcessingFailure;
import com.libertas.vipaas.common.exceptions.MissingFieldException;
import com.libertas.vipaas.common.exceptions.NoSuchCreditCardException;
import com.libertas.vipaas.common.exceptions.NoSuchProductException;

public interface CreditCardService {
		JSONObject addCreditCard(JSONObject  metadata) throws NoSuchProductException, MissingFieldException, CreditCardProcessingFailure;
		JSONObject getCreditCardById(String creditcardId) throws NoSuchCreditCardException;
		void updateCreditCard(String creditcardId,JSONObject  metadata) throws NoSuchCreditCardException;
		void deleteCreditCardById(String creditcardId) throws NoSuchCreditCardException, CreditCardProcessingFailure;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
