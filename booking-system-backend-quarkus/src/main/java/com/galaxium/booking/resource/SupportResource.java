package com.galaxium.booking.resource;

import com.galaxium.booking.client.IncidenceManagerClient;
import com.galaxium.booking.client.dto.ScanBoardingPassDto;
import com.galaxium.booking.client.dto.ScanResponseDto;
import com.galaxium.booking.client.dto.SupportTicketDto;
import com.galaxium.booking.dto.BookingDto;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.entity.Booking;
import com.galaxium.booking.service.BookingService;
import com.galaxium.booking.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/ticket")
public class SupportResource {

    @RestClient
    IncidenceManagerClient incidenceManagerClient;

    @Inject
    BookingService bookingService;

    @Inject
    UserService userService;

    @Path("/scan")
    @POST
    public Response extractInfoFromDocument(String base64PdfImage) {
        ScanResponseDto scanResponseDto = incidenceManagerClient.scan(base64PdfImage);
        BookingDto userBooking = bookingService.findBookingById(scanResponseDto.id());
        Long userId = userBooking.userId;

        UserDto userDto = userService.findUserById(userId);

        ScanBoardingPassDto scanBoardingPassDto = new ScanBoardingPassDto(userDto.name, userDto.email, scanResponseDto.id());
        return Response.ok(scanBoardingPassDto).build();
    }

    @Path("/store")
    @POST
    public Response openSupportTicket(SupportTicketDto supportTicketDto) {
        incidenceManagerClient.store(supportTicketDto);
        return Response.accepted().build();
    }
}
