package com.surevine.alfresco.gateway;

public class GatewayException extends RuntimeException {

	private static final long serialVersionUID = 8855249878905196389L;

	public GatewayException(String message) {
		super(message);
	}
	
	public GatewayException(String message, Throwable cause) {
		super(message, cause);
	}
}
