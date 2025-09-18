package com.realestate.controller;

import com.realestate.dto.AIQueryRequest;
import com.realestate.dto.AIQueryResponse;
import com.realestate.entity.AIQuery;
import com.realestate.entity.User;
import com.realestate.service.AIQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI Assistant", description = "Endpoints pour l'assistant IA immobilier")
@SecurityRequirement(name = "bearerAuth")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    private final AIQueryService aiQueryService;

    public AIController(AIQueryService aiQueryService) {
        this.aiQueryService = aiQueryService;
    }

    @PostMapping("/query")
    @Operation(summary = "Poser une question à l'assistant IA",
              description = "Envoyez une question en langage naturel sur l'immobilier")
    public ResponseEntity<AIQueryResponse> askQuestion(
            @Valid @RequestBody AIQueryRequest request,
            @AuthenticationPrincipal User user) {
        
        logger.info("AI query received from user {}: {}", user.getId(), request.getQuestion());
        
        try {
            AIQueryResponse response = aiQueryService.processQuery(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing AI query for user {}: {}", user.getId(), e.getMessage());
            
            AIQueryResponse errorResponse = new AIQueryResponse(
                "Je suis désolé, une erreur s'est produite. Veuillez réessayer.",
                null,
                0,
                0
            );
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Historique des conversations IA",
              description = "Récupère l'historique des conversations de l'utilisateur connecté")
    public ResponseEntity<Page<AIQuery>> getQueryHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AIQuery> queries = aiQueryService.getUserQueryHistory(user, pageable);
        
        return ResponseEntity.ok(queries);
    }

    @GetMapping("/admin/queries")
    @Operation(summary = "Dashboard admin - Toutes les requêtes IA",
              description = "Récupère toutes les requêtes IA pour supervision (ADMIN seulement)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AIQuery>> getAllQueries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AIQuery> queries = aiQueryService.getAllQueriesForAdmin(pageable);
        
        return ResponseEntity.ok(queries);
    }
}