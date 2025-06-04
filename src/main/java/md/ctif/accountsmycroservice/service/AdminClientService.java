package md.ctif.accountsmycroservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdminClientService {
    @Autowired
    private Keycloak keycloak;

    public Flux<UserRepresentation> getAllKeycloakUsers() {
        return Flux.fromIterable(keycloak.realm("recipe-app")
                .users()
                .list());
    }

    public Mono<UserRepresentation> getKeycloakUserByEmail(String email) {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .searchByEmail(email,true)
                        .getFirst()
        );
    }

    public Mono<UserRepresentation> getKeycloakUser(String id) {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .get(id).toRepresentation()
        );
    }
}
