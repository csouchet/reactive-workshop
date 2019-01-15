package com.bonitasoft.reactiveworkshop.infra;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionHandlers {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(value = NOT_FOUND)
	public void handleNotFoundException(NotFoundException e) {
		// no content
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = INTERNAL_SERVER_ERROR)
	public void handleThrowable(Exception e) {
		log.error("Error while processing", e);
		// no content
	}

}
