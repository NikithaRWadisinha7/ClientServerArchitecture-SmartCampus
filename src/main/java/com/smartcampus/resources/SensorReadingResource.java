/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;
import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList; // <--- We import standard ArrayList
import java.util.UUID;

/**
 *
 * @author nikithawadisinha
 */

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String parentSensorId;
    
    public SensorReadingResource(String parentSensorId) {
        this.parentSensorId = parentSensorId;
    }
    
    @GET
    public Response getReadingHistory() {
        // FIXED: We wrap the raw data in a standard ArrayList. 
        // This stops the JSON converter from panicking and fixes the 500 crash!
        return Response.ok(new ArrayList<>(MockDatabase.getReadings().values())).build(); 
    }

    @POST
    public Response addReading(SensorReading newReading) {
        Sensor sensor = MockDatabase.getSensors().get(parentSensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Cannot add reading. Sensor " + parentSensorId + " is currently in maintenance.");
        }

        newReading.setId(UUID.randomUUID().toString());
        MockDatabase.getReadings().put(newReading.getId(), newReading);
        sensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(newReading)
                .build();
    }
}