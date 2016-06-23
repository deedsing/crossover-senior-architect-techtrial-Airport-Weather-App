package com.crossover.trial.weather.exception;

/**<h1>DataPointException</h1>
 * <p>
 * This exception is thrown if the data point is not valid for a particular Weather type.
 * This class is an example of very specific exception.
 * @author deedsing
 *
 */
public class DataPointException extends WeatherException{

	private static final long serialVersionUID = -6743386270878245804L;

	public DataPointException(String e){
		super(e);
	}
}
