/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.resource;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



/**
 * Author - Thinal Fernando
 * UOW NO - W2149585
 * IIT NO - 20242112
 */

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getApiInfo() {

        Map<String, Object> response = new HashMap<>();

        response.put("version", "1.0");
        response.put("admin contact", "w2149585@westminster.ac.uk");

        Map<String, String> resources = new HashMap<>();
        resources.put("discovery", "/api/v1");
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("sensorReadingsTemplate", "/api/v1/sensors/{sensorId}/readings");


        response.put("resources", resources);

        return response;
    }
}