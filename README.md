Project Title: Zimo - A web-based application prototype for a Portable Charger Sharing System


Project Description: Zimo is a web-based application prototype to demonstrate a shared power bank system in Greater London. It provides an interactive map interface which will allow users to locate, rent and return portable chargers.


Front End - HTML, CSS, JavaScript, Google Maps API, Places API
Back End - Java, JAX-RS (Jersey), RESTful API
Server - Apache Tomcat


Software:
NetBeans IDE 18
Postman

Inside NetBeans please double check the project properties:
1. Right click the project folder
2. Properties > Compile > Java Platform: JDK 17
3. Properties > Run > Server: Apache Tomcat & Java EE Version: Java EE 8 Web


Replace the Google Maps API Key in index.html



Inside Postman please use this URL:
http://localhost:8080/Zimo/api/


Endpoint Examples:
POST /stations  (Create Stations)    
POST /chargers  (Create chargers)
GET /chargers   (Get all chargers)
GET /stations/1 (Get station 1)
GET /stations/1/chargers (Get all chargers in station 1)
POST /chargers/1/rent   (Rent charger with ID 1)

Sample JSON Requests for testing:
Create new station (POST /stations)

{
    "name": "Leicester Square",
    "lat": 51.5104,
    "lng": -0.1301,
    "address": "75 Charing Cross Road",
    "postcode": "WC2H 0BF",
    "status": "active"
}

Create new chargers (POST /chargers)

{
    "stationId": 1,
    "chargerType": "USB-C",
    "status": "available"
}



An An Yu - University of Westminster 2026
