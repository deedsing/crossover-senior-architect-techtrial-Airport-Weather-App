package com.crossover.trial.weather.exception;

/**
 * <h1>WeatherException</h1>
 * <p>
 * This exception is throw if the weather information is not correct.
 * This is also an example of generic exception.
 * @author deedsing
 *
 */
public class WeatherException extends Exception {

	private static final long serialVersionUID = -3725075907235088927L;

	public WeatherException(String e) {
		super(e);
	}
	
}
