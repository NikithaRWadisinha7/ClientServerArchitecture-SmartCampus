/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author nikithawadisinha
 */

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiMetadata() {
        String jsonMetadata = "{"
            + "\"api_version\": \"1.0\","
            + "\"description\": \"Smart Campus Sensor API\","
            + "\"endpoints\": {"
            + "    \"rooms\": \"/api/v1/rooms\","
            + "    \"sensors\": \"/api/v1/sensors\""
            + "  }"
            + "}";
            
        return Response.ok(jsonMetadata).build();
    }
}