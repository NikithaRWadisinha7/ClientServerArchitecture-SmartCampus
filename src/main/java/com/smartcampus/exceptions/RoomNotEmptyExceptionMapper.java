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
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        String jsonError = "{"
                + "\"errorCode\": 409,"
                + "\"errorType\": \"Conflict\","
                + "\"errorMessage\": \"" + exception.getMessage() + "\""
                + "}";

        return Response.status(Response.Status.CONFLICT)
                .entity(jsonError)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}