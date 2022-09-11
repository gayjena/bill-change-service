package com.gj.billchange.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.gj.billchange.dto.BillChangeServiceResponse;
import com.gj.billchange.exception.BadRequestException;
import com.gj.billchange.exception.ChangeNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BillChangeServiceControllerAdvice {
	
	@ExceptionHandler({BadRequestException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public BillChangeServiceResponse handleBadRequestException(Exception exception) {
		log.info("Invalid request parameters provided. [message = {}]", exception.getMessage());
		BillChangeServiceResponse billChangeServiceResponse = new BillChangeServiceResponse();
		billChangeServiceResponse.setMessage(exception.getMessage());
		billChangeServiceResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		return billChangeServiceResponse;
	}

	@ExceptionHandler(ChangeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public BillChangeServiceResponse handleChangeNotFoundException(ChangeNotFoundException exception) {
		BillChangeServiceResponse billChangeServiceResponse = new BillChangeServiceResponse();
		billChangeServiceResponse.setMessage(exception.getMessage());
		billChangeServiceResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
		return billChangeServiceResponse;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public BillChangeServiceResponse handleAllUncaughtException(Exception exception) {
		log.error("Unknown error occurred", exception);
		BillChangeServiceResponse billChangeServiceResponse = new BillChangeServiceResponse();
		billChangeServiceResponse.setMessage("Internal Server Error");
		billChangeServiceResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return billChangeServiceResponse;
	}

}
