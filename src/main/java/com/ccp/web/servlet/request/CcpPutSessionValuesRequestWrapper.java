package com.ccp.web.servlet.request;
 
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpEmailDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.json.transformers.CcpJsonTransformerGenerateFieldHash;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CcpPutSessionValuesRequestWrapper extends HttpServletRequestWrapper {
	
	private final Function<CcpJsonRepresentation, CcpJsonRepresentation> task;
	
	private final HttpServletRequest request;
	
	public CcpPutSessionValuesRequestWrapper(HttpServletRequest request,Function<CcpJsonRepresentation, CcpJsonRepresentation> task) {
		super(request);
		this.request = request;
		this.task = task;
	}

	@SuppressWarnings("unchecked")
	public ServletInputStream getInputStream() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			ServletRequest request = super.getRequest();
			ServletInputStream inputStream = request.getInputStream();
			Map<String, Object> originalJson = mapper.readValue(inputStream, Map.class);
			CcpJsonRepresentation sessionValues = this.getSessionValues(originalJson);
			var fieldHashGenerator = new CcpJsonTransformerGenerateFieldHash("email", "originalEmail");
			CcpJsonRepresentation transformedJson = sessionValues.getTransformedJson(fieldHashGenerator, this.task);
			CcpJsonServletInputStream is = new CcpJsonServletInputStream(transformedJson);
			return is;
		} catch (IOException e) {
			StringBuffer requestURL = this.request.getRequestURL();
			CcpEmailDecorator email = new CcpStringDecorator(requestURL.toString()).email().findFirst("/");
			CcpJsonRepresentation sessionValues = this.getSessionValues(CcpConstants.EMPTY_JSON.content);
			var fieldHashGenerator = new CcpJsonTransformerGenerateFieldHash("email", "originalEmail");
			CcpJsonRepresentation put = sessionValues.put("email", email);
			CcpJsonRepresentation apply = fieldHashGenerator.apply(put);
			CcpJsonServletInputStream is = new CcpJsonServletInputStream(apply);
			return is;
		}
	}

	protected CcpJsonRepresentation getSessionValues() {
		CcpJsonRepresentation sessionValues = this.getSessionValues(CcpConstants.EMPTY_JSON.content);
		return sessionValues;
	}
	
	private CcpJsonRepresentation getSessionValues(Map<String, Object> originalJson) {

		String sessionToken = this.request.getHeader("sessionToken");
		String userAgent = this.request.getHeader("User-Agent");
		String ip = this.request.getHeader("Host");
		
		StringBuffer requestURL = this.request.getRequestURL();
		CcpEmailDecorator email = new CcpStringDecorator(requestURL.toString()).email().findFirst("/");
		CcpJsonRepresentation md = new CcpJsonRepresentation(originalJson);
		CcpJsonRepresentation put = md.put("sessionToken", sessionToken)
				.put("userAgent", userAgent).put("email", email.content).put("ip", ip);
		return put;
	}
}
