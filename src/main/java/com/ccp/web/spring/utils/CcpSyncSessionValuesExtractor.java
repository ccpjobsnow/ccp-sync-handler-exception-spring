package com.ccp.web.spring.utils;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

import jakarta.servlet.http.HttpServletRequest;

public interface CcpSyncSessionValuesExtractor {
	default CcpJsonRepresentation getSessionValues(HttpServletRequest request, String email) {
		
		String sessionToken = request.getHeader("sessionToken");
		String userAgent = request.getHeader("User-Agent");
		String ip = request.getHeader("Host");

		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("sessionToken", sessionToken)
				.put("userAgent", userAgent)
				.put("email", email)
				.put("ip", ip)
				;
		return put;
	}

}
