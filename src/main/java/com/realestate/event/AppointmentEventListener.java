package com.realestate.event;

import com.realestate.entity.Appointment;
import com.realestate.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {

    private final NotificationService notificationService;

    public AppointmentEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleAppointmentCreatedEvent(AppointmentCreatedEvent event) {
        Appointment appointment = event.getAppointment();
        notificationService.notifyNewAppointment(appointment);
    }

    @EventListener
    public void handleAppointmentUpdatedEvent(AppointmentUpdatedEvent event) {
        Appointment appointment = event.getAppointment();
        
        switch (appointment.getStatus()) {
            case CONFIRMED:
                notificationService.notifyAppointmentConfirmed(appointment);
                break;
            case CANCELLED:
                notificationService.notifyAppointmentCancellation(appointment);
                break;
            default:
                // Ne rien faire pour les autres statuts
                break;
        }
    }
}
