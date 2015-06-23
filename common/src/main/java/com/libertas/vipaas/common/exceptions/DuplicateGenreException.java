package com.libertas.vipaas.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_FAILED, reason="Genre already exists")  // 404
public class DuplicateGenreException extends Exception {

	public DuplicateGenreException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
