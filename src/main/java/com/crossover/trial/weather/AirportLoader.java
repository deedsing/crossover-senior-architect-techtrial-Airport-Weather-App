package com.crossover.trial.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.crossover.trial.weather.client.AirportClient;
import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AirportData.DST;

/**<h1>AirportLoader</h1>
 * <p>
 * A simple airport loader which reads a file from disk and sends records to the resource endpoint.
 * This loader will load a file , which will be provided as java command line argument.
 *
 * 
 * @author deedsing 
 */
public class AirportLoader {

	private static Logger log = Logger.getLogger(AirportLoader.class);

  

    public AirportLoader() {       
    }

    public void upload(InputStream airportDataStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        String l = null;
		AirportClient restClient = new AirportClient();
        try {
        while ((l = reader.readLine()) != null) {
        	String [] arr = l.split(",");

        	/* example file row --- > 1,"General Edward Lawrence Logan Intl","Boston","United States","BOS","KBOS",42.364347,-71.005181,19,-5,"A" */        	
        	
        	String name = arr[1].replace("\"","");
        	String iata=arr[4].replace("\"","") ;
    		Double latitude=Double.parseDouble(arr[6].replace("\"",""));
    		Double longitude=Double.parseDouble(arr[7].replace("\"",""));
    		String city=arr[2].replace("\"","");
    		String country= arr[3].replace("\"","");
    		Double altitude=Double.parseDouble(arr[8].replace("\"",""));
    		String icao=arr[5].replace("\"","");
    		Double timezone= Double.parseDouble(arr[9].replace("\"",""));
    		DST dst=DST.valueOf(arr[10].replace("\"",""));
				
        	
        	
        	AirportData airport = new AirportData.Builder().withName(name).withIATA(iata).withLatitude(latitude).withLongitude(longitude).withCity(city).withCountry(country).withAltitude(altitude).withICAO(icao).withTimezone(timezone).withDST(dst).build();
	
			restClient.populate(airport);
			} 
        }catch (NumberFormatException e) {
			log.info("Exception occured , System was not able to load airport :"+l);	
        	log.error(e);
			
	    } catch (ModelException e) {
	    	log.info("Exception occured , System was not able to load airport :"+l);	
	    	log.error(e);
				
		}catch (ArrayIndexOutOfBoundsException e) {
			log.error("Exception occured , System was not able to load airport :"+l);	
	    	log.error(e);
		} catch (IOException e) {
			log.info("Exception occured , System was not able to load airport :"+l);	
				log.error(e);
				
			}
        }
    

    public static void main(String args[]) throws IOException{
       log.info("Starting the load of Airports ...");
       log.debug("File --> "+args[0]);
    	
       File airportDataFile = new File(args[0]);
       if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            log.error("Not able to find file at path: "+airportDataFile);
            log.info("Exception occured program halted abnormally , please check the logs. ");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        log.info("Loading of Airport data is complete.");
        System.exit(0);
    }
}
