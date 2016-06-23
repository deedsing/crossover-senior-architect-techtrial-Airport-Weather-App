package com.crossover.trial.weather.model;

/**
 * <h1>Weather Data Types</h1>
 * 
 * <p>
 * These are various supported Data types that can be loaded into the system and corresponds to a specific weather information.
 * Each Data point is taken as a Math Quantile object and has a set of five distinct readings.
 * {@link DataPoint}.
 * </p>
 * 
 * 
 *
 * @author code test administrator
 */
public enum DataPointType {
    WIND,
    TEMPERATURE,
    HUMIDITY,
    PRESSURE,
    CLOUDCOVER,
    PRECIPITATION
}
