package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;


import java.util.stream.Stream;

@ApplicationScoped
public class MyMessagingApplication {

    /**
     * Injects an emitter to send messages to the "words-out" channel.
     */
    /**@Channel("words-out")
    Emitter<String> emitter;


    void onStart(@Observes StartupEvent ev) {
        Stream.of("Hello", "with", "Quarkus", "Messaging", "message").forEach(string -> emitter.send(string));
    }


    @Incoming("words-in")
    @Outgoing("uppercase")
    public String toUpperCase(String message) {
        return message.toUpperCase();
    }

    @Incoming("uppercase")
    public void sink(String word) {
        System.out.println(">> " + word);
    }
    **/
}
