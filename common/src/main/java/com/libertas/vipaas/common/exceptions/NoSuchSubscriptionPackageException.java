package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such subscription package")  // 404
public class NoSuchSubscriptionPackageException extends Exception{
	public NoSuchSubscriptionPackageException(String string) {
		super(string);
	}
}
