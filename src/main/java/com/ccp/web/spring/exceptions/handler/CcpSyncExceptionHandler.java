package com.ccp.web.spring.exceptions.handler;

import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.process.CcpFlow;
import com.ccp.validation.CcpJsonInvalid;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class CcpSyncExceptionHandler {

	public static Function<Throwable, CcpJsonRepresentation> genericExceptionHandler;
 
	@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
	@ExceptionHandler({ CcpJsonInvalid.class })
	public Map<String, Object> handle(CcpJsonInvalid e) {
		return e.result.content;
	}

	@ResponseBody
	@ExceptionHandler({ CcpFlow.class })
	public Map<String, Object> handle(CcpFlow e, HttpServletResponse res){
		
		res.setStatus(e.status.status());
		
		String message = e.getMessage();
		
		CcpJsonRepresentation result = 
				CcpConstants.EMPTY_JSON.put("msg", message);
		
		if(e.fields.length <= 0) {
			return result.content;
		}
		
		CcpJsonRepresentation subMap = e.json.getJsonPiece(e.fields);
		
		CcpJsonRepresentation putAll = result.putAll(subMap);
		
		return putAll.content;
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ Throwable.class })
	public void handle(Throwable e) {
		if(genericExceptionHandler == null) {
			throw new RuntimeException("genericExceptionHandler must has an instance ", e);
		}
		genericExceptionHandler.apply(e);
	}
	
//	@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
//	@ExceptionHandler({ org.springframework.web.HttpRequestMethodNotSupportedException.class })
	public void methodNoSupported() {
		
	}
}
