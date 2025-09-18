package com.realestate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_queries")
public class AIQuery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Question is required")
    private String question;

    @Column(columnDefinition = "JSONB")
    private String rawResults;

    @Column(columnDefinition = "TEXT")
    private String aiAnswer;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public AIQuery() {}

    public AIQuery(String question, User user) {
        this.question = question;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getRawResults() { return rawResults; }
    public void setRawResults(String rawResults) { this.rawResults = rawResults; }

    public String getAiAnswer() { return aiAnswer; }
    public void setAiAnswer(String aiAnswer) { this.aiAnswer = aiAnswer; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}