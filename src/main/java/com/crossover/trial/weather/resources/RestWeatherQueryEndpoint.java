package com.crossover.trial.weather.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.ErrorResponse;
import com.google.gson.Gson;

/**<h1>Weather Query Resource </h1>
 * 
 * <p>
 * This rest resource implements public API WeatherQueryEndpoint and provide information about the currents statistics of Weather data
 * the application holds.
 * 
 * This resource allows its users to obtain weather forecasts for Airports ,weather DataPoints held in memory, the frequency check of airports and frequency check of weather 
 * information in a particular geographical radius from a particular airport.
 * 
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger log = Logger.getLogger(RestWeatherQueryEndpoint.class);

    /** earth radius in KM */
    public static final double R = 6372.8;

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();

    public static Map<Integer, Integer> radiusFreq = new HashMap<Integer, Integer>();
  

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @GET
    @Path("/ping")
    public String ping() {
    	log.debug("Health checking .... ");
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        
        
        for (Map.Entry<String, AtmosphericInformation> entry1 : RestWeatherCollectorEndpoint.mapAtmosphericInformation.entrySet())
        {
        	AtmosphericInformation ai = entry1.getValue();
        	 if (ai.getCloudCover() != null
                     || ai.getHumidity() != null
                     || ai.getPressure() != null
                     || ai.getPrecipitation() != null
                     || ai.getTemperature() != null
                     || ai.getWind() != null) {
                     // updated in the last day
                     if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                         datasize++;
                     }
              
        }
        	 }
        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        
        for (Map.Entry<String, AirportData> entry : RestWeatherCollectorEndpoint.mapAirportData.entrySet())
        {
              AirportData data = entry.getValue();
              double frac = (double) data.getFrequencyCount() / RestWeatherCollectorEndpoint.mapAirportData.size();
              freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);
        
        
        /*Radius frequency will keep a count of how many requests has been made for a particular radius.
         * example if radius 22 is accessed 3 times and radius 123 is accessed 4 times  then frequency 
         * for radius 22 is 3 and and frequency of radius 123 is 4.
         * It appears the intern was trying to make a histogram but he left it in the middle it doesnot work.
         * the cold code just creates an empty array set and prints an empty array of int which is initialized to max
         * radius request made .
         * if you make a request say 7000 , system response for ping will fail as it may result in crash.
         * 
         * 
         * */
        retval.put("radius_freq", radiusFreq);

        
        log.debug(retval);
        return gson.toJson(retval);

    }
    
    /**
     * A graphical representation of radius frequency.
     * It does not conform to rest standard , here only for just graphical representation.
     * Ideally a service method should be used.
     * @return
     */
    @GET
    @Path("/hist/radius")
    public Response histForRadiusFreq() {
       
    	StringBuffer buf = new StringBuffer();
    	buf.append("Radius(KM) : Histogram"+"\n");
    	
    	
    	/*In actual production environment if this information is required , then we will need to modify and include check for maximum length of '#' bar to be displayed.*/
        /*The uncommented is just a thought provoking code ... ;-)*/
    	
    	/*int maxValue = RestWeatherQueryEndpoint.radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(0);
    	int length = String.valueOf(maxValue).length();*/
    	
    
        
        for (Map.Entry<Integer, Integer> entry1 : RestWeatherQueryEndpoint.radiusFreq.entrySet())
        {
        	buf.append(entry1.getKey()+"    : "+ getAHashString(entry1.getValue()));
        	buf.append("\n");
              
        }
        	 
       
        return Response.status(Response.Status.OK).entity(buf.toString()).build();

    }
    
    /**
     * A graphical representation of IATA frequency.
     * It does not conform to rest standard , here only for just representation.
     * Ideally a service method should be used.
     * @return
     */
    @GET
    @Path("/hist/iata")
    public Response histForIATAFreq() {
       
    	StringBuffer buf = new StringBuffer();
    	buf.append("IATA   : Histogram"+"\n");
    	
    	
  
         
         for (Map.Entry<String, AirportData> entry : RestWeatherCollectorEndpoint.mapAirportData.entrySet())
         {
               AirportData data = entry.getValue();
               
               /*In actual production environment if this information is required , then we will need to modify and include check for maximum length of '#' bar to be displayed.*/
               /*The uncommented is just a thought provoking code ... ;-)*/
               
              /* double frac = (double) data.getFrequencyCount() / RestWeatherCollectorEndpoint.mapAirportData.size();
               int nearestInt =(int) Math.round(frac);
               buf.append(entry.getKey()+"    : "+ getAHashString(nearestInt));*/
               
               buf.append(entry.getKey()+"    : "+ getAHashString(data.getFrequencyCount()));
               buf.append("\n");
         }
         
         
   
        return Response.status(Response.Status.OK).entity(buf.toString()).build();

    }
    
    /**
     * 
     * @param value
     * @return A string of '#' as per param value supplied
     * Ex:
     * value 8 , returns "########"
     */
    private String getAHashString(Integer value) {
    	StringBuffer buf = new StringBuffer();
		for(int i=value;i>0;i--){
			buf.append('#');
		}
		return buf.toString();
		
	}


	//TODO radius calculation understanding is also pending
    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {
    	iata=iata.toUpperCase();
    	
    	log.debug("Searching for available weather conditions for the airport :"+iata +" with requested radius search : "+radiusString);
    	try{ Double.parseDouble(radiusString);
    	}catch (NumberFormatException e){
    		ErrorResponse error = new ErrorResponse("NumberFormatException","Radius is a decimal number in KM, please provide a valid input");
    		log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
    		return Response.status(Response.Status.BAD_REQUEST).entity(error).build(); 
    		
    	}
    	
    	
    	double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        if(radius>R){
        	ErrorResponse error = new ErrorResponse("BAD_REQUEST","The search confines only to earth radius , if You are an alien and wants to know your airport location , please contact crossover support they will provide you the correct url for your favourite destination :-) ");
    		log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
        	return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }if (radius < 0){
        	ErrorResponse error = new ErrorResponse("BAD_REQUEST","The search confines only to earth radius , if You are an microbe and wants to know your airport location , please contact crossover support they will provide you the correct url for your favourite destination :-) ");
    		log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
        	return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } 
        updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval = new ArrayList<AtmosphericInformation>();
        
        // check iata
        if(iata.length()==3&& RestWeatherCollectorEndpoint.mapAirportData.get(iata)==null){ 	  
        	ErrorResponse error = new ErrorResponse("NOT_ACCEPTABLE","No Airport with IATA code "+iata+" exists.");
      	  log.debug("Failure :" + Response.Status.NOT_ACCEPTABLE +": "+error.toString());
          	  return Response.status(Response.Status.NOT_ACCEPTABLE).entity(error).build(); 
      	  
        }else if(iata.length()!=3){
        	
        	 ErrorResponse error = new ErrorResponse("BAD_REQUEST","Invalid IATA code. IATA code is 3-letter code");
  	  log.debug("Failure :" + Response.Status.BAD_REQUEST +": "+error.toString());
  	  return Response.status(Response.Status.BAD_REQUEST).entity(error).build(); 
        }  else{ // start the computations.
        	
        if (radius == 0) {
            retval.add(RestWeatherCollectorEndpoint.mapAtmosphericInformation.get(iata)); // radius is 0 return the information for the  same airport
        } else {
            AirportData ad = RestWeatherCollectorEndpoint.mapAirportData.get(iata);
            
            /*iterate over map find the nearest airports and get atmospheric information for those airports and add to the list*/
            for (Map.Entry<String, AirportData> entry : RestWeatherCollectorEndpoint.mapAirportData.entrySet())
            {
                  AirportData data = entry.getValue();
                  
                  if (calculateDistance(ad, data) <= radius){
                      AtmosphericInformation ai = RestWeatherCollectorEndpoint.mapAtmosphericInformation.get(data.getIata());
                      if(ai!=null)
                      if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                         || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null){
                          retval.add(ai);
                      }
                  }
                 
            }
          
          
        }
        }
        log.debug("Success :" + Response.Status.OK);
        log.debug("Nearby Weather information "+retval);
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    /**
     * Records information about how often requests are made
     * 
     * @param iata an iata code
     * @param radius query radius
     */
    // CR : Change the modifier of internal methods to private since they are  used by the class internally. 
    private void  updateRequestFrequency(String iata, Double radius) {
    	int nearestIntForRadius= (int) Math.round(radius);
        radiusFreq.put(nearestIntForRadius, radiusFreq.getOrDefault(nearestIntForRadius, 0) +1);
    }


    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    // CR : Change the modifier of internal methods to private since they are  used by the class internally. 
    public static double calculateDistance(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad2.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    
    
}
