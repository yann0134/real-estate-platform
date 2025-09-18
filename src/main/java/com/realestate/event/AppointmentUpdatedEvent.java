package com.realestate.event;

import com.realestate.entity.Appointment;
import org.springframework.context.ApplicationEvent;

public class AppointmentUpdatedEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentUpdatedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
