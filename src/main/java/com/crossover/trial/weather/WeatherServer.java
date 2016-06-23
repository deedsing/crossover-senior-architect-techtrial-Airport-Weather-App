package com.crossover.trial.weather;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.crossover.trial.weather.resources.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.resources.RestWeatherQueryEndpoint;


/**
 * <h1>A Local Test Server</h1>
 * 
 * <p>
 * A main method used to test the Weather Application locally -- live deployment is to a tomcat container.
 *
 * @author code test administrator
 */
public class WeatherServer {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final Logger log = Logger.getLogger(WeatherServer.class);
    public static void main(String[] args) {
        try {
        	new WeatherServer().startServer();
        } catch (IOException | InterruptedException ex) {
        	log.fatal(ex);
        }

    }

	public  void startServer() throws IOException, InterruptedException {
		log.info("Starting Weather App local testing server: " + BASE_URL);
		log.info("Not for production use");

		final ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(RestWeatherCollectorEndpoint.class);
		resourceConfig.register(RestWeatherQueryEndpoint.class);
		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    @Override
		    public void run() {
		        server.shutdownNow();
		    }
		}));

		HttpServerProbe probe = new HttpServerProbe.Adapter() {
		    public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
		        System.out.println(request.getRequestURI());
		    }
		};

		server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);
		server.start();
		 log.info(format("Weather Server started.\n url=%s\n", BASE_URL));

		Thread.currentThread().join();
	}
	
	public void stopServer(){
		
		log.info("Stoping Weather App local testing server: " + BASE_URL);
		log.info("Not for production use");
		final ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(RestWeatherCollectorEndpoint.class);
		resourceConfig.register(RestWeatherQueryEndpoint.class);
		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);
		server.shutdownNow();
	}
}
