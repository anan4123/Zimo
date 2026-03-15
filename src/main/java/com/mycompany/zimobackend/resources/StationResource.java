/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.resources;

import com.mycompany.zimobackend.exception.StationNotFoundException;
import com.mycompany.zimobackend.exception.InvalidInputException;
import com.mycompany.zimobackend.model.Station;
import com.mycompany.zimobackend.model.Charger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Path("/stations")
public class StationResource {
    private static List<Station> stations = new ArrayList<Station>();
    private static int nextId = 1;
    
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
    public List<Station> getAllStations(){
        return stations;
    }
    
    // GET method to search for a station
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Station getStationById(@PathParam("id") int id){
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst()
                .orElseThrow(() -> new StationNotFoundException("Station with ID " + id + " not found"));  
    }
    
    @GET
    @Path("/{id}/chargers")
    @Produces(MediaType.APPLICATION_JSON)
    public List <Charger> getChargerByStation(@PathParam("id") int id){
        Station station = stations.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new StationNotFoundException("Station with ID " + id + " not found"));
        return ChargerResource.getAllChargers().stream()
                .filter(charger -> charger.getStationId() == id)
                .collect(Collectors.toList());
    }
   
    
    //Creating a new station
    @POST 
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStation(Station station){
        if (station.getName() == null || station.getName().isEmpty()){
            throw new InvalidInputException("Station name cannot be empty");
        }
        if (station.getLat() == 0 || station.getLng() == 0){
            throw new InvalidInputException("Coordinates cannot be zero");
        }
        
        if (station.getAddress() == null || station.getAddress().isEmpty()){
            throw new InvalidInputException("Address cannot be empty");
        }
        
        if (station.getPostcode() == null || station.getPostcode().isEmpty()){
            throw new InvalidInputException("Postcode cannot be empty");
        }
        
        station.setId(nextId++);
        stations.add(station);
        return Response.status(Response.Status.CREATED).entity(station).build();
    
    }
    
    @PUT //Update station
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Station updateStationStatus(@PathParam("id") int id, Station updatedStation){
        Station station_found = stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst()
                .orElseThrow(() -> new StationNotFoundException("Station " + id + " not found"));
        station_found.setStatus(updatedStation.getStatus());
        return station_found;
    }
    
    @DELETE //method is used to delete stations
    @Path("/{id}")
    public void deleteStation(@PathParam("id") int id){
        boolean removed = stations.removeIf(station -> station.getId() == id);
        if (!removed){
            throw new StationNotFoundException("Unable to find station ID: " + id);
        }
    }

}
