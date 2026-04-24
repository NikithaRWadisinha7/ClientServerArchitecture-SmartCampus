/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;
import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author nikithawadisinha
 */

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Collection<Sensor> getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = MockDatabase.getSensors().values();
        
        if (type != null && !type.trim().isEmpty()) {
            return allSensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        
        return allSensors;
    }

    @POST
    public Response createSensor(Sensor newSensor) {

        Room targetRoom = MockDatabase.getRooms().get(newSensor.getRoomId());
        
        if (targetRoom == null) {
            throw new LinkedResourceNotFoundException("Cannot create sensor. Room ID " + newSensor.getRoomId() + " does not exist.");
        }

        if (newSensor.getId() == null) {
            newSensor.setId(UUID.randomUUID().toString());
        }
        if (newSensor.getStatus() == null) {
            newSensor.setStatus("ACTIVE");
        }

        MockDatabase.getSensors().put(newSensor.getId(), newSensor);
        targetRoom.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(newSensor)
                .build();
    }
    
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
