package com.libertas.vipaas.common.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CorsFilter implements Filter{


	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse rs=(HttpServletResponse)response;
			rs.addHeader("Access-Control-Max-Age", "3600");
			rs.addHeader("Access-Control-Allow-Origin", "*");
			rs.addHeader("Allow","GET, PUT, DELETE, POST, OPTIONS");
			rs.addHeader("Access-Control-Allow-Methods","GET, PUT, DELETE, POST");
			rs.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH");
		    rs.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
			chain.doFilter(request, rs);

	}

	public void destroy() {
	}


}