package com.realestate.service;

import com.realestate.entity.Appointment;
import com.realestate.entity.User;
import com.realestate.websocket.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envoie une notification de nouveau rendez-vous au propriétaire du bien
     */
    public void notifyNewAppointment(Appointment appointment) {
        String destination = "/queue/notifications/" + appointment.getProperty().getOwner().getEmail();
        String message = String.format("Nouveau rendez-vous pour le bien: %s", 
            appointment.getProperty().getTitle());
        
        sendNotification(destination, "APPOINTMENT_CREATED", message, appointment);
    }

    /**
     * Notifie l'annulation d'un rendez-vous
     */
    public void notifyAppointmentCancellation(Appointment appointment) {
        // Notifier le propriétaire
        String ownerDestination = "/queue/notifications/" + appointment.getProperty().getOwner().getEmail();
        String ownerMessage = String.format("Rendez-vous annulé pour le bien: %s", 
            appointment.getProperty().getTitle());
        sendNotification(ownerDestination, "APPOINTMENT_CANCELLED", ownerMessage, appointment);

        // Notifier le visiteur
        String visitorDestination = "/queue/notifications/" + appointment.getVisitor().getEmail();
        String visitorMessage = String.format("Votre rendez-vous pour %s a été annulé", 
            appointment.getProperty().getTitle());
        sendNotification(visitorDestination, "APPOINTMENT_CANCELLED", visitorMessage, appointment);
    }

    /**
     * Notifie la confirmation d'un rendez-vous
     */
    public void notifyAppointmentConfirmed(Appointment appointment) {
        String destination = "/queue/notifications/" + appointment.getVisitor().getEmail();
        String message = String.format("Votre rendez-vous pour %s a été confirmé", 
            appointment.getProperty().getTitle());
        
        sendNotification(destination, "APPOINTMENT_CONFIRMED", message, appointment);
    }

    /**
     * Envoie une notification générique à un utilisateur
     */
    public void sendUserNotification(User user, String type, String message, Object payload) {
        String destination = "/queue/notifications/" + user.getEmail();
        sendNotification(destination, type, message, payload);
    }

    /**
     * Envoie une notification à tous les administrateurs
     */
    public void notifyAdmins(String message, Object payload) {
        // Dans une implémentation réelle, on récupérerait tous les administrateurs
        String destination = "/topic/admin/notifications";
        sendNotification(destination, "ADMIN_NOTIFICATION", message, payload);
    }

    /**
     * Méthode utilitaire pour envoyer une notification
     */
    private void sendNotification(String destination, String type, String message, Object payload) {
        WebSocketMessage<Object> notification = new WebSocketMessage<>(
            type,
            null, // sender
            null, // recipient (déjà dans la destination)
            payload,
            message
        );
        
        messagingTemplate.convertAndSend(destination, notification);
    }
}
