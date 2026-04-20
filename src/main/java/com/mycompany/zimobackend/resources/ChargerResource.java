// Code adapted from Yu, A.A. (2025) 'BookStore Application API'.
// Assignment for 5COSC022W.2 Client Server Architecture, 
// BSc Computer Science, University of Westminster. Unpublished
package com.mycompany.zimobackend.resources;

import com.mycompany.zimobackend.model.Charger;
import com.mycompany.zimobackend.model.Station;
import com.mycompany.zimobackend.exception.ChargerErrorException;
import com.mycompany.zimobackend.exception.InvalidInputException;
import com.mycompany.zimobackend.exception.StationErrorException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

//URL for ChargerResource - Zimo/api/stations
@Path("/chargers")
public class ChargerResource {
    //Arraylist used to store chargers created in the current session
    private static List<Charger> chargers = new ArrayList<>();
    private static int nextId = 1;
    
    //Two sample chargers created for testing
    static {
        Charger Charger1 = new Charger();
        Charger1.setId(nextId++);
        Charger1.setStationId(1);
        Charger1.setChargerType("USB-C");
        Charger1.setStatus("available");
        chargers.add(Charger1);
        
        Charger Charger2 = new Charger();
        Charger2.setId(nextId++);
        Charger2.setStationId(1);
        Charger2.setChargerType("Lightning");
        Charger2.setStatus("available");
        chargers.add(Charger2);
    }

    @GET //Returns all chargers created in the system
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Charger> getAllChargers() {
        return chargers;
    }
        
    @GET //Return a charger by its ID
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Charger getChargerById(@PathParam("id") int id){
        for (Charger c : chargers){
            if (c.getId() == id){
                return c;
            }
        } // Throw ChargerErrorExcepton if the charger is not found
        throw new ChargerErrorException("Charger with ID: " + id + "not found");
    }
    
    @GET //Returns all chargers at a specific station
    @Path("/station/{stationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Charger> getChargersByStation(@PathParam("stationId") int stationId) {
       try {
           return chargers.stream()
                   .filter(c -> c.getStationId() == stationId) //Find stations that match with stationID
                   .collect(Collectors.toList());
         
       } catch (Exception e){ //Output ChargerErrorException if no chargers can be found at the station
           throw new ChargerErrorException("Unable to find chargers at station " + stationId);
           
       }
    }
    
    @POST //Add new charger
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCharger(Charger charger) {
        if (charger.getChargerType() == null || charger.getChargerType().isEmpty()){ // Check if charger type is empty
            //Throw InvalidInputException if charger type is empty
            throw new InvalidInputException("Charger type is required");
        }
       
        int charger_count = 0;
        //Loop to check all chargers in the system
        for (Charger c: chargers){
            // Check if the charger is stored in a station and is the same charger type
            if (c.getStationId() == charger.getStationId() && c.getChargerType().equals(charger.getChargerType())){
                charger_count++;
            }
        }
        // If charger count is more than 2 for each charger, return error
        if (charger_count >= 2){
            throw new InvalidInputException("Maximum of 2 chargers for this type at each station");
        }
        //Assign ID to new charger
        charger.setId(nextId++);
        chargers.add(charger); //Add charger to the list
        return Response.status(Response.Status.CREATED).entity(charger).build();
    }
    
    @POST
    @Path("/{id}/rent") //Rent chargers
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentCharger(@PathParam("id") int id){
        Charger charger = null;
        for (Charger c : chargers){ //Search for charger with matching ID
            if (c.getId() == id){
                charger = c;
                break;
            }
        }
         
        if (charger == null){ //Output error if the charger cannot be found
            throw new ChargerErrorException("Charger not found");
        }
        
        if ("available".equals(charger.getStatus())){ //check if charger status is available
            charger.setStatus("in_use"); //change status to in use when charger is borrowed
            return Response.status(Response.Status.OK).entity(charger).build();
        } 
        else { //return error if charger is already borrowed
            return Response.status(Response.Status.BAD_REQUEST).entity(charger).build();
        }  
    }
    
    @POST
    @Path("/{id}/return") //Return charger
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnCharger(@PathParam("id") int id){
        Charger charger = null;
        for (Charger c : chargers){ //Search for charger with matching ID
            if (c.getId() == id){
                charger = c;
                break;
            }
        }
        
        if (charger == null){ //Output error if charger cannot be found
            throw new ChargerErrorException("Charger not found");
        }
        
        charger.setStatus("available"); //Change status from in use to available
        return Response.status(Response.Status.OK).entity(charger).build();
    }
    
    @DELETE // Delete charger
    @Path("/{id}")
    public void deleteCharger(@PathParam("id") int id){
        boolean removed = chargers.removeIf(charger -> charger.getId() == id);
        if(!removed){ //Output error message if charger cannot be found
            throw new ChargerErrorException("Unable to find charger ID: " + id);
        }
    }
    
    
    
    
}
    

