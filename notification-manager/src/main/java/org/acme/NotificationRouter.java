package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class NotificationRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("kafka:{{notification.kafka.topic}}")
            .routeId("kafka-to-telegram")
            .log("Received message from Kafka topic '{{notification.kafka.topic}}': ${body}")
            .to("telegram:bots?authorizationToken={{notification.telegram.authorizationtoken}}&chatId={{notification.telegram.chatid}}");
    }
}
