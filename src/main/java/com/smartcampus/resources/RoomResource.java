/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;
import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author nikithawadisinha
 */

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Collection<Room> getAllRooms() {
        return MockDatabase.getRooms().values();
    }

    @POST
    public Response createRoom(Room newRoom, @Context UriInfo uriInfo) {
        if (newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            newRoom.setId(UUID.randomUUID().toString());
        }
        MockDatabase.getRooms().put(newRoom.getId(), newRoom);
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newRoom.getId()).build();
        
        return Response.status(Response.Status.CREATED)
                .location(locationURI)
                .entity(newRoom)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = MockDatabase.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = MockDatabase.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found\"}")
                    .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete Room " + roomId + ". It still contains active sensors.");
        }
        MockDatabase.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}