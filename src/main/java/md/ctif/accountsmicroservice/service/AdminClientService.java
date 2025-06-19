package md.ctif.accountsmicroservice.service;

import lombok.extern.log4j.Log4j2;
import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
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
            UserRepresentation user = keycloak.realm("recipe-app").users().get(id).toRepresentation();
            Map<String, List<String>> attrs = user.getAttributes();
            boolean contains = attrs != null
                    && attrs.get("favourite") != null
                    && attrs.get("favourite").contains(favourite.toString());
            log.info("Checked favourite status for user {}, favourite {}: {}", id, favourite, contains);
            return Mono.just(contains);
        } catch (Exception e) {
            log.error("Error checking favourite status for user {} favourite {}", id, favourite, e);
            return Mono.just(false);
        }
    }

    public Mono<Void> setFavouriteStatus(String id, Long favourite, Boolean favouriteStatus) {
        log.info("Setting favourite status for user {}, favourite {}, status {}", id, favourite, favouriteStatus);
        return Mono.fromRunnable(() -> {
            try {
                UserRepresentation user = keycloak.realm("recipe-app").users().get(id).toRepresentation();
                var attrs = Optional.ofNullable(user.getAttributes())
                        .orElseGet(() -> {
                            Map<String, List<String>> newAttrs = new HashMap<>();
                            user.setAttributes(newAttrs);
                            return newAttrs;
                        });

                List<String> favourites = attrs.computeIfAbsent("favourite", k -> new ArrayList<>());

                if (Boolean.TRUE.equals(favouriteStatus)) {
                    if (!favourites.contains(favourite.toString())) {
                        favourites.add(favourite.toString());
                        log.info("Added favourite {} for user {}", favourite, id);
                    } else {
                        log.info("Favourite {} already present for user {}", favourite, id);
                    }
                } else {
                    if (favourites.remove(favourite.toString())) {
                        log.info("Removed favourite {} for user {}", favourite, id);
                    } else {
                        log.info("Favourite {} was not present for user {}", favourite, id);
                    }
                }

                keycloak.realm("recipe-app").users().get(id).update(user);
            } catch (Exception e) {
                log.error("Error setting favourite status for user {} favourite {} status {}", id, favourite, favouriteStatus, e);
                throw new RuntimeException(e);
            }
        });
    }

    public Mono<Void> setPicture(String id, String picture) {
        log.info("Setting picture for user {} to {}", id, picture);
        return Mono.fromRunnable(() -> {
            try {
                UserRepresentation user = keycloak.realm("recipe-app").users().get(id).toRepresentation();
                Map<String, List<String>> attrs = Optional.ofNullable(user.getAttributes())
                        .orElseGet(() -> {
                            Map<String, List<String>> newAttrs = new HashMap<>();
                            user.setAttributes(newAttrs);
                            return newAttrs;
                        });

                List<String> pictures = attrs.get("picture");
                if (pictures == null) {
                    pictures = new ArrayList<>();
                    attrs.put("picture", pictures);
                } else {
                    pictures.clear();
                }

                pictures.add(picture);
                log.info("Picture set successfully for user {}", id);

                keycloak.realm("recipe-app").users().get(id).update(user);
            } catch (Exception e) {
                log.error("Error setting picture for user {}", id, e);
                throw new RuntimeException(e);
            }
        });
    }
}
