package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such offer")  // 404
public class NoSuchOfferException extends Exception{
	public NoSuchOfferException(String string) {
		super(string);
	}
}
