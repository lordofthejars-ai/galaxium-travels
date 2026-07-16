package com.galaxium.booking.hivemind;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HiveMindDirectory {

    public static final String CULTURAL_PROTOCOL = "Cultural Protocol";
    HashCommands<String, String, String> hash;

    public HiveMindDirectory(RedisDataSource ds) {
        hash = ds.hash(String.class);
    }

    @Startup
    public void initialize() {
        hash.hset("Venus", CULTURAL_PROTOCOL, "Do not whistle. Whistling or sharp high-pitched noises are interpreted as a declaration of planetary war.");
        hash.hset("Mars", CULTURAL_PROTOCOL, "Leave your magnets behind. Magnetic jewelry or tools will \"scramble\" their local memories, which they consider a class-A felony.");
        hash.hset("Jupiter", CULTURAL_PROTOCOL, "Never speak in the singular. Use plural pronouns (\"We would like to buy a souvenir\") or they will literally ignore your existence.");
        hash.hset("Saturn", CULTURAL_PROTOCOL, "No throwing rocks. Disturbing the ring particulate is viewed as a desecration of their ancestral family trees.");
        hash.hset("Uranus", CULTURAL_PROTOCOL, "Avoid making promises. They store and process contracts using crystal carbon structures; breaking a verbal agreement physically cracks their landscape.");
        hash.hset("Neptune", CULTURAL_PROTOCOL, "Toss a copper coin into the ocean. It acts as a bio-electrical \"tip\" that boosts their processing speed, guaranteeing you great service.");
    }

    public String culturalProtocol(String planet) {
        return hash.hget(planet, CULTURAL_PROTOCOL);
    }

}
