package org.acme;

import io.quarkus.oidc.OidcConfigurationMetadata;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

/**
 * Exposes the Keycloak token endpoint URL to the frontend so it never needs
 * a hardcoded port (DevServices assigns a random port at runtime).
 */
@Path("/q/auth-config")
@PermitAll
public class AuthConfigResource {

    @Inject
    OidcConfigurationMetadata oidcMetadata;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> config() {
        return Map.of(
            "tokenEndpoint", oidcMetadata.getTokenUri().toString(),
            "clientId", "notification-manager"
        );
    }
}
