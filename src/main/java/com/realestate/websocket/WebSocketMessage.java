package com.realestate.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage<T> {
    private String type;
    private String sender;
    private String recipient;
    private T content;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public WebSocketMessage(String type, T content) {
        this.type = type;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
    public WebSocketMessage(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public WebSocketMessage(String type, String recipient, T content) {
        this.type = type;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
