package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Such Entitlement") 
public class NoSuchEntitlementException extends Exception {
	public NoSuchEntitlementException(String message){
		super(message);
	}
}
