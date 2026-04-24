/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.exceptions;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 *
 * @author nikithawadisinha
 */

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        System.err.println("CRITICAL SYSTEM ERROR: " + exception.getMessage());
        exception.printStackTrace();
        
        String jsonError = "{"
                + "\"errorCode\": 500,"
                + "\"errorType\": \"Internal Server Error\","
                + "\"errorMessage\": \"An unexpected system error occurred. Our team has been notified.\""
                + "}";

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(jsonError)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}