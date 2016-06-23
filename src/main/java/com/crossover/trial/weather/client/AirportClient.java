package com.crossover.trial.weather.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.model.AirportData;

/**
 * <h1>RestClient for Airport</h1>
 * <p>
 * A reference implementation for the airport client. Consumers of the REST API can look at AirportClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 * @author deedsing
 *
 */
public class AirportClient {
	  
	private Logger log = Logger.getLogger(AirportClient.class);
	private static final String BASE_URI = "http://localhost:8080";
	    /** end point for read queries */
	    private WebTarget query;

	    /** end point to supply updates */
	    private WebTarget collect;

	    public AirportClient() {
	        Client client = ClientBuilder.newClient();
	        query = client.target(BASE_URI + "/query");
	        collect = client.target("http://localhost:8080/collect");
	        log.debug("Rest Client configured ... ");
	        log.debug("URL for Query endpoint: "+query);
	        log.debug("URL for Collenct endpoint: "+collect);
	    }

	    public void pingCollect() {
	        WebTarget path = collect.path("/ping");
	        Response response = path.request().get();
	        log.debug(path + response.readEntity(String.class) + "\n");
	    }

	    public void getAirports() {
	        WebTarget path = collect.path("/airports");
	        Response response = path.request().get();
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
	    }
	    
	    public void getAirport(String iata) {
	        WebTarget path = collect.path("/airport/"+iata);
	        Response response = path.request().get();
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
	    }
	    
	    public void deleteAirport(String iata) {
	        WebTarget path = collect.path("/airport/"+iata);
	        Response response = path.request().delete();
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
	    }
	  
	    public void populate(AirportData airport) {
	        WebTarget path = collect.path("/airport");
	        Response response = path.request().post(Entity.entity(airport, "application/json"));
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
	    }
 
	    public void populate(String iata,Double longitude,Double latitude) {
	        WebTarget path = collect.path("/airport/"+iata+"/"+longitude+"/"+latitude);
	        Response response = path.request().post(Entity.entity("", "application/json"));
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
	    }    

	    
	    /*
	     * Uncomment the main method for testing .
	     * Note the Rest should be deployed before you can run this method.
	     */
	    public static void main(String[] args) throws ModelException {
	    	AirportClient airportClient = new AirportClient();
	    	AirportData airport = new AirportData("Bengaluru Airport","BLR",13.1979, 77.7063, "Bengaluru", "India", 3001.97, "VOBG", 5.5, AirportData.DST.U);
	    	airportClient.pingCollect();
	    	airportClient.populate(airport);
	    	airportClient.getAirport("BLR");
	    	airportClient.deleteAirport("BLR");
	    	airportClient.populate("IXC",30.4024,76.4719);
	    	airportClient.getAirports();
	        System.out.print("complete");
	        System.exit(0);
	    }

}

