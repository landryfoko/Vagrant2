package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such user")  // 404
public class NoSubscriptionPackageException extends Exception{
	public NoSubscriptionPackageException(String string) {
		super(string);
	}
}
