package com.crossover.trial.weather.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.crossover.trial.weather.exception.ModelException;
import com.crossover.trial.weather.utils.CustomMethods;


/**<h1>AirportData</h1>
 * <p>
 * A java model class for holding Airport information. 
 * @author deedsing
 *
 */
@XmlRootElement()
public class AirportData {
	
	private int frequencyCount;
	
	/** the three letter IATA code */
    private String iata="";
    private String name;
    public int getFrequencyCount() {
		return frequencyCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** latitude value in degrees */
    private Double latitude;

    /** longitude value in degrees */
    private Double longitude;
    private String city;
    private String country;
    private Double altitude;
    private String icao="";
    
    /**
     * This is the main constructor used to create object for this class.
     * @param name
     * @param iata
     * @param latitude
     * @param longitude
     * @param city
     * @param country
     * @param altitude
     * @param icao
     * @param timezone
     * @param dst
     * @throws ModelException
     */
    public AirportData(String name,String iata, Double latitude, Double longitude, String city, String country, Double altitude,
			String icao, Double timezone, DST dst) throws ModelException {
		super();
		this.name = name;
		this.iata = iata;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.country = country;
		this.altitude = altitude;
		this.icao = icao;
		this.timezone = timezone;
		this.dst = dst;
		validateState();
	}

	private Double timezone;
    private DST dst;
    public DST getDst() {
		return dst;
	}

	public void setDst(DST dst) {
		this.dst = dst;
	}

	public enum DST {E,A,S,O,Z,N,U};

    public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
	}

	public Double getTimezone() {
		return timezone;
	}

	public void setTimezone(Double timezone) {
		this.timezone = timezone;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public AirportData() { }

    /*public AirportData(String iata, String latString, String longString) throws ModelException {
    	this.iata = iata;
		this.latitude = Double.parseDouble(latString);
		this.longitude = Double.parseDouble(longString);
		validateState();
	}*/

	public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public double getLatitude() {
        return latitude;
    }
    
  

    public double getLongitude() {
        return longitude;
    }


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iata == null) ? 0 : iata.hashCode());
		return result;
	}

	
	@Override
    public boolean equals(Object other) {
        if (other instanceof AirportData) {
            return ((AirportData)other).getIata().equals(this.getIata());
        }

        return false;
    }
	
	public void validateState() throws ModelException {
	    StringBuilder ex = new StringBuilder();
	    if( this.iata!=null&&this.iata.length()!=3 && CustomMethods.isAlpha(iata)) {
	      ex.append("Iata code is invalid , IATA code must be three letter word");
	    }
	    if( !this.icao.isEmpty()&&this.icao.length()!=4 &&CustomMethods.isAlpha(iata)) {
		      ex.append("icao code is invalid , icao code must be four letter word");
		    }
	    
	  
	    if ( ! ex.toString().isEmpty() ) throw new ModelException(ex.toString());
	  }

	public synchronized void updateFrequency() {
		frequencyCount++;
		
	}
	
	static public class Builder {
		 private String iata="";
		 private String name;
		 private Double latitude;
		 private Double longitude;
		 private String city;
		 private String country;
		 private Double altitude;
		 private String icao="";
		 private Double timezone;
		 private DST dst;
		 
		 
        public Builder() { }

        public Builder withName(String name) {
            this.name= name;
            return this;
        }

        public Builder withIATA(String iata) {
            this.iata = iata;
            return this;
        }

        public Builder withLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }
        
        public Builder withLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }
        
        public Builder withAltitude(Double altitude) {
            this.altitude = altitude;
            return this;
        }
        
        public Builder withICAO(String icao) {
            this.icao = icao;
            return this;
        }
        public Builder withTimezone(Double timezone) {
            this.timezone = timezone;
            return this;
        }
        
        public Builder withDST(DST dst) {
            this.dst = dst;
            return this;
        }

        public AirportData build() throws ModelException {
            return new AirportData(this.name, this.iata, this.latitude, this.longitude, this.city,this.country,this.altitude,this.icao, this.timezone,this.dst);
        }
    }
	
}
