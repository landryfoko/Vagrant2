package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such product")  // 404
public class NoSuchRecommendationException extends Exception{
	public NoSuchRecommendationException(String string) {
		super(string);
	}
}
