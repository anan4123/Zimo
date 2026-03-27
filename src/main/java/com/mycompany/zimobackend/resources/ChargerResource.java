/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

@Path("/chargers")
public class ChargerResource {
    private static List<Charger> chargers = new ArrayList<>();
    private static int nextId = 1;
    
        
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

    @GET //Get all chargers
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Charger> getAllChargers() {
        return chargers;
    }
        
    @GET //Get charger by ID
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Charger getChargerById(@PathParam("id") int id){
        for (Charger c : chargers){
            if (c.getId() == id){
                return c;
            }
        }
        throw new ChargerErrorException("Charger with ID: " + id + "not found");
    }
    
    @GET
    @Path("/station/{stationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Charger> getChargersByStation(@PathParam("stationId") int stationId) {
       try {
           return chargers.stream()
                   .filter(c -> c.getStationId() == stationId)
                   .collect(Collectors.toList());
         
       } catch (Exception e){
           throw new ChargerErrorException("Unable to find chargers at station " + stationId);
           
       }
    }
    
    @POST //Add new charger
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCharger(Charger charger) {
        if (charger.getChargerType() == null || charger.getChargerType().isEmpty()){ // Charger type cannot be empty
            throw new InvalidInputException("Charger type is required");
        }
        
        int charger_count = 0;
        for (Charger c: chargers){
            if (c.getStationId() == charger.getStationId() && c.getChargerType().equals(charger.getChargerType())){
                charger_count++;
            }
        }
        
        if (charger_count >= 2){
            throw new InvalidInputException("Maximum of 2 chargers for this type at each station");
        }
        
        charger.setId(nextId++);
        chargers.add(charger);
        return Response.status(Response.Status.CREATED).entity(charger).build();
    }
    
    @POST
    @Path("/{id}/rent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentCharger(@PathParam("id") int id){
        Charger charger = null;
        for (Charger c : chargers){
            if (c.getId() == id){
                charger = c;
                break;
            }
        }
        
        if (charger == null){
            throw new ChargerErrorException("Charger not found");
        }
        
        if ("available".equals(charger.getStatus())){
            charger.setStatus("in_use");
            return Response.status(Response.Status.OK).entity(charger).build();
        } 
        else {
            return Response.status(Response.Status.BAD_REQUEST).entity(charger).build();
        }  
    }
    
    @POST
    @Path("/{id}/return")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnCharger(@PathParam("id") int id){
        Charger charger = null;
        for (Charger c : chargers){
            if (c.getId() == id){
                charger = c;
                break;
            }
        }
        
        if (charger == null){
            throw new ChargerErrorException("Charger not found");
        }
        
        charger.setStatus("available");
        return Response.status(Response.Status.OK).entity(charger).build();
    }
    
    @DELETE // Delete charger
    @Path("/{id}")
    public void deleteCharger(@PathParam("id") int id){
        boolean removed = chargers.removeIf(charger -> charger.getId() == id);
        if(!removed){
            throw new ChargerErrorException("Unable to find charger ID: " + id);
        }
    }
    
    
    
    
}
    

