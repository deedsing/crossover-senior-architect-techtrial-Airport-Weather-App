-----------------------------
Readme
-----------------------------

Rest implementation of Airport Weather Application system , aka AWA.
Final project with implementaion for POST and DELETE REST endpoint
and also the implementaion of airport loader , helper main class to
load airports.

Run this application for sometime and then try 
http://localhost:8080/query/hist/radius  
http://localhost:8080/query/hist/iata   
More information on these urls can be found below.

-----------------------------
Maven Instruction 
-----------------------------
Before running a maven build , please check on the system the port 
8080 is not being used . The test cases will fail .

-----------------------------
AirportLoader Main class
-----------------------------
args[0] --> file/path/of/airports.txt
File format :
1,"General Edward Lawrence Logan Intl","Boston","United States","BOS","KBOS",42.364347,-71.005181,19,-5,"A"
2,"Newark Liberty Intl","Newark","United States","EWR","KEWR",40.6925,-74.168667,18,-5,"A" 

-----------------------------
Rest URL Informations
-----------------------------

-----------------------------
Description : Get list of available IATA codes of airports.
-----------------------------
Request : GET 
URL : http://localhost:8080/collect/airports/
HTTP Response:OK (200)
Output:
[
  "EWR",
  "LCY",
  "MMU",
  "BOS",
  "LGA",
  "LHR",
  "LTN",
  "LPL",
  "STN",
  "JFK"
]

-----------------------------
Description : Get Details of an airport.
-----------------------------
Request : GET 
URL : http://localhost:8080/collect/airport/{String iata}
Example :http://localhost:8080/collect/airport/EWR
HTTP Response: OK (200),NOT_ACCEPTABLE,BAD_REQUEST
Output:
{
  "frequencyCount": 2,
  "iata": "EWR",
  "name": "Newark Liberty Intl",
  "latitude": 40.6925,
  "longitude": -74.168667,
  "city": "Newark",
  "country": "United States",
  "altitude": 18,
  "icao": "KEWR",
  "timezone": -5,
  "dst": "A"
}

Custom ErrorResponse:

Condition : Airport iata code doesnot exist
{
  "error": "NOT_ACCEPTABLE",
  "message": "No Airport with IATA code EWX exists."
}

Condition : IATA code invalid.
{
  "error": "BAD_REQUEST",
  "message": "Invalid IATA code. IATA code is 3-letter code"
}




-----------------------------
Description : Delete an airport.
-----------------------------
Request : Delete 
URL : http://localhost:8080/collect/airport/{String iata}
Example :http://localhost:8080/collect/airport/EWR
HTTP Response:Accepted (202)
Output: 

Custom ErrorResponse:

Condition : Airport iata code doesnot exist
{
  "error": "NOT_ACCEPTABLE",
  "message": "No Airport with IATA code EWX exists."
}


-----------------------------
Description : Create an airport
-----------------------------
Request : POST 
URL : http://localhost:8080/collect/airport/{String iata}/{String latitude}/{String longitude} 
Example :http://localhost:8080/collect/airport/IXC/33.33/55.55
HTTP Response:Created (201)
Output: 
{
  "frequencyCount": 0,
  "iata": "IXC",
  "name": null,
  "latitude": 33.33,
  "longitude": 55.55,
  "city": null,
  "country": null,
  "altitude": null,
  "icao": "",
  "timezone": null,
  "dst": null
}

Custom ErrorResponse:


Condition : IATA code invalid.
{
  "error": "BAD_REQUEST",
  "message": "Invalid IATA code. IATA code is 3-letter code"
}


-----------------------------
Description : Create an airport
-----------------------------
Request : POST 
URL : http://localhost:8080/collect/airport/ 
Example :http://localhost:8080/collect/airport/
Input : Airport object
{
  "frequencyCount": 5,
  "iata": "EWR",
  "name": "Newark Liberty Intl",
  "latitude": 40.6925,
  "longitude": -74.168667,
  "city": "Newark",
  "country": "United States",
  "altitude": 18,
  "icao": "KEWR",
  "timezone": -5,
  "dst": "A"
}
HTTP Response:Created (201)
Output: If successfull same object is returned.
{
  "frequencyCount": 5,
  "iata": "EWR",
  "name": "Newark Liberty Intl",
  "latitude": 40.6925,
  "longitude": -74.168667,
  "city": "Newark",
  "country": "United States",
  "altitude": 18,
  "icao": "KEWR",
  "timezone": -5,
  "dst": "A"
}

Custom ErrorResponse:

Condition : Model Exceptions (IATA code invalid. ICAO not valid) 
{
  "error": "BAD_REQUEST",
  "message": "Invalid IATA code. IATA code is 3-letter code"
}
{
  "error": "BAD_REQUEST",
  "message": "com.crossover.trial.weather.exception.ModelException: icao code is invalid , icao code must be four letter word"
}


-----------------------------
Description : Get Health status Collect endpoint.
-----------------------------
Request : GET 
URL : http://localhost:8080/collect/ping/
Output: 1 (Note : Ideally a json response should be return , but as per the API interface it should return 1)
HTTP Response:OK (200)

-----------------------------
Description : Get Health status Query Endpoint
-----------------------------
Request : GET 
URL : http://localhost:8080/query/ping/
HTTP Response:OK (200)
Output:
{
  "iata_freq": {
    "EWR": 1.2,
    "LCY": 0,
    "MMU": 0.8,
    "BOS": 0.4,
    "LGA": 0.5,
    "LHR": 0,
    "LTN": 0,
    "LPL": 0,
    "STN": 0,
    "JFK": 0.7
  },
  "radius_freq": {
    "0": 1,
    "32": 3,
    "66": 17,
    "86": 2,
    "100": 4,
    "2332": 10
  },
  "datasize": 1
}


-----------------------------
Description : Post weather information
-----------------------------
Request : POST 
URL : http://localhost:8080/collect/weather/mmu/humidity
Example :http://localhost:8080/collect/weather/mmu/humidity
Input : DataPoint object
{"mean":0.0,"first":0,"second":4,"third":10,"count":10}
HTTP Response:Created (201) , NOT_ACCEPTABLE
Output: If successfull same object is returned.
{"mean":0.0,"first":0,"second":4,"third":10,"count":10}

Custom ErrorResponse:

Condition : Model Exceptions (IATA code invalid) 

{
  "error": "NOT_ACCEPTABLE",
  "message": "com.crossover.trial.weather.exception.WeatherException: You may be providing a wrong data header. Supported headers are : [WIND, TEMPERATURE, HUMIDITY, PRESSURE, CLOUDCOVER, PRECIPITATION]"
}
{
  "error": "NOT_ACCEPTABLE",
  "message": "com.crossover.trial.weather.exception.WeatherException: System did not allowed to create Atmospheric information as no valid airport is registered with iata code MFFMU"
}
{
  "error": "NOT_ACCEPTABLE",
  "message": "com.crossover.trial.weather.exception.DataPointException: WIND[mean=-20.0,first=0,second=4,third=10,count=10]"
}


-----------------------------
Description : Get nearby weather information 
-----------------------------
Request : GET 
URL : http://localhost:8080/query/weather/{String iata}/{String radius}
EX : http://localhost:8080/query/weather/BOS/20
HTTP Response:OK (200), BAD_REQUEST(400)
Output:List of Atmospheric object 
[
  {
    "temperature": null,
    "wind": {
      "mean": 20,
      "first": 0,
      "second": 4,
      "third": 10,
      "count": 10
    },
    "humidity": null,
    "precipitation": null,
    "pressure": null,
    "cloudCover": null,
    "lastUpdateTime": 1458058941024
  }
]

Error:
{
  "error": "BAD_REQUEST",
  "message": "The search confines only to earth radius , if You are an alien and wants to know your airport location , please contact crossover support they will provide you the correct url for your favourite destination :-) "
}
{
  "error": "BAD_REQUEST",
  "message": "The search confines only to earth radius , if You are an microbe and wants to know your airport location , please contact crossover support they will provide you the correct url for your favourite destination :-) "
}
{
  "error": "NumberFormatException",
  "message": "Radius is a decimal number in KM, please provide a valid input"
}

-----------------------------
Description : Graphical respresentation of IATA frequency. (Extras)
-----------------------------
Request : GET 
URL : http://localhost:8080/query/hist/iata
HTTP Response:OK (200)
Output:

IATA   : Histogram
EWR    : ############
LCY    : 
MMU    : ########
BOS    : ####
LGA    : #####
LHR    : 
LTN    : 
LPL    : 
STN    : 
JFK    : #######

-----------------------------
Description : Graphical respresentation of Radius frequency. (Extras)
-----------------------------
Request : GET 
URL : http://localhost:8080/query/hist/radius
HTTP Response:OK (200)
Output:
Radius(KM) : Histogram
0    : #
32    : ###
66    : #################
100    : ####
86    : ##
2332    : ##########


----------------------
Feedback on Assignment
----------------------
The assignment was good.
A detailed high level description of various rest endpoints should have been provided ,


