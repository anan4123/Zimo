// Code adapted from Yu, A.A. (2025) 'BookStore Application API'.
// Assignment for 5COSC022W.2 Client Server Architecture, 
// BSc Computer Science, University of Westminster. Unpublished

package com.mycompany.zimobackend.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
//ExceptionMapper is called when StationErrorException is thrown
public class ChargerExceptionHandler implements ExceptionMapper<ChargerErrorException>{
    @Override
    public Response toResponse(ChargerErrorException exception){
         //Error response created in JSON format
        String JSON_response = "{\"error\":\"Charger Not Found\",\"message\":\"" + exception.getMessage() + "\"}";
        return Response.status(Response.Status.NOT_FOUND)
                .entity(JSON_response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }    
}
