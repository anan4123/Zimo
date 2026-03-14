/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.resources;

import com.mycompany.zimobackend.model.Charger;
import com.mycompany.zimobackend.model.Station;
import com.mycompany.zimobackend.exception.ChargerNotFoundException;
import com.mycompany.zimobackend.exception.InvalidInputException;
import com.mycompany.zimobackend.exception.StationNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Path("/chargers")
public class ChargerResource {
    private static List<Charger> chargers = new ArrayList<>();
    private static int nextId = 1;

    @GET //Get all chargers
    @Produces(MediaType.APPLICATION_JSON)
    public List<Charger> getAllChargers() {
        return chargers;
    }
        
    @GET //Get charger by ID
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Charger getChargerById(@PathParam("id") int id){
        return chargers.stream()
                .filter(charger -> charger.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ChargerNotFoundException("Charger with ID: " + id + " not found"));
    }
    
  
    
    @POST //Add new charger
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCharger(Charger charger) {
        if (charger.getChargerType() == null || charger.getChargerType().isEmpty()){ // Charger type cannot be empty
            throw new InvalidInputException("Charger type is required");
        }
        
        if (charger.getBatteryLevel() < 0 || charger.getBatteryLevel() > 100){ // Battery level must be between 0 and 100
            throw new InvalidInputException("Battery level must be between 0-100");
        }
        
        if (charger.getStatus() == null || charger.getStatus().isEmpty()){ // Status cannot be left empty
            throw new InvalidInputException("Status cannot be empty");
        }
        
        charger.setId(nextId++);
        charger.setStatus("available");
        chargers.add(charger);
        return Response.status(Response.Status.CREATED).entity(charger).build();
        
    }
    
    @DELETE // Delete charger
    @Path("/{id}")
    public void deleteCharger(@PathParam("id") int id){
        boolean removed = chargers.removeIf(charger -> charger.getId() == id);
        if(!removed){
            throw new ChargerNotFoundException("Unable to find charer ID: " + id);
        }
    }
    
    
    
    
}
    

