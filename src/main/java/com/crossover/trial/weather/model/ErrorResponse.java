package com.crossover.trial.weather.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *  <h1>Custom Error Response</h1>
 *  
 *  <p> This is a custom response class for errors and exception.
 *  For any abnormal condition a JSON response is returned to the user.</p>
 *  <p>
 *  Example:
 *  {
 * "error": "BAD_REQUEST",
 * "message": "Invalid IATA code. IATA code is 3-letter code"
    }
 *  </p>
 * @author deedsing
 *
 */
@XmlRootElement()
public final class ErrorResponse {

	public ErrorResponse(){
		
	}
	private String error;
	private String message;
	public ErrorResponse(String error, String message) {
		super();
		this.error = error;
		this.message = message;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
