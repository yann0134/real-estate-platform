package com.realestate.service;

import com.realestate.dto.AIQueryRequest;
import com.realestate.dto.AIQueryResponse;
import com.realestate.entity.Listing;
import com.realestate.entity.User;
import com.realestate.repository.AIQueryRepository;
import com.realestate.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AIQueryServiceTest {

    @Mock
    private AIQueryRepository aiQueryRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private AIQueryService aiQueryService;
    private User testUser;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        aiQueryService = new AIQueryService(aiQueryRepository, listingRepository, webClientBuilder, new ObjectMapper());
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
    }

    @Test
    void processQuery_ShouldReturnResults_WhenListingsFound() {
        // Arrange
        AIQueryRequest request = new AIQueryRequest("Appartements à Yaoundé moins de 200000");
        
        Listing listing1 = new Listing();
        listing1.setId(1L);
        listing1.setTitle("Appartement moderne");
        listing1.setPrice(new BigDecimal("180000"));
        
        Listing listing2 = new Listing();
        listing2.setId(2L);
        listing2.setTitle("Studio meublé");
        listing2.setPrice(new BigDecimal("150000"));
        
        List<Listing> mockListings = Arrays.asList(listing1, listing2);
        
        when(listingRepository.findByAIQuery(any(), any(), any(), any())).thenReturn(mockListings);
        
        // Act
        AIQueryResponse response = aiQueryService.processQuery(request, testUser);
        
        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalResults());
        assertNotNull(response.getListings());
        assertEquals(2, response.getListings().size());
        assertTrue(response.getResponseTimeMs() > 0);
        
        verify(listingRepository).findByAIQuery(any(), any(), any(), any());
        verify(aiQueryRepository).save(any());
    }

    @Test
    void processQuery_ShouldReturnEmptyResults_WhenNoListingsFound() {
        // Arrange
        AIQueryRequest request = new AIQueryRequest("Villas à Douala moins de 50000");
        
        when(listingRepository.findByAIQuery(any(), any(), any(), any())).thenReturn(List.of());
        
        // Act
        AIQueryResponse response = aiQueryService.processQuery(request, testUser);
        
        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalResults());
        assertNotNull(response.getListings());
        assertTrue(response.getListings().isEmpty());
        assertNotNull(response.getAnswer());
        
        verify(listingRepository).findByAIQuery(any(), any(), any(), any());
        verify(aiQueryRepository).save(any());
    }
}