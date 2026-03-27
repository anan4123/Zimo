/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.resources;


import com.mycompany.zimobackend.model.Feedback;
import com.mycompany.zimobackend.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

@Path("/feedback")
public class FeedbackResource {
    
    private static List<Feedback> feedbacks = new ArrayList<Feedback>();
   
    @POST
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback (Feedback feedback){
        if (feedback.getTopic() == null || feedback.getTopic().isEmpty()){
            throw new InvalidInputException("Topic is required");
        }
        
        if (feedback.getSuggestion() == null || feedback.getSuggestion().isEmpty()){
            throw new InvalidInputException("Suggestion is required");
        }
        
        feedbacks.add(feedback);
        return Response.status(Response.Status.CREATED).entity(feedback).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Feedback> getAllFeedback(){
        return feedbacks;
    }
}
