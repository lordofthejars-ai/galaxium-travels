package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class NotificationRouter extends RouteBuilder {

    @Inject
    PassengerService passengerService;

    @Override
    public void configure() {
        from("kafka:{{notification.kafka.topic}}")
                .routeId("kafka-to-telegram")
                .unmarshal().json(JsonLibrary.Jackson, FlightNotification.class)
                .log(body().toString())
                .setProperty("notification", body())
                .bean(passengerService)
                .split(body())
                .setHeader("CamelTelegramChatId", body())
                .setBody(simple("${exchangeProperty.notification.message}"))
                .log(body().toString())
                .to("telegram:bots?authorizationToken={{notification.telegram.authorizationtoken}}")
                .end();
    }
}
