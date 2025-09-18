package com.realestate.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage<T> {
    private String type;
    private String sender;
    private String recipient;
    private T content;
    private String message;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public WebSocketMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public WebSocketMessage(String type, T content) {
        this();
        this.type = type;
        this.content = content;
    }
    
    public WebSocketMessage(String type, String message) {
        this();
        this.type = type;
        this.message = message;
    }
    
    public WebSocketMessage(String type, String recipient, T content) {
        this();
        this.type = type;
        this.recipient = recipient;
        this.content = content;
    }

    public WebSocketMessage(String type, String sender, String recipient, T content, String message) {
        this();
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.message = message;
    }
}
