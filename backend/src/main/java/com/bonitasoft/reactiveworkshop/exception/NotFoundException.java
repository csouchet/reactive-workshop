package com.bonitasoft.reactiveworkshop.exception;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 3352517052931944979L;

	/**
	 * Default constructor
	 */
	public NotFoundException() {
		super();
	}

	/**
	 * Constructor with a message
	 *
	 * @param message
	 *            The message
	 */
	public NotFoundException(final String message) {
		super(message);
	}

	/**
	 * Constructor with a message and a cause
	 *
	 * @param message
	 *            The message
	 * @param cause
	 *            The root exception
	 */
	public NotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
