package com.crossover.trial.weather;

import com.crossover.trial.weather.client.AirportClient;
import com.crossover.trial.weather.client.WeatherClient;
import com.crossover.trial.weather.exception.DataPointException;
import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.AirportData.DST;
import com.crossover.trial.weather.resources.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.resources.RestWeatherQueryEndpoint;

import com.crossover.trial.weather.resources.WeatherQueryEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

	public WeatherEndpointTest(){
		Client client = ClientBuilder.newClient();
        query = client.target(BASE_URL + "/query");
        collect = client.target("http://localhost:8080/collect");
      
	}
	 /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;
	private static final String BASE_URL = "http://localhost:8080";
    private static final Logger log = Logger.getLogger(WeatherEndpointTest.class);
    private DataPoint _dp;
   
	 static HttpServer server = null;
	@BeforeClass
    public static void  setUp() throws Exception {
		log.info("Starting Weather App local testing server: " + BASE_URL);
		log.info("Not for production use");

		final ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(RestWeatherCollectorEndpoint.class);
		resourceConfig.register(RestWeatherQueryEndpoint.class);
		  server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);


		HttpServerProbe probe = new HttpServerProbe.Adapter() {
		    public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
		        System.out.println(request.getRequestURI());
		    }
		};

		server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);
		server.start();
		 log.info(format("Weather Server started.\n url=%s\n", BASE_URL));  
    
		 AirportLoader loader = new AirportLoader();
		 loader.upload(WeatherEndpointTest.class.getClass().getClassLoader().getSystemResourceAsStream("airports.txt"));
	}
	

	
	@Test
    public void testDataisOK() {
		assertEquals(10, RestWeatherCollectorEndpoint.mapAirportData.size());
		
    }
    
	@Test
    public void testAddAirport() {
		AirportData a1 =null;
		try {
			a1 = new AirportData("Bengaluru Airport","BLR",13.1979, 77.7063, "Bengaluru", "India", 3001.97, "VOBG", 5.5, AirportData.DST.U);
		} catch (ModelException e) {
			log.error(e);
		}
		 WebTarget path = collect.path("/airport");
	        Response response = path.request().post(Entity.entity(a1, "application/json"));
	        log.debug(path+": " + response.readEntity(String.class) + "\n");
		    assertEquals("BLR", RestWeatherCollectorEndpoint.mapAirportData.get("BLR").getIata());
		
    }
	
	
	@Test
    public void testAddAirport2() {

		WebTarget path = collect.path("/airport/"+"IXC"+"/"+"30.4024"+"/"+"76.4719");
        Response response = path.request().post(Entity.entity("", "application/json"));
        log.debug(path+": " + response.readEntity(String.class) + "\n");
		
		assertEquals("IXC", RestWeatherCollectorEndpoint.mapAirportData.get("IXC").getIata());
		
    }
	
	@Test
    public void testDeleteAirport() {
		  WebTarget path = collect.path("/airport/"+"IXC");
	      Response response = path.request().delete();
	      log.debug(path+": " + response.readEntity(String.class) + "\n");
		  assertEquals(null, RestWeatherCollectorEndpoint.mapAirportData.get("IXC"));
		
    }
	
	@Test
    public void testGetAirport() {
		 WebTarget path = collect.path("/airports");
	      Response response = path.request().get();
	      String out=response.readEntity(String.class) ;
	      log.debug(path+": " + out+ "\n");
	      
	      equals(out.contains("EWR"));
    }
	
	/**
	 * Exception test for ModelException
	 * @throws ModelException
	 */
	@Test (expected=ModelException.class) 
	public void throwModelException() throws ModelException{
		log.debug("Model Exception test case is ok");
		 AirportData airportData = new AirportData.Builder().withIATA("DUMMY").build();

	}
	
	 @Test
	 public void testGetWeather0Radius() throws Exception {
		 populateWindDPForEWR();
		 populateHumidityDPForEWR();
		 populateWindDPForBOS();
		 WebTarget path = query.path("/weather/BOS/0");
	     Response response = path.request().get();
	     ArrayList<AtmosphericInformation> list = response.readEntity(ArrayList.class);
	    String validator="wind={mean=0.0, first=0, second=4, third=10, count=10}";
	    log.debug(path+": " + list+ "\n");
		equals(list.contains(validator));
	 
	 }
	 
	 @Test
	 public void testGetWeather200Radius() throws Exception {
		 populateWindDPForEWR();
		 populateHumidityDPForEWR();
		 populateWindDPForBOS();
		 WebTarget path = query.path("/weather/BOS/200");
	     Response response = path.request().get();
	     ArrayList<AtmosphericInformation> list = response.readEntity(ArrayList.class);
	    String validator="wind={mean=0.0, first=0, second=4, third=10, count=10}, humidity={mean=0.0, first=0, second=4, third=10, count=10}";
	    log.debug(path+": " + list+ "\n");
		equals(list.contains(validator));
	 
	 }
	 
	 @Test
	 public void testQueryPing() throws Exception {
		 // invoking radius 14 for 4 times
		 query.path("/weather/BOS/14.44").request().get();
		 query.path("/weather/BOS/14.44").request().get();
		 query.path("/weather/BOS/14.44").request().get();
		 query.path("/weather/BOS/14.44").request().get();
		 
		 // invoking IATA MMU  three times
		 collect.path("/airport/MMU").request().get();
		 collect.path("/airport/MMU").request().get();
		 collect.path("/airport/MMU").request().get();
		 collect.path("/airport/MMU").request().get();
		 
		 WebTarget path = query.path("/ping/");
	     String out = path.request().get().readEntity(String.class);
	     log.debug(path+": " + out+ "\n");
	     equals(out.contains("iata_freq")&& out.contains("radius_freq")&&out.contains("datasize"));
	     
	
	 
	 }
	
	
	 @AfterClass
    public static void tearDown() throws Exception {
		log.info("Shuting down the server: " + BASE_URL);
		log.info("Not for production use");
        server.shutdownNow();
    }

    private void populateWindDPForEWR() {
        WebTarget path = collect.path("/weather/EWR/wind");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(0).withMedian(4).withCount(10)
                .build();
        Response response = path.request().post(Entity.entity(dp, "application/json"));
        log.debug(path + response.readEntity(String.class) + "\n");
     
    }
    
    private void populateWindDPForBOS() {
        WebTarget path = collect.path("/weather/BOS/wind");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(0).withMedian(4).withCount(10)
                .build();
        Response response = path.request().post(Entity.entity(dp, "application/json"));
        log.debug(path + response.readEntity(String.class) + "\n");
     
    }
    
    private void populateHumidityDPForEWR() {
        WebTarget path = collect.path("/weather/EWR/humidity");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(0).withMedian(4).withCount(10)
                .build();
        Response response = path.request().post(Entity.entity(dp, "application/json"));
        log.debug(path + response.readEntity(String.class) + "\n");
     
    }
	

	

}
