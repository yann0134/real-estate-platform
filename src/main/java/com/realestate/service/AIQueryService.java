package com.realestate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.dto.AIQueryRequest;
import com.realestate.dto.AIQueryResponse;
import com.realestate.dto.OpenAIRequest;
import com.realestate.dto.OpenAIResponse;
import com.realestate.entity.AIQuery;
import com.realestate.entity.Listing;
import com.realestate.entity.User;
import com.realestate.repository.AIQueryRepository;
import com.realestate.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class AIQueryService {

    private static final Logger logger = LoggerFactory.getLogger(AIQueryService.class);

    private final AIQueryRepository aiQueryRepository;
    private final ListingRepository listingRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Value("${openai.api-url}")
    private String openaiApiUrl;

    @Value("${openai.model}")
    private String openaiModel;

    public AIQueryService(AIQueryRepository aiQueryRepository,
                         ListingRepository listingRepository,
                         WebClient.Builder webClientBuilder,
                         ObjectMapper objectMapper) {
        this.aiQueryRepository = aiQueryRepository;
        this.listingRepository = listingRepository;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AIQueryResponse processQuery(AIQueryRequest request, User user) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Processing AI query for user {}: {}", user.getId(), request.getQuestion());

            // 1. Analyse de l'intention et extraction des paramètres
            QueryParameters params = extractParameters(request.getQuestion());
            logger.debug("Extracted parameters: {}", params);

            // 2. Exécution de la requête sécurisée sur la base de données
            List<Listing> listings = executeSecureQuery(params);
            logger.debug("Found {} listings matching the query", listings.size());

            // 3. Génération de la réponse IA
            String aiAnswer = generateAIResponse(request.getQuestion(), listings);

            // 4. Calcul du temps de réponse
            long responseTime = System.currentTimeMillis() - startTime;

            // 5. Sauvegarde de la requête
            saveQuery(request.getQuestion(), listings, aiAnswer, user, responseTime);

            return new AIQueryResponse(aiAnswer, listings, listings.size(), responseTime);

        } catch (Exception e) {
            logger.error("Error processing AI query for user {}: {}", user.getId(), e.getMessage(), e);
            long responseTime = System.currentTimeMillis() - startTime;
            
            String errorAnswer = "Je suis désolé, une erreur s'est produite lors du traitement de votre demande. Veuillez réessayer.";
            saveQuery(request.getQuestion(), List.of(), errorAnswer, user, responseTime);
            
            return new AIQueryResponse(errorAnswer, List.of(), 0, responseTime);
        }
    }

    private QueryParameters extractParameters(String question) {
        QueryParameters params = new QueryParameters();
        String normalizedQuestion = question.toLowerCase();

        // Extraction de la ville
        String[] cities = {"yaoundé", "yaounde", "douala", "bamenda", "bafoussam", "garoua", "maroua", "ngaoundéré", "bertoua", "ebolowa"};
        for (String city : cities) {
            if (normalizedQuestion.contains(city)) {
                params.cityName = city;
                break;
            }
        }

        // Extraction du prix maximum
        Pattern pricePattern = Pattern.compile("(moins de|maximum|max|jusqu'à)\\s*(\\d+(?:[\\s.]?\\d+)*)");
        Matcher priceMatcher = pricePattern.matcher(normalizedQuestion);
        if (priceMatcher.find()) {
            String priceStr = priceMatcher.group(2).replaceAll("[\\s.]", "");
            try {
                params.maxPrice = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse price: {}", priceMatcher.group(2));
            }
        }

        // Extraction du type de propriété
        if (normalizedQuestion.contains("appartement")) {
            params.propertyType = Listing.PropertyType.APARTMENT;
        } else if (normalizedQuestion.contains("maison")) {
            params.propertyType = Listing.PropertyType.HOUSE;
        } else if (normalizedQuestion.contains("villa")) {
            params.propertyType = Listing.PropertyType.VILLA;
        } else if (normalizedQuestion.contains("studio")) {
            params.propertyType = Listing.PropertyType.STUDIO;
        }

        // Extraction du nombre de chambres
        Pattern roomPattern = Pattern.compile("(\\d+)\\s*chambre");
        Matcher roomMatcher = roomPattern.matcher(normalizedQuestion);
        if (roomMatcher.find()) {
            try {
                params.minRooms = Integer.parseInt(roomMatcher.group(1));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse room count: {}", roomMatcher.group(1));
            }
        }

        return params;
    }

    private List<Listing> executeSecureQuery(QueryParameters params) {
        // Requête sécurisée utilisant JPA - l'IA n'a jamais accès direct à la BD
        return listingRepository.findByAIQuery(
            params.cityName,
            params.maxPrice != null ? params.maxPrice : new BigDecimal("999999999"),
            params.propertyType,
            params.minRooms
        );
    }

    private String generateAIResponse(String question, List<Listing> listings) {
        try {
            // Construction du prompt pour l'IA
            StringBuilder context = new StringBuilder();
            context.append("Voici les résultats trouvés pour la recherche immobilière:\n\n");
            
            if (listings.isEmpty()) {
                context.append("Aucun bien immobilier ne correspond aux critères spécifiés.\n");
            } else {
                context.append("Nombre de biens trouvés: ").append(listings.size()).append("\n\n");
                for (int i = 0; i < Math.min(listings.size(), 3); i++) {
                    Listing listing = listings.get(i);
                    context.append(String.format("- %s à %s: %s FCFA, %d pièces\n",
                        listing.getTitle(),
                        listing.getCity().getName(),
                        listing.getPrice(),
                        listing.getRooms() != null ? listing.getRooms() : 0
                    ));
                }
                if (listings.size() > 3) {
                    context.append("... et ").append(listings.size() - 3).append(" autres biens.\n");
                }
            }

            // Préparation de la requête OpenAI
            OpenAIRequest.Message systemMessage = new OpenAIRequest.Message("system",
                "Tu es un assistant immobilier expert au Cameroun. Réponds de manière amicale et professionnelle " +
                "en français. Donne des informations claires et utiles sur les biens immobiliers."
            );

            OpenAIRequest.Message userMessage = new OpenAIRequest.Message("user",
                String.format("Question du client: %s\n\n%s\n\nRéponds de manière naturelle et engageante.",
                    question, context.toString())
            );

            OpenAIRequest openAIRequest = new OpenAIRequest(
                openaiModel,
                Arrays.asList(systemMessage, userMessage),
                500,
                0.7
            );

            // Appel à l'API OpenAI
            OpenAIResponse response = webClient.post()
                .uri(openaiApiUrl)
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(openAIRequest)
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .block();

            if (response != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }

        } catch (WebClientResponseException e) {
            logger.error("OpenAI API error: {}", e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Error generating AI response", e);
        }

        // Réponse de fallback
        if (listings.isEmpty()) {
            return "Je n'ai trouvé aucun bien immobilier correspondant à vos critères. " +
                   "Vous pourriez essayer d'élargir votre recherche ou me poser une question différente.";
        } else {
            return String.format("J'ai trouvé %d bien(s) immobilier(s) qui correspondent à votre recherche. " +
                   "Vous pouvez consulter les détails ci-dessous.", listings.size());
        }
    }

    private void saveQuery(String question, List<Listing> listings, String aiAnswer, User user, long responseTime) {
        try {
            AIQuery aiQuery = new AIQuery(question, user);
            aiQuery.setAiAnswer(aiAnswer);
            aiQuery.setResponseTimeMs(responseTime);
            
            // Sauvegarde des résultats au format JSON
            if (!listings.isEmpty()) {
                String rawResults = objectMapper.writeValueAsString(listings);
                aiQuery.setRawResults(rawResults);
            }
            
            aiQueryRepository.save(aiQuery);
            logger.info("Saved AI query for user {}", user.getId());
            
        } catch (JsonProcessingException e) {
            logger.error("Error serializing query results", e);
        } catch (Exception e) {
            logger.error("Error saving AI query", e);
        }
    }

    public Page<AIQuery> getUserQueryHistory(User user, Pageable pageable) {
        return aiQueryRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public Page<AIQuery> getAllQueriesForAdmin(Pageable pageable) {
        return aiQueryRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // Classe interne pour les paramètres extraits
    private static class QueryParameters {
        String cityName;
        BigDecimal maxPrice;
        Listing.PropertyType propertyType;
        Integer minRooms;

        @Override
        public String toString() {
            return String.format("QueryParameters{cityName='%s', maxPrice=%s, propertyType=%s, minRooms=%d}",
                cityName, maxPrice, propertyType, minRooms);
        }
    }
}