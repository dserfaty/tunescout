package com.dev1023.tunescout.config;

import com.dev1023.tunescout.services.RecommendationStorageService;
import com.dev1023.tunescout.services.impl.InMemoryRecommendationStorageServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that provides an in-memory implementation of the RecommendationStorageService
 * to avoid requiring a Redis server for tests.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates an in-memory implementation of the RecommendationStorageService for testing.
     * The @Primary annotation ensures this implementation is used instead of any other
     * implementations of RecommendationStorageService in the test context.
     * The @ConditionalOnMissingBean annotation ensures this bean is only created if no other
     * bean of type RecommendationStorageService is already defined.
     *
     * @return An in-memory implementation of RecommendationStorageService
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(RecommendationStorageService.class)
    public RecommendationStorageService inMemoryRecommendationStorageService() {
        return new InMemoryRecommendationStorageServiceImpl();
    }
}
