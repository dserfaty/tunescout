package com.dev1023.tunescout.services;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.domain.dto.RecommendationRequestIdDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RecommendationsService {
    /**
     * Synchronously gets recommended artists based on a query.
     * This method blocks until the recommendations are available.
     *
     * @param query The query to use for recommendations
     * @return A list of recommended artists
     */
    List<ArtistDto> getRecommendedArtists(String query);

    /**
     * Asynchronously initiates a recommendation request.
     * This method returns immediately with a request ID.
     *
     * @param query The query to use for recommendations
     * @return A DTO containing the request ID and status
     */
    RecommendationRequestIdDto initiateRecommendation(String query);

    /**
     * Gets the recommendations for a given request ID.
     *
     * @param requestId The request ID
     * @return An Optional containing the list of recommended artists, or empty if not found or not ready
     */
    Optional<List<ArtistDto>> getRecommendationById(String requestId);

    /**
     * Gets the status of a recommendation request.
     *
     * @param requestId The request ID
     * @return A DTO containing the request ID and status
     */
    RecommendationRequestIdDto getRecommendationStatus(String requestId);
}
