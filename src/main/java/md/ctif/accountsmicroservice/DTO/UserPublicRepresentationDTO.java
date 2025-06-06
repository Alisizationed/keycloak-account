package md.ctif.accountsmicroservice.DTO;

import lombok.*;
import org.keycloak.representations.idm.AbstractUserRepresentation;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserPublicRepresentationDTO extends AbstractUserRepresentation {
    private String picture;
    private String bio;
    public UserPublicRepresentationDTO(AbstractUserRepresentation user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setEmailVerified(user.isEmailVerified());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        if (user.getAttributes() != null && user.getAttributes().get("picture") != null) {
            this.setPicture(user.getAttributes().get("picture").get(0).toString());
        }
        if (user.getAttributes() != null && user.getAttributes().get("biography") != null) {
            this.setBio(user.getAttributes().get("biography").get(0).toString());
        }
    }
}
