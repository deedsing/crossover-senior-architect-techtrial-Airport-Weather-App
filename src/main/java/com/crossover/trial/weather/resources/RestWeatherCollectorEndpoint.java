package com.crossover.trial.weather.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.crossover.trial.weather.exception.DataPointException;
import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.model.ErrorResponse;
import com.google.gson.Gson;

/**
 * <h1>Weather Collector Resource</h1>
 * 
 * <p>
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 * 
 *
 * @author deedsing
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollector {
    public final static Logger log = Logger.getLogger(RestWeatherCollectorEndpoint.class);

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();
   
    /*This data is accessed by both rest resources we can move it to a static class called MemoryData ? */
    public static Map<String,AirportData>  mapAirportData= new HashMap<String,AirportData>();
    public static Map<String ,AtmosphericInformation> mapAtmosphericInformation = new HashMap<String,AtmosphericInformation>();
    
	/**
	 * Just a check if the service is up.
	 * It returns 1 if the service is running , I can't return 0 if it is down :)
	 * 
	 * @return 1 
	 */
	@GET
    @Path("/ping")
	public Response ping() {
		log.debug("Ping check ok");
        return Response.status(Response.Status.OK).entity(1).build();
          
	}
	
	/**
	 * This method will create new weather DataPoint which corresponds to a specific weather information.
	 * @param iataCode The IATA code of the airport.
	 * @param pointType String representation of enum DataPointType.
	 * @param datapointJson JSON formated String of DataPoint object type.
	 * 
	 * @return Success --> HTTP Created Response (DataPoint object)
	 * @return Failure --> Custom Error Response (ErrorResponse object)
	 * 
	 */
    @POST
    @Path("/weather/{iata}/{pointType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
    	iataCode = iataCode.toUpperCase();
        try {
        	log.debug("Request Receive to update weather information of type "+pointType +"for Airport: "+iataCode);
        	log.debug("DataPoint : "+datapointJson);
            addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
           
        } catch (WeatherException e) {
        	e.printStackTrace();
        	log.error("Request failed and an is exception raised --> " +e);
        	 return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("NOT_ACCEPTABLE",e.toString())).build();
            
        }
        log.debug("Success :" + Response.Status.CREATED);
        return Response.status(Response.Status.CREATED).entity(datapointJson).build();
    }

    
    /**
     * This method retrieves list of available airports.
     * @return list of IATA codes of available airports .
     */
    @Override
	@GET
	@Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAirports() {
		
		List<String> list = new ArrayList<String>();
		list.addAll(mapAirportData.keySet());
		log.debug("Resource requested to print all the known airports.");
		  log.debug("Success :" + Response.Status.OK);
		 return Response.status(Response.Status.OK).entity(list).build();
	}
    
    /**
     * This method retrieves information about an airport.
     * @param iataCode The IATA code of the airport.
     * 
     * @param  Success --> JSON Response (AirportData).
     * @return Failure --> Custom Error Response (ErrorResponse object).
     * 
     */
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirport(@PathParam("iata") String iata) {
    	log.debug("Request to retrieve information about airport "+ iata);
          AirportData airport = mapAirportData.get(iata.toUpperCase());
          if(airport!=null){
        	  airport.updateFrequency();
        	  log.debug("Success :" + Response.Status.OK);
		 return Response.status(Response.Status.OK).entity(airport).build();
          }else if(iata.length()==3){
        	  ErrorResponse error = new ErrorResponse("NOT_ACCEPTABLE","No Airport with IATA code "+iata+" exists.");
        	  log.debug("Failure :" + Response.Status.NOT_ACCEPTABLE +": "+error.toString());
            	  return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();} 
              else{
            	  ErrorResponse error = new ErrorResponse("BAD_REQUEST","Invalid IATA code. IATA code is 3-letter code");
            	  log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
            	  return Response.status(Response.Status.BAD_REQUEST).entity(error).build(); }
    }
   
    /**
     * Creates a new Airport object.
     * @param iata The IATA code of the airport.
     * @param lat  Latitude dimension for the airport.
     * @param long Longitude dimension for the airport.
     * 
     * @return Success --> HTTP Created Response and (AirportData Object)
	 * @return Failure --> Custom Error Response (ErrorResponse object)
     */
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
    	log.debug("Request to create an airport.");
    	try {
			AirportData airport = new AirportData.Builder().withIATA(iata).withLatitude(Double.parseDouble(latString)).withLongitude(Double.parseDouble(longString)).build();
			mapAirportData.put(iata, airport);
			log.debug("Success :" + Response.Status.CREATED);
			log.debug("Airport --> "+airport.toString());
			return Response.status(Response.Status.CREATED).entity(airport).build();
	    	
		} catch (ModelException e) {
			e.printStackTrace();
        	  log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+e.toString());
			return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("BAD_REQUEST",e.toString())).build();
			
		}catch (Exception e) {
			log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+e.toString());
			return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("BAD_REQUEST",e.toString())).build();
			
		}
       
    }

    /** Creates a new Airport object.
    * @param airport AirportData Object.
    * 
    * @return Success --> HTTP Created Response and (AirportData object)
	* @return Failure --> Custom Error Response (ErrorResponse object)
    * 
	*/
    @POST
    @Path("/airport")
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addAirport(AirportData airport) {
    	log.debug("Request to create an airport.");
    	    try {
    	    	
				airport.validateState();
				mapAirportData.put(airport.getIata(), airport);
			} catch (ModelException e) {
				e.printStackTrace();
	        	  log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+e.toString());
				return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("BAD_REQUEST",e.toString())).build();
				
			}catch (Exception e) {
				log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+e.toString());
				return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("BAD_REQUEST",e.toString())).build();
				
			}
    		log.debug("Success :" + Response.Status.CREATED);
			log.debug("Airport --> "+airport.toString());
			return Response.status(Response.Status.CREATED).entity(airport).build();
	    	      
    }
    
    /**
     * This method deletes an airport.
     * @param iataCode The IATA code of the airport.
     * 
     * @param  Success --> HTTP Accepted Response
     * @return Failure --> Custom Error Response (ErrorResponse object).
     * 
     */
    @DELETE
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
    	log.debug("Request to delete the airport "+ iata);
          AirportData airport = mapAirportData.get(iata.toUpperCase());
          if(airport!=null){
        	  mapAirportData.remove(iata);
        		log.debug("Success :" + Response.Status.ACCEPTED);
		 return Response.status(Response.Status.ACCEPTED).build();
          }
          else if(iata.length()==3){
        	  ErrorResponse error = new ErrorResponse("NOT_ACCEPTABLE","No Airport with IATA code "+iata+" exists.");
        	  log.debug("Failure :" + Response.Status.NOT_ACCEPTABLE +": "+error.toString());
            	  return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build();} 
              else{
            	  ErrorResponse error = new ErrorResponse("BAD_REQUEST","Invalid IATA code. IATA code is 3-letter code");
            	  log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
            	  return Response.status(Response.Status.BAD_REQUEST).entity(error).build(); }
    }

    

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    private void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        
    	iataCode = iataCode.toUpperCase();
        AtmosphericInformation ai = mapAtmosphericInformation.get(iataCode);
        if( mapAirportData.get(iataCode)!=null){
        if(ai!=null)
        updateAtmosphericInformation(ai, pointType, dp);
        else{
        	// create new AtmosphericInformation object 
        	ai= new AtmosphericInformation();
        	updateAtmosphericInformation(ai, pointType, dp);
        	mapAtmosphericInformation.put(iataCode, ai);
        }
        }else{
        	throw new WeatherException("System did not allowed to create Atmospheric information as no valid airport is registered with iata code "+iataCode);
  	  }
    }

    /**
     * update atmospheric information with the given data point {@link DataPoint}, for the given point type {@link DataPointType}}
     *
     * @param ai the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp the actual data point
     */
    private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.WIND.name()+dp.toString());
        }

        else if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.TEMPERATURE.name()+dp.toString());
        }

        else if (pointType.equalsIgnoreCase(DataPointType.HUMIDITY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.HUMIDITY.name()+dp.toString());
        }

        else if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.PRESSURE.name()+dp.toString());
        }

        else if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.CLOUDCOVER.name()+dp.toString());
        }

        else  if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >=0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }else
            	throw new DataPointException(DataPointType.PRECIPITATION.name()+dp.toString());
        }
        else
        	throw new WeatherException("You may be providing a wrong data header. Supported headers are : "+java.util.Arrays.asList(DataPointType.values()));
    }


}
