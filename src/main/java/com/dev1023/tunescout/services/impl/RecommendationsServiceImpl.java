package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.domain.dto.ModelArtistsDto;
import com.dev1023.tunescout.domain.dto.RecommendationRequestIdDto;
import com.dev1023.tunescout.mappers.Mapper;
import com.dev1023.tunescout.services.PromptService;
import com.dev1023.tunescout.services.RecommendationStorageService;
import com.dev1023.tunescout.services.RecommendationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class RecommendationsServiceImpl implements RecommendationsService {
    private final ChatClient chatClient;
    private final PromptService promptService;
    private final Mapper<ModelArtistsDto.ModelArtistDto, ArtistDto> artistMapper;
    private final RecommendationStorageService recommendationStorageService;

    @Override
    public List<ArtistDto> getRecommendedArtists(String query) {
        log.debug("Processing chat query synchronously: {}", query);
        String response = chatClient
                .prompt(promptService.getPrompt(query))
                .call()
                .content();
        log.debug("Chat query processed successfully, response: {}", response);
        var modelResponse = promptService.getModelArtistResponse(response);
        return Arrays.stream(modelResponse.artists()).map(artistMapper::mapTo).toList();
    }

    @Override
    public RecommendationRequestIdDto initiateRecommendation(String query) {
        log.debug("Initiating asynchronous recommendation for query: {}", query);
        String requestId = recommendationStorageService.createPendingRecommendation(query);

        // Process the recommendation asynchronously
        log.warn("Process recommendation asynchronously for query: {}", query);
        processRecommendationAsync(requestId, query);
        log.warn("done");

        return RecommendationRequestIdDto.builder()
                .requestId(requestId)
                .status(InMemoryRecommendationStorageServiceImpl.STATUS_PROCESSING)
                .build();
    }

    @Async
    protected void processRecommendationAsync(String requestId, String query) {
        log.debug("Processing asynchronous recommendation for requestId: {}", requestId);
        try {
            // Get recommendations from the chat client
            List<ArtistDto> recommendations = getRecommendedArtists(query);

            // Store the recommendations with the existing requestId
            recommendationStorageService.storeRecommendation(requestId, recommendations);

            // Update the status to completed
            recommendationStorageService.updateRecommendationStatus(requestId, InMemoryRecommendationStorageServiceImpl.STATUS_COMPLETED);

            log.debug("Asynchronous recommendation completed for requestId: {}", requestId);
        } catch (Exception e) {
            log.error("Error processing asynchronous recommendation for requestId: {}", requestId, e);
            recommendationStorageService.updateRecommendationStatus(requestId, InMemoryRecommendationStorageServiceImpl.STATUS_FAILED);
        }
    }

    @Override
    public Optional<List<ArtistDto>> getRecommendationById(String requestId) {
        log.debug("Getting recommendation for requestId: {}", requestId);
        return recommendationStorageService.getRecommendation(requestId);
    }

    @Override
    public RecommendationRequestIdDto getRecommendationStatus(String requestId) {
        log.debug("Getting recommendation status for requestId: {}", requestId);
        String status = recommendationStorageService.getRecommendationStatus(requestId);

        if (status == null) {
            return null;
        }

        return RecommendationRequestIdDto.builder()
                .requestId(requestId)
                .status(status)
                .build();
    }
}
