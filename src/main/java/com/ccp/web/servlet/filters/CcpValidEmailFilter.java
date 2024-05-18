package com.ccp.web.servlet.filters;

import com.ccp.decorators.CcpStringDecorator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CcpValidEmailFilter implements Filter{
	
	private CcpValidEmailFilter() {}
	
	public static final CcpValidEmailFilter INSTANCE = new CcpValidEmailFilter();
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain){

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEAD, PATCH");
		response.setHeader("Access-Control-Max-Age", "3600");

		response.setHeader("Access-Control-Allow-Headers",
				"Access-Control-Allow-Headers, X-Requested-With, authorization, token, email, Content-Type, Authorization, Access-Control-Request-Methods, Access-Control-Request-Headers");

		String method = request.getMethod();

		boolean optionsMethod = "OPTIONS".equalsIgnoreCase(method);

		if (optionsMethod) {
			return;
		}

		StringBuffer requestURL = request.getRequestURL();
		String url = new CcpStringDecorator(requestURL.toString()).url().asDecoded();
		String filtered = "login/";
		int indexOf = url.indexOf(filtered) + filtered.length();
		String urlSecondPiece = url.substring(indexOf);
		String[] split = urlSecondPiece.split("/");
		String email = split[0];
		boolean invalidEmail = new CcpStringDecorator(email).email().isValid() == false;
		if(invalidEmail) {
			response.setStatus(400);
			return;
		}
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

	}

	
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	
	public void destroy() {
		
	}

}
