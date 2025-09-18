package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.realestate.entity.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDTO {
    private Long id;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String lastName;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être une adresse email valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;
    
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;
    
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phone;
    
    private String role;
    
    // Méthodes utilitaires
    public static UserProfileDTO fromUser(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        // Ne pas inclure le mot de passe pour des raisons de sécurité
        return dto;
    }
    
    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        // Le mot de passe doit être encodé avant d'être enregistré
        // user.setPassword(this.password); // À encoder avant de sauvegarder
        return user;
    }
}
