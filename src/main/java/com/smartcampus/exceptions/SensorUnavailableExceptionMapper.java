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
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        String jsonError = "{"
                + "\"errorCode\": 403,"
                + "\"errorType\": \"Forbidden\","
                + "\"errorMessage\": \"" + exception.getMessage() + "\""
                + "}";

        return Response.status(Response.Status.FORBIDDEN)
                .entity(jsonError)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}