package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.services.RecommendationStorageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRecommendationStorageServiceImpl implements RecommendationStorageService {

    // Map to store recommendations by request ID
    private final Map<String, List<ArtistDto>> recommendationsMap = new ConcurrentHashMap<>();

    // Map to store recommendation status by request ID
    private final Map<String, String> statusMap = new ConcurrentHashMap<>();

    // Map to store query to request ID mapping (for caching)
    private final Map<String, String> queryToRequestIdMap = new ConcurrentHashMap<>();

    // Status constants
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    @Override
    public String storeRecommendation(List<ArtistDto> recommendations) {
        String requestId = UUID.randomUUID().toString();
        recommendationsMap.put(requestId, recommendations);
        statusMap.put(requestId, STATUS_COMPLETED);
        return requestId;
    }

    @Override
    public void storeRecommendation(String requestId, List<ArtistDto> recommendations) {
        recommendationsMap.put(requestId, recommendations);
        statusMap.put(requestId, STATUS_COMPLETED);
    }

    @Override
    public Optional<List<ArtistDto>> getRecommendation(String requestId) {
        return Optional.ofNullable(recommendationsMap.get(requestId));
    }

    @Override
    public String createPendingRecommendation(String query) {
        // Normalize the query (trim and lowercase)
        String normalizedQuery = query.trim().toLowerCase();

        // Check if this query has already been processed
        String existingRequestId = queryToRequestIdMap.get(normalizedQuery);
        if (existingRequestId != null) {
            // If the request exists and is completed, return the existing ID
            String status = statusMap.get(existingRequestId);
            if (STATUS_COMPLETED.equals(status) || STATUS_PROCESSING.equals(status)) {
                return existingRequestId;
            }
        }

        // Create a new request ID
        String requestId = UUID.randomUUID().toString();
        statusMap.put(requestId, STATUS_PROCESSING);

        // Store the normalized query to request ID mapping
        queryToRequestIdMap.put(normalizedQuery, requestId);

        return requestId;
    }

    @Override
    public void updateRecommendationStatus(String requestId, String status) {
        statusMap.put(requestId, status);
    }

    @Override
    public String getRecommendationStatus(String requestId) {
        return statusMap.get(requestId);
    }
}
