package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.config.TestConfig;
import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.services.RecommendationStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestConfig.class)
class InMemoryRecommendationStorageServiceImplTest {

    @Autowired
    private InMemoryRecommendationStorageServiceImpl storageService;

    private List<ArtistDto> testArtists;

    @BeforeEach
    void setUp() {
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

        System.out.println("[DEBUG_LOG] Successfully retrieved and verified artists from in-memory storage: " + retrievedArtists.get());
    }

    @Test
    void testCreatePendingRecommendation() {
        // Create a pending recommendation
        String query = "test query";
        String requestId = storageService.createPendingRecommendation(query);

        // Verify status
        assertEquals(InMemoryRecommendationStorageServiceImpl.STATUS_PROCESSING, storageService.getRecommendationStatus(requestId));

        // Store recommendation
        storageService.storeRecommendation(requestId, testArtists);

        // Verify status updated
        assertEquals(InMemoryRecommendationStorageServiceImpl.STATUS_COMPLETED, storageService.getRecommendationStatus(requestId));

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
        storageService.updateRecommendationStatus(requestId, InMemoryRecommendationStorageServiceImpl.STATUS_PROCESSING);
        assertEquals(InMemoryRecommendationStorageServiceImpl.STATUS_PROCESSING, storageService.getRecommendationStatus(requestId));

        // Update status to COMPLETED
        storageService.updateRecommendationStatus(requestId, InMemoryRecommendationStorageServiceImpl.STATUS_COMPLETED);
        assertEquals(InMemoryRecommendationStorageServiceImpl.STATUS_COMPLETED, storageService.getRecommendationStatus(requestId));

        // Update status to FAILED
        storageService.updateRecommendationStatus(requestId, InMemoryRecommendationStorageServiceImpl.STATUS_FAILED);
        assertEquals(InMemoryRecommendationStorageServiceImpl.STATUS_FAILED, storageService.getRecommendationStatus(requestId));

        System.out.println("[DEBUG_LOG] Successfully tested status updates for request ID: " + requestId);
    }
}
