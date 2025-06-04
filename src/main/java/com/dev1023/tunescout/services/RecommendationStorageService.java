package com.dev1023.tunescout.services;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import java.util.List;
import java.util.Optional;

/**
 * Service for storing and retrieving artist recommendations.
 */
public interface RecommendationStorageService {
    /**
     * Stores a list of artist recommendations and returns a unique request ID.
     *
     * @param recommendations The list of artist recommendations to store
     * @return A unique request ID for retrieving the recommendations later
     */
    String storeRecommendation(List<ArtistDto> recommendations);

    /**
     * Stores a list of artist recommendations with an existing request ID.
     *
     * @param requestId The existing request ID
     * @param recommendations The list of artist recommendations to store
     */
    void storeRecommendation(String requestId, List<ArtistDto> recommendations);

    /**
     * Retrieves a list of artist recommendations by request ID.
     *
     * @param requestId The unique request ID
     * @return An Optional containing the list of artist recommendations, or empty if not found
     */
    Optional<List<ArtistDto>> getRecommendation(String requestId);

    /**
     * Stores a recommendation request ID with a "PROCESSING" status.
     *
     * @param query The query used for the recommendation
     * @return A unique request ID
     */
    String createPendingRecommendation(String query);

    /**
     * Updates the status of a recommendation request.
     *
     * @param requestId The unique request ID
     * @param status The new status
     */
    void updateRecommendationStatus(String requestId, String status);

    /**
     * Gets the status of a recommendation request.
     *
     * @param requestId The unique request ID
     * @return The status of the recommendation request, or null if not found
     */
    String getRecommendationStatus(String requestId);
}
