// Code adapted from Yu, A.A. (2025) 'BookStore Application API'.
// Assignment for 5COSC022W.2 Client Server Architecture, 
// BSc Computer Science, University of Westminster. Unpublished

package com.mycompany.zimobackend.resources;

import com.mycompany.zimobackend.model.Feedback;
import com.mycompany.zimobackend.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;

// Define the URL for the feedback resource - /Zimo/api/feedback
@Path("/feedback")
public class FeedbackResource {
    // In-memory storage used to store feedbacks submitted for current sessions
    private static List<Feedback> feedbacks = new ArrayList<Feedback>();
   
    @POST //POST method to submit new feedback
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeedback (Feedback feedback){
        if (feedback.getTopic() == null || feedback.getTopic().isEmpty()){
            throw new InvalidInputException("Topic is required");
            // InvalidInputException is thrown if the topic is empty
        }
        
        if (feedback.getSuggestion() == null || feedback.getSuggestion().isEmpty()){
            throw new InvalidInputException("Suggestion is required");
            // InvalidInputException is throwin if the suggesstion is empty
        }
        
        feedbacks.add(feedback); //Add feedback to the in-memory list
        return Response.status(Response.Status.CREATED).entity(feedback).build();
        //Return response when the feedback is added
    }
    
    @GET // Retrieve all feedbacks stored in in-memory list
    @Produces(MediaType.APPLICATION_JSON)
    public List<Feedback> getAllFeedback(){
        return feedbacks;
    }
}
