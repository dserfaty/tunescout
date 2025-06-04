package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.config.RedisConfig;
import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.services.RecommendationStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Primary
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisRecommendationStorageServiceImpl implements RecommendationStorageService {

    private final RedisTemplate<String, List<ArtistDto>> artistListRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConfig redisConfig;

    // Redis key prefixes
    private static final String RECOMMENDATIONS_KEY_PREFIX = "recommendations:";
    private static final String STATUS_KEY_PREFIX = "status:";
    private static final String QUERY_TO_REQUEST_ID_KEY_PREFIX = "query:";

    // Status constants
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    @Autowired
    public RedisRecommendationStorageServiceImpl(
            RedisTemplate<String, List<ArtistDto>> artistListRedisTemplate,
            RedisTemplate<String, Object> redisTemplate,
            RedisConfig redisConfig) {
        this.artistListRedisTemplate = artistListRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.redisConfig = redisConfig;
        
        // Test Redis connection
        try {
            this.redisTemplate.getConnectionFactory().getConnection().ping();
        } catch (RedisConnectionFailureException e) {
            throw new IllegalStateException("Redis server is not available. Cannot initialize RedisRecommendationStorageService.", e);
        }
    }

    @Override
    public String storeRecommendation(List<ArtistDto> recommendations) {
        String requestId = UUID.randomUUID().toString();
        storeRecommendation(requestId, recommendations);
        return requestId;
    }

    @Override
    public void storeRecommendation(String requestId, List<ArtistDto> recommendations) {
        String recommendationsKey = RECOMMENDATIONS_KEY_PREFIX + requestId;
        artistListRedisTemplate.opsForValue().set(recommendationsKey, recommendations);
        artistListRedisTemplate.expire(recommendationsKey, Duration.ofMinutes(redisConfig.getCacheTtlMinutes()));
        
        updateRecommendationStatus(requestId, STATUS_COMPLETED);
    }

    @Override
    public Optional<List<ArtistDto>> getRecommendation(String requestId) {
        String recommendationsKey = RECOMMENDATIONS_KEY_PREFIX + requestId;
        List<ArtistDto> recommendations = artistListRedisTemplate.opsForValue().get(recommendationsKey);
        return Optional.ofNullable(recommendations);
    }

    @Override
    public String createPendingRecommendation(String query) {
        // Normalize the query (trim and lowercase)
        String normalizedQuery = query.trim().toLowerCase();
        String queryKey = QUERY_TO_REQUEST_ID_KEY_PREFIX + normalizedQuery;

        // Check if this query has already been processed
        String existingRequestId = (String) redisTemplate.opsForValue().get(queryKey);
        if (existingRequestId != null) {
            // If the request exists and is completed, return the existing ID
            String status = getRecommendationStatus(existingRequestId);
            if (STATUS_COMPLETED.equals(status)) {
                return existingRequestId;
            }
        }

        // Create a new request ID
        String requestId = UUID.randomUUID().toString();
        updateRecommendationStatus(requestId, STATUS_PROCESSING);

        // Store the normalized query to request ID mapping with TTL
        redisTemplate.opsForValue().set(queryKey, requestId);
        redisTemplate.expire(queryKey, Duration.ofMinutes(redisConfig.getCacheTtlMinutes()));

        return requestId;
    }

    @Override
    public void updateRecommendationStatus(String requestId, String status) {
        String statusKey = STATUS_KEY_PREFIX + requestId;
        redisTemplate.opsForValue().set(statusKey, status);
        redisTemplate.expire(statusKey, Duration.ofMinutes(redisConfig.getCacheTtlMinutes()));
    }

    @Override
    public String getRecommendationStatus(String requestId) {
        String statusKey = STATUS_KEY_PREFIX + requestId;
        return (String) redisTemplate.opsForValue().get(statusKey);
    }
}