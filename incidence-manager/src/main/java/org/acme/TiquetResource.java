package org.acme;

import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.ai.TicketResponseExperts;
import org.acme.docling.BoardingPassScanner;

@Path("/tiquet")
public class TiquetResource {

    @Inject
    BoardingPassScanner boardingPassScanner;

    public record ScanResponse(Long id, boolean valid){}

    @POST
    @Path("/scan")
    public ScanResponse scan(String boardingPassBase64) {

        long id = boardingPassScanner.scanBookingId(boardingPassBase64);
        return new ScanResponse(id,  id > 0 ?  true : false);
    }

    public record TiquetRequest(
        @NotBlank
        String user,

        @Email
        String email,

        @Min(1)
        Long bookingId,

        @NotEmpty
        String message)
    {}

    @Inject
    TicketResponseExperts.ExpertTicketResponseAgent expertTicketResponseAgent;

    @POST
    @Path("/store")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createSupportTicket(@Valid TiquetRequest tiquetRequest) {
        ResultWithAgenticScope<String> response = expertTicketResponseAgent.ask(tiquetRequest.message());

        System.out.println(response.agenticScope().readState("sentiment"));

        return Response.ok(response.result()).build();

    }
}
