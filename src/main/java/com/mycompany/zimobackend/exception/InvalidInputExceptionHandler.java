// Code adapted from Yu, A.A. (2025) 'BookStore Application API'.
// Assignment for 5COSC022W.2 Client Server Architecture, 
// BSc Computer Science, University of Westminster. Unpublished
package com.mycompany.zimobackend.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider

//ExceptionMapper is called when the InvalidInputException is thrown
public class InvalidInputExceptionHandler implements ExceptionMapper<InvalidInputException> {
     @Override
    public Response toResponse(InvalidInputException exception) {
        //Error message in JSON format
        String JSON_response = "{\"error\":\"Invalid Input\",\"message\":\"" + exception.getMessage() + "\"}";
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(JSON_response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
