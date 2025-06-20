package md.ctif.accountsmicroservice.api;

import lombok.AllArgsConstructor;
import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import md.ctif.accountsmicroservice.service.AdminClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class AdminClientController {

    private final AdminClientService adminClientService;

    @GetMapping
    public Flux<UserListRepresentationDTO> getAllUsers() {
        return adminClientService.getAllKeycloakUsers();
    }

    @GetMapping("/count")
    public Mono<Long> getUserCount() {
        return adminClientService.getKeycloakUserCount();
    }

    @GetMapping("/page")
    public Flux<UserListRepresentationDTO> getAllUsersPageable(
            @RequestParam Integer offset, @RequestParam Integer limit
    ) {
        return adminClientService.getAllKeycloakUsersPageable(offset, limit);
    }

    @GetMapping("/{id}")
    public Mono<UserPublicRepresentationDTO> getUserById(@PathVariable("id") String id) {
        return adminClientService.getKeycloakUser(id);
    }

    @GetMapping("/email/{email}")
    public Mono<String> getKeycloakIdByEmail(@PathVariable("email") String email) {
        return adminClientService.getKeycloakUserId(email);
    }

    @GetMapping("/favourite/count/{id}")
    public Mono<Long> getFavouriteCountByKeycloakId(@PathVariable("id") String id) {
        return adminClientService.getFavouriteCountByKeycloakId(id);
    }

    @GetMapping("/favourite/{id}")
    public Flux<Long> getFavouriteByKeycloakId(@PathVariable("id") String id) {
        return adminClientService.getFavouriteByKeycloakId(id);
    }

    @GetMapping("/favourite/v2/{id}")
    public Flux<Long> getFavouriteByKeycloakIdPageable(
            @PathVariable("id") String id,
            @RequestParam Integer offset,
            @RequestParam Integer limit
    ) {
        return adminClientService.getFavouriteByKeycloakIdPageable(id, offset, limit);
    }

    @GetMapping("/favourite/{id}/{favourite}")
    public Mono<Boolean> isFavourite(
            @PathVariable("id") String id,
            @PathVariable("favourite") Long favourite
    ) {
        return adminClientService.isFavourite(id, favourite);
    }

    @PostMapping("/favourite/{id}/{favourite}")
    public Mono<Void> setIsFavourite(
            @PathVariable("id") String id,
            @PathVariable("favourite") Long favourite,
            @RequestParam Boolean favouriteStatus
    ) {
        return adminClientService.setFavouriteStatus(id, favourite, favouriteStatus);
    }

    @PostMapping("/picture/{id}")
    public Mono<Void> setPicture(
            @PathVariable String id,
            @RequestBody String picture
    ) {
        return adminClientService.setPicture(id,picture);
    }
}
