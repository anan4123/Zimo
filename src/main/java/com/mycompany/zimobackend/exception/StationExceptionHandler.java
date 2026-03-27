/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zimobackend.exception;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


public class StationExceptionHandler implements ExceptionMapper<StationErrorException>{
    @Override
    public Response toResponse(StationErrorException exception){
        String response = "{\"error\":\"Station Not Found\",\"message\":\"" + exception.getMessage() + "\"}";
        return Response.status(Response.Status.NOT_FOUND)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
}
