package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class NotificationRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("kafka:{{notification.kafka.topic}}")
                .routeId("kafka-to-telegram")
                .unmarshal().json(JsonLibrary.Jackson, FlightNotification.class)
                .setProperty("notification", body())
                .bean(PassengerService.class)
                .split(body())
                .parallelProcessing()
                .setHeader("CamelTelegramChatId", body())
                .setBody(exchangeProperty("notification.message"))
                .to("telegram:bots?authorizationToken={{notification.telegram.authorizationtoken}}")
                .end();
    }
}
