package org.acme;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
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

    @POST
    @Path("/store")
    public Response createSupportTicket(@Valid TiquetRequest tiquetRequest) {

        return Response.noContent().build();

    }
}
