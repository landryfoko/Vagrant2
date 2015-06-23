package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Such CreditCard")  // 404
public class NoSuchCreditCardException extends Exception {

	public NoSuchCreditCardException(String message) {
		super(message);
	}


}
