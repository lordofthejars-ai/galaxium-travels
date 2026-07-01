package org.acme;

import io.serverlessworkflow.impl.WorkflowInstance;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.docling.BoardingPassScanner;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

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

    @Inject
    TicketResponseWorkflow ticketResponseWorkflow;

    @POST
    @Path("/storetest")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<TiquetResponse> createSupportTicket(@Valid TiquetRequest tiquetRequest) {

        return ticketResponseWorkflow.startInstance(tiquetRequest.message())
            .onItem()
            .transform(data -> data.as(TiquetResponse.class)
                .orElseThrow());

    }

    @POST
    @Path("/store")
    public Response supportTicketProcessing(@Valid TiquetRequest tiquetRequest) {
        WorkflowInstance instance = ticketResponseWorkflow.instance(tiquetRequest);
        // fire and forget (agents will be called on a thread within the engine)
        instance.start();

        return Response
            .accepted(Map.of("instanceId", instance.id()))
            .build();
    }

    private static final JsonFormat CE_JSON = (JsonFormat) EventFormatProvider.getInstance()
        .resolveFormat(JsonFormat.CONTENT_TYPE);

    @Channel("flow-in-outgoing")
    Emitter<byte[]> flowIn;

    @Inject
    ObjectMapper objectMapper;

    @PUT
    @Path("/update")
    public Response sendReview(TiquetResponse review, @HeaderParam("X-Flow-Instance-Id") String instanceId)
        throws JsonProcessingException {
        byte[] body = objectMapper.writeValueAsBytes(review);

        CloudEvent ce = CloudEventBuilder.v1().withId(UUID.randomUUID().toString())
            .withExtension("flowinstanceid", instanceId)
            .withSource(URI.create("api:/update")).withType("org.acme.newsletter.review.done")
            .withDataContentType("application/json").withData(body).build();

        byte[] ceBytes = CE_JSON.serialize(ce);
        flowIn.send(ceBytes);

        return Response.accepted().build();
    }
}
