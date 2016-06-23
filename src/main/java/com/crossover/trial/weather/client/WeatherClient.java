package com.crossover.trial.weather.client;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;

/**
 * A reference implementation for the weather client. Consumers of the REST API can look at WeatherClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 *
 * @author code test administrator
 */
public class WeatherClient {
    
	private Logger log = Logger.getLogger(WeatherClient.class);
    private static final String BASE_URI = "http://localhost:8080";
    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    
    public WeatherClient() {
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

    public void pingQuery() {
        WebTarget path = query.path("/ping");
        Response response = path.request().get();
        log.debug(path + response.readEntity(String.class) + "\n");
    }

    public void populate() {
        WebTarget path = collect.path("/weather/BOS/wind");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(0).withMedian(4).withCount(10)
                .build();
        Response response = path.request().post(Entity.entity(dp, "application/json"));
        log.debug(path + response.readEntity(String.class) + "\n");
     
    }
    
    public void populate1() {
        WebTarget path = collect.path("/weather/EWR/humidity");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(0).withMedian(4).withCount(10)
                .build();
        Response response = path.request().post(Entity.entity(dp, "application/json"));
        log.debug(path + response.readEntity(String.class) + "\n");
     
    }

    public void query() {
        WebTarget path = query.path("/weather/BOS/0");
        Response response = path.request().get();
        ArrayList<AtmosphericInformation> list = response.readEntity(ArrayList.class);
        System.out.println(list);
       
        //log.debug(path + response.readEntity(String.class) + "\n");
    }

    
   public void radiusCheck() throws ModelException{
		
	   	populate1();  // now NDL has one atmospheric object
	 	 WebTarget path = query.path("/weather/BOS/190");
		 Response response = path.request().get();
		 System.out.println("radiusCheck.get:" + response.readEntity(String.class));
			
	
   }
    public static void main(String[] args) throws ModelException {
       WeatherClient wc = new WeatherClient();
      // wc.pingCollect();
       wc.populate();
       wc.query();
       //wc.pingQuery();
      // wc.radiusCheck();
       //wc.populate1();
       System.out.print("complete");
        System.exit(0);
    }
}
