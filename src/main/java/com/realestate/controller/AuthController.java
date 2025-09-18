package com.realestate.controller;

import com.realestate.dto.LoginRequest;
import com.realestate.dto.LoginResponse;
import com.realestate.dto.RegisterRequest;
import com.realestate.entity.User;
import com.realestate.service.UserService;
import com.realestate.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints pour l'authentification")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur", description = "Créer un nouveau compte utilisateur")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setRole(request.getRole() != null ? request.getRole() : User.Role.USER);

            User savedUser = userService.createUser(user);
            
            // Génération automatique du token JWT
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            return ResponseEntity.ok(new LoginResponse(token, savedUser));
            
        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Se connecter avec email/mot de passe")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user.getEmail());
            
            return ResponseEntity.ok(new LoginResponse(token, user));
            
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Identifiants invalides");
        }
    }
}