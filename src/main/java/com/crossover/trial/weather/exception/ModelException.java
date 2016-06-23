package com.crossover.trial.weather.exception;

/**<h1>ModelException</h1>
 * <p>
 * This exception is thrown by model classes, that is, classes belonging to (com.crossover.trial.weather.model) if the object is not valid.
 * This is an example of Generic Exception.
 * @author deedsing
 *
 */
public class ModelException extends Exception{

	private static final long serialVersionUID = -881366138652567876L;
	public ModelException(String e){
		super(e);
	}
	
}
