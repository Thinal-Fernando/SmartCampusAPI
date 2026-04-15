/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.resource;

import com.smartcampusapi.model.Sensor;
import com.smartcampusapi.model.SensorReading;
import com.smartcampusapi.store.DataStore;
import com.smartcampusapi.exceptions.DataNotFoundException;
import com.smartcampusapi.exceptions.SensorUnavailableException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

/**
 * Author - Thinal Fernando
 * UOW NO - W2149585
 * IIT NO - 20242112
 */


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return DataStore.readings.getOrDefault(sensorId, new ArrayList<SensorReading>());
    }

    @POST
    public Response addReading(SensorReading reading) {
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading body is required.")
                    .build();
        }

        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            throw new DataNotFoundException("Sensor not found: " + sensorId);
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is under maintenance and cannot accept readings."
            );
        }

        List<SensorReading> readings = DataStore.readings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<SensorReading>();
            DataStore.readings.put(sensorId, readings);
        }

        readings.add(reading);
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}