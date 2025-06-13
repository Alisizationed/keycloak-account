package md.ctif.accountsmicroservice.service;

import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class AdminClientService {
    @Autowired
    private Keycloak keycloak;

    public Flux<UserListRepresentationDTO> getAllKeycloakUsers() {
        return Flux.fromIterable(keycloak.realm("recipe-app")
                        .users()
                        .list())
                .map(UserListRepresentationDTO::new);
    }

    public Flux<UserListRepresentationDTO> getAllKeycloakUsersPageable(Integer offset, Integer limit) {
        return Flux.fromIterable(keycloak.realm("recipe-app")
                        .users()
                        .list(offset, limit))
                .map(UserListRepresentationDTO::new);
    }

    public Mono<UserPublicRepresentationDTO> getKeycloakUserByEmail(String email) {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .searchByEmail(email, true)
                        .getFirst()
        ).map(UserPublicRepresentationDTO::new);
    }

    public Mono<UserPublicRepresentationDTO> getKeycloakUser(String id) {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .get(id).toRepresentation()
        ).map(UserPublicRepresentationDTO::new);
    }

    public Mono<String> getKeycloakUserId(String email) {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .searchByEmail(email, true)
                        .getFirst()
        ).map(AbstractUserRepresentation::getId);
    }

    public Mono<Long> getKeycloakUserCount() {
        return Mono.just(
                keycloak.realm("recipe-app")
                        .users()
                        .count()
        ).map(Long::valueOf);
    }

    public Flux<String> getFavouriteByUsername(String username) {
        try {
            List<String> list = keycloak.realm("recipe-app").users()
                    .search(username, true)
                    .getFirst()
                    .getAttributes()
                    .get("favourite");
            return list != null ? Flux.fromIterable(list) : Flux.empty();
        } catch (Exception e) {
            return Flux.empty();
        }
    }
}
