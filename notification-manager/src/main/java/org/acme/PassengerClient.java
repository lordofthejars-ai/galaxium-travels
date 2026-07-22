package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "passenger-service")
@Path("/users")
public interface PassengerClient {

    @GET
    @Path("/flight/{flightId}")
    List<Passenger> getPassengersByFlight(@PathParam("flightId") long flightId);

}
