/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.resource;


import com.smartcampusapi.model.Room;
import com.smartcampusapi.store.DataStore;
import com.smartcampusapi.exceptions.DataNotFoundException;
import com.smartcampusapi.exceptions.RoomNotEmptyException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;


/**
 * Author - Thinal Fernando
 * UOW NO - W2149585
 * IIT NO - 20242112
 */

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }
    
 
    
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {

        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room id is required.")
                    .build();
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room name is required.")
                    .build();
        }

        if (room.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room capacity must be greater than 0.")
                    .build();
        }

        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with id '" + room.getId() + "' already exists.")
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        DataStore.rooms.put(room.getId(), room);

        URI uri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();

        return Response.created(uri)
                .entity(room)
                .build();
    }
    
    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId){
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new DataNotFoundException("Room not found: " + roomId);        }
        return room;
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new DataNotFoundException("Room not found: " + roomId);
        }
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException( "Cannot delete room '" + roomId + "': it still has " +
                    room.getSensorIds().size() + " sensor(s) assigned.");
        }
        DataStore.rooms.remove(roomId);
        return Response.ok().entity("Room deleted successfully.").build();
    }
    
    
    
    
}

