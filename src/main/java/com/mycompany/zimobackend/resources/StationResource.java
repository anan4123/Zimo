// Code adapted from Yu, A.A. (2025) 'BookStore Application API'.
// Assignment for 5COSC022W.2 Client Server Architecture, 
// BSc Computer Science, University of Westminster. Unpublished
package com.mycompany.zimobackend.resources;

import com.mycompany.zimobackend.exception.StationErrorException;
import com.mycompany.zimobackend.exception.InvalidInputException;
import com.mycompany.zimobackend.model.Station;
import com.mycompany.zimobackend.model.Charger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// Create URL for StationResource - Zimo/api/stations
@Path("/stations")
public class StationResource {
    // Arraylist used to store stations created in current session
    private static List<Station> stations = new ArrayList<Station>();
    private static int nextId = 1;
    
    //Sample station created for testing
    //Displayed on map once loaded
    static {
        Station Station1 = new Station();
        Station1.setName("Holborn");
        Station1.setAddress("271 High Holborn");
        Station1.setPostcode("WC1V 7EE");
        Station1.setId(nextId++);
        Station1.setLat(51.5173);
        Station1.setLng(-0.116);
        stations.add(Station1);
    }
    
    
    // GET Method for all Stations
    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    //Return stations stored in the arraylist
    public List<Station> getAllStations(){
        return stations;
    }
    
    // GET method to return charging stations by ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Station getStationById(@PathParam("id") int id){
        for (Station s : stations){
            if (s.getId() == id){
                return s;
            }
        } //Throw error if the station cannot be found
        throw new StationErrorException("Station with ID " + id + " not found");  
    }
    
    @GET 
    @Path("/{id}/chargers") //Return all chargers at a specific stations
    @Produces(MediaType.APPLICATION_JSON)
    public List <Charger> getChargerByStation(@PathParam("id") int id){
        Station station = stations.stream()
                .filter(s -> s.getId() == id) //Find station with matching ID
                .findFirst()
                //Throw StationErrorException if station can not be found
                .orElseThrow(() -> new StationErrorException("Station with ID " + id + " not found"));
        return ChargerResource.getAllChargers().stream() //Return all chargers
                .filter(charger -> charger.getStationId() == id) //Find chargers that match with stationID
                .collect(Collectors.toList()); 
    }
   
    
    //Creating a new station
    @POST 
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStation(Station station){
        if (station.getName() == null || station.getName().isEmpty()){
            //Throw InvalidInputException if the station name is left empty
            throw new InvalidInputException("Station name cannot be empty");
        }
        if (station.getLat() == 0 || station.getLng() == 0){
            //Throw InvalidInputException if coordinates is zero
            throw new InvalidInputException("Coordinates cannot be zero");
        }
        
        if (station.getAddress() == null || station.getAddress().isEmpty()){
            //Throw InvalidInputException if address is empty
            throw new InvalidInputException("Address cannot be empty");
        }
        
        if (station.getPostcode() == null || station.getPostcode().isEmpty()){
            //Throw InvalidInputException if post code is empty
            throw new InvalidInputException("Postcode cannot be empty");
        }
        
        //Create new station ID and assign to station
        station.setId(nextId++);
        stations.add(station); //Add station to arraylist
        return Response.status(Response.Status.CREATED).entity(station).build();
    
    }
    
    @PUT //PUT method used to update station status
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Station updateStationStatus(@PathParam("id") int id, Station updatedStation){
        Station station_found = stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst()
                .orElseThrow(() -> new StationErrorException("Station " + id + " not found"));
        station_found.setStatus(updatedStation.getStatus());
        return station_found;
    }
    
    @DELETE // DELETE method is used to remove stations from the map
    @Path("/{id}") //Delete station by ID
    public void deleteStation(@PathParam("id") int id){
        boolean removed = stations.removeIf(station -> station.getId() == id);
        if (!removed){
            throw new StationErrorException("Unable to find station ID: " + id);
            //StationErrorException is thrown if the station ID cannot be found
        }
    }

}
