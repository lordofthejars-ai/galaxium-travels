package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/notifications")
@RolesAllowed("admin")
public class NotificationResource {

    @Channel("notifications-out")
    MutinyEmitter<FlightNotification> emitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> send(FlightNotification message) {
        return emitter.send(message)
                .replaceWith(Response.accepted(message).build());
    }

   @RestClient
   FlightClient flightClient;
   
   @GET
   public List<Flight> findFlights() {
        return flightClient.getFlights();
   }
}
