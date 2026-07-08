package com.galaxium.booking.client;

import com.galaxium.booking.client.dto.ScanResponseDto;
import com.galaxium.booking.client.dto.SupportTicketDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

/**
 * REST Client for the Incidence Manager service.
 * Connects to the /ticket/scan and /ticket/store endpoints.
 */
@Path("/ticket")
@RegisterRestClient(configKey = "incidence-manager")
public interface IncidenceManagerClient {

    /**
     * Scan a boarding pass PDF (base64-encoded) and extract the booking ID.
     *
     * @param boardingPassBase64 base64-encoded PDF boarding pass
     * @return ScanResponse JSON: { "id": Long, "valid": boolean }
     */
    @POST
    @Path("/scan")
    @Consumes(MediaType.TEXT_PLAIN)
    ScanResponseDto scan(String boardingPassBase64);

    /**
     * Submit a support ticket to trigger the incidence workflow (fire-and-forget).
     * The request body must match TicketRequest: user, email, bookingId, message.
     *
     * Note: the incidence-manager uses "email" (not "userEmail") as the field name.
     *
     * @param ticketRequest map with keys: user, email, bookingId, message
     * @return 202 Accepted with { "instanceId": String }
     */
    @POST
    @Path("/store")
    Response store(SupportTicketDto ticketRequest);
}
