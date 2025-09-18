package com.realestate.dto;

import com.realestate.entity.Listing;

import java.util.List;

public class AIQueryResponse {
    
    private String answer;
    private List<Listing> listings;
    private int totalResults;
    private long responseTimeMs;

    // Constructors
    public AIQueryResponse() {}

    public AIQueryResponse(String answer, List<Listing> listings, int totalResults, long responseTimeMs) {
        this.answer = answer;
        this.listings = listings;
        this.totalResults = totalResults;
        this.responseTimeMs = responseTimeMs;
    }

    // Getters and Setters
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<Listing> getListings() { return listings; }
    public void setListings(List<Listing> listings) { this.listings = listings; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
}