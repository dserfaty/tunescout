package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.config.RedisConfig;
import com.dev1023.tunescout.domain.dto.ArtistDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Import(RedisConfig.class)
class RedisRecommendationStorageServiceImplTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("tunescout.cache.ttl-minutes", () -> "1"); // 1 minute TTL for tests
    }

    @Autowired
    private RedisRecommendationStorageServiceImpl storageService;

    @Autowired
    private RedisTemplate<String, List<ArtistDto>> artistListRedisTemplate;

    private List<ArtistDto> testArtists;

    @BeforeEach
    void setUp() {
        // Clear Redis database before each test
        artistListRedisTemplate.getConnectionFactory().getConnection().flushAll();

        // Create test data
        testArtists = Arrays.asList(
                ArtistDto.builder().name("Artist 1").genre("Rock").description("Rock artist").build(),
                ArtistDto.builder().name("Artist 2").genre("Pop").description("Pop artist").build(),
                ArtistDto.builder().name("Artist 3").genre("Jazz").description("Jazz artist").build()
        );
    }

    @Test
    void testStoreAndRetrieveRecommendation() {
        // Store recommendation
        String requestId = storageService.storeRecommendation(testArtists);

        // Retrieve recommendation
        Optional<List<ArtistDto>> retrievedArtists = storageService.getRecommendation(requestId);

        // Verify
        assertTrue(retrievedArtists.isPresent());
        assertEquals(3, retrievedArtists.get().size());

        // Verify deserialization of each artist
        ArtistDto artist1 = retrievedArtists.get().get(0);
        assertEquals("Artist 1", artist1.getName());
        assertEquals("Rock", artist1.getGenre());
        assertEquals("Rock artist", artist1.getDescription());

        ArtistDto artist2 = retrievedArtists.get().get(1);
        assertEquals("Artist 2", artist2.getName());
        assertEquals("Pop", artist2.getGenre());
        assertEquals("Pop artist", artist2.getDescription());

        ArtistDto artist3 = retrievedArtists.get().get(2);
        assertEquals("Artist 3", artist3.getName());
        assertEquals("Jazz", artist3.getGenre());
        assertEquals("Jazz artist", artist3.getDescription());

        System.out.println("[DEBUG_LOG] Successfully retrieved and verified artists from Redis: " + retrievedArtists.get());
    }

    @Test
    void testCreatePendingRecommendation() {
        // Create a pending recommendation
        String query = "test query";
        String requestId = storageService.createPendingRecommendation(query);

        // Verify status
        assertEquals(RedisRecommendationStorageServiceImpl.STATUS_PROCESSING, storageService.getRecommendationStatus(requestId));

        // Store recommendation
        storageService.storeRecommendation(requestId, testArtists);

        // Verify status updated
        assertEquals(RedisRecommendationStorageServiceImpl.STATUS_COMPLETED, storageService.getRecommendationStatus(requestId));

        // Create another recommendation with the same query
        String newRequestId = storageService.createPendingRecommendation(query);

        // Should return the existing request ID
        assertEquals(requestId, newRequestId);

        System.out.println("[DEBUG_LOG] Successfully tested query caching with request ID: " + requestId);
    }

    @Test
    void testUpdateRecommendationStatus() {
        // Create a request ID
        String requestId = "test-request-id";

        // Update status to PROCESSING
        storageService.updateRecommendationStatus(requestId, RedisRecommendationStorageServiceImpl.STATUS_PROCESSING);
        assertEquals(RedisRecommendationStorageServiceImpl.STATUS_PROCESSING, storageService.getRecommendationStatus(requestId));

        // Update status to COMPLETED
        storageService.updateRecommendationStatus(requestId, RedisRecommendationStorageServiceImpl.STATUS_COMPLETED);
        assertEquals(RedisRecommendationStorageServiceImpl.STATUS_COMPLETED, storageService.getRecommendationStatus(requestId));

        // Update status to FAILED
        storageService.updateRecommendationStatus(requestId, RedisRecommendationStorageServiceImpl.STATUS_FAILED);
        assertEquals(RedisRecommendationStorageServiceImpl.STATUS_FAILED, storageService.getRecommendationStatus(requestId));

        System.out.println("[DEBUG_LOG] Successfully tested status updates for request ID: " + requestId);
    }
}
