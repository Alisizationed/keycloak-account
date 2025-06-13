package md.ctif.accountsmicroservice.api;

import lombok.AllArgsConstructor;
import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import md.ctif.accountsmicroservice.service.AdminClientService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

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

    @GetMapping("/favourite/{username}")
    public Flux<String> getFavouriteByUsername(@PathVariable("username") String username) {
        return adminClientService.getFavouriteByUsername(username);
    }
}
