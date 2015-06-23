package com.libertas.vipaas.services.bookmark;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.*;

@ControllerAdvice
public class StatusAdvice implements ResponseBodyAdvice {

	@Override
	public Object beforeBodyWrite(Object arg0, MethodParameter arg1,
			MediaType arg2, Class arg3, ServerHttpRequest arg4,
			ServerHttpResponse arg5) {
		System.out.println("\n\n\n\n\n\n\n============Executing status advice================\n\n\n\n\n\n");
		return null;
	}

	@Override
	public boolean supports(MethodParameter arg0, Class arg1) {
		return true;
	}



}