package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "flight-service")
@Path("/flights")
public interface FlightClient {

    @GET
    List<Flight> getFlights();

}
