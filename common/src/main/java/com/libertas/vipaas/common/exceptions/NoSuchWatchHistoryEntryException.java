package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such watch histiry entry")  // 404
public class NoSuchWatchHistoryEntryException extends Exception{
	public NoSuchWatchHistoryEntryException(String string) {
		super(string);
	}
}
