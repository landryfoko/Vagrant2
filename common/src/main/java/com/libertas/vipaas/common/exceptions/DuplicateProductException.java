package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_FAILED, reason="Product already exists")  
public class DuplicateProductException extends Exception {


	public DuplicateProductException(String message) {
		super(message);
	}


}
