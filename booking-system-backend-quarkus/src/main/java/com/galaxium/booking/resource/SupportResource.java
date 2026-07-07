package com.galaxium.booking.resource;

import com.galaxium.booking.client.dto.ScanBoardingPassDto;
import com.galaxium.booking.client.dto.SupportTicketDto;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/ticket")
public class SupportResource {

    @Path("/scan")
    @POST
    public Response extractInfoFromDocument(String base64PdfImage) {
        ScanBoardingPassDto scanBoardingPassDto = new ScanBoardingPassDto("Alice", "alice@example.com", 1L);
        return Response.ok(scanBoardingPassDto).build();
    }

    @Path("/store")
    @POST
    public Response openSupportTicket(SupportTicketDto supportTicketDto) {
        System.out.println(supportTicketDto);
        return Response.accepted().build();
    }
}
