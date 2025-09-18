package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.realestate.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long visitorId;
    private String visitorName;
    private String visitorEmail;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    
    private String message;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
