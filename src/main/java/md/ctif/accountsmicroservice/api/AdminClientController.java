package md.ctif.accountsmicroservice.api;

import lombok.AllArgsConstructor;
import md.ctif.accountsmicroservice.DTO.UserListRepresentationDTO;
import md.ctif.accountsmicroservice.DTO.UserPublicRepresentationDTO;
import md.ctif.accountsmicroservice.service.AdminClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{id}")
    public Mono<UserPublicRepresentationDTO> getUserById(@PathVariable("id") String id) {
        return adminClientService.getKeycloakUser(id);
    }

    @GetMapping("/email/{email}")
    public Mono<String> getKeycloakIdByEmail(@PathVariable("email") String email) {
        return adminClientService.getKeycloakUserId(email);
    }
}
