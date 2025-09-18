package com.realestate.controller;

import com.realestate.service.NotificationService;
import com.realestate.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

    private final NotificationService notificationService;

    public WebSocketController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Endpoint pour envoyer un message privé à un utilisateur spécifique
     */
    @MessageMapping("/private-message")
    @SendToUser("/queue/private")
    public WebSocketMessage<String> sendPrivateMessage(
            @Payload WebSocketMessage<String> message,
            Principal principal) {
        
        // Ajouter l'expéditeur au message
        message.setSender(principal.getName());
        
        // Envoyer une notification de réception
        WebSocketMessage<String> response = new WebSocketMessage<>(
            "MESSAGE_RECEIVED",
            principal.getName(),
            message.getRecipient(),
            "Message reçu avec succès",
            null
        );
        
        return response;
    }

    /**
     * Endpoint pour marquer une notification comme lue
     */
    @MessageMapping("/notifications/mark-read")
    @SendToUser("/queue/notifications")
    public WebSocketMessage<String> markNotificationAsRead(
            @Payload String notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Ici, vous pourriez mettre à jour le statut de la notification dans la base de données
        // notificationService.markAsRead(notificationId, userDetails.getUsername());
        
        return new WebSocketMessage<>(
            "NOTIFICATION_READ",
            null,
            userDetails.getUsername(),
            "Notification marquée comme lue",
            notificationId
        );
    }

    /**
     * Endpoint pour s'abonner aux mises à jour en temps réel
     */
    @MessageMapping("/subscribe")
    @SendToUser("/queue/updates")
    public WebSocketMessage<String> subscribeToUpdates(Principal principal) {
        return new WebSocketMessage<>(
            "SUBSCRIPTION_CONFIRMED",
            null,
            principal.getName(),
            "Abonnement aux mises à jour réussi",
            null
        );
    }
}
