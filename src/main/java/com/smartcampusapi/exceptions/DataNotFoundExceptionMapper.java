/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampusapi.exceptions;

/**
 * Author - Thinal Fernando
 * UOW NO - W2149585
 * IIT NO - 20242112
 */

import com.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;



@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException>  {
    
    @Override
    public Response toResponse(DataNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 404, "/api/v1");
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}