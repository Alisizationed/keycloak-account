package md.ctif.accountsmycroservice.api;

import md.ctif.accountsmycroservice.service.AdminClientService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/users")
public class AdminClientController {
    @Autowired
    private AdminClientService adminClientService;
    @GetMapping
    public Flux<UserRepresentation> getAllUsers() {
        return adminClientService.getAllKeycloakUsers();
    }
}
