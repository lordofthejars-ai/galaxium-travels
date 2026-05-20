package com.galaxium.booking.resource;

import com.galaxium.booking.dto.ErrorResponse;
import com.galaxium.booking.dto.Result;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.dto.UserRegistration;
import com.galaxium.booking.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for user operations.
 */
@Path("/")
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid UserRegistration request) {
        Result<UserDto> result = userService.registerUser(request.name(), request.email());
        
        return switch (result) {
            case Result.Success<UserDto> success -> Response.ok(success.value()).build();
            case Result.Failure<UserDto> failure -> toErrorResponse(failure.error());
        };
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(
            @QueryParam("name") String name,
            @QueryParam("email") String email) {
        
        if (name == null || email == null) {
            ErrorResponse error = new ErrorResponse(
                "MISSING_PARAMETERS",
                "Both name and email are required",
                null
            );
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        
        Result<UserDto> result = userService.getUser(name, email);
        
        return switch (result) {
            case Result.Success<UserDto> success -> Response.ok(success.value()).build();
            case Result.Failure<UserDto> failure -> toErrorResponse(failure.error());
        };
    }

    private Response toErrorResponse(ErrorResponse error) {
        Response.Status status = switch (error.errorCode) {
            case "USER_NOT_FOUND" -> Response.Status.NOT_FOUND;
            case "EMAIL_ALREADY_EXISTS", "EMAIL_EXISTS" -> Response.Status.CONFLICT;
            case "INVALID_EMAIL" -> Response.Status.BAD_REQUEST;
            default -> Response.Status.BAD_REQUEST;
        };
        return Response.status(status).entity(error).build();
    }
}

// Made with Bob
