package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such promotion")  // 404
public class NoSuchPromotionException extends Exception{
	public NoSuchPromotionException(String string) {
		super(string);
	}
}
