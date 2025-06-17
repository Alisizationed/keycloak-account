package md.ctif.accountsmicroservice.service;

import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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

    public Flux<Long> getFavouriteByKeycloakId(String id) {
        try {
            List<Long> list = keycloak.realm("recipe-app").users()
                    .get(id)
                    .toRepresentation()
                    .getAttributes()
                    .get("favourite")
                    .stream().map(Long::valueOf).collect(Collectors.toList());
            return Flux.fromIterable(list);
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    public Mono<Long> getFavouriteCountByKeycloakId(String id) {
        try {
            Long size = (long) keycloak.realm("recipe-app").users()
                    .get(id)
                    .toRepresentation()
                    .getAttributes()
                    .get("favourite")
                    .size();
            return Mono.just(size);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    public Flux<Long> getFavouriteByKeycloakIdPageable(String id, Integer offset, Integer limit) {
        try {
            List<Long> list = keycloak.realm("recipe-app").users()
                    .get(id)
                    .toRepresentation()
                    .getAttributes()
                    .get("favourite")
                    .stream().map(Long::valueOf).toList();
            offset = Math.min(offset, list.toArray().length);
            limit = Math.min(offset + limit, list.toArray().length);
            return Flux.fromIterable(list.subList(offset, limit));
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    public Mono<Boolean> isFavourite(String id, Long favourite) {
        try {
            return Mono.just(keycloak.realm("recipe-app").users()
                    .get(id)
                    .toRepresentation()
                    .getAttributes()
                    .get("favourite")
                    .contains(favourite.toString()));
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    public Mono<Void> setFavouriteStatus(String id, Long favourite, Boolean favouriteStatus) {
        System.out.println(favouriteStatus);
        if (favouriteStatus == true) {
            return Mono.fromRunnable(() -> {
                UserRepresentation user = keycloak.realm("recipe-app")
                        .users()
                        .get(id)
                        .toRepresentation();
                if (user.getAttributes().putIfAbsent("favourite",List.of(favourite.toString())) != null) {
                    user.getAttributes().get("favourite").add(favourite.toString());
                }
                keycloak.realm("recipe-app").users().get(id).update(user);
            });
        } else {
            return Mono.fromRunnable(() -> {
                UserRepresentation user = keycloak.realm("recipe-app")
                        .users()
                        .get(id)
                        .toRepresentation();
                if (user.getAttributes().get("favourite") != null) {
                    user.getAttributes().get("favourite").remove(favourite.toString());
                }
                keycloak.realm("recipe-app").users().get(id).update(user);
            });
        }
    }

    public Mono<Void> setPicture(String id, String picture) {
        return Mono.fromRunnable(() -> {
            UserRepresentation user = keycloak.realm("recipe-app")
                    .users()
                    .get(id)
                    .toRepresentation();
            user.getAttributes().get("picture").clear();
            user.getAttributes().get("picture").add(picture);
            keycloak.realm("recipe-app").users().get(id).update(user);
        });
    }
}
