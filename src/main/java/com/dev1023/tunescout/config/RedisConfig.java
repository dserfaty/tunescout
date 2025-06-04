package com.dev1023.tunescout.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

@Configuration
public class RedisConfig {

    @Value("${tunescout.cache.ttl-minutes:60}")
    private long cacheTtlMinutes;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use Jackson2JsonRedisSerializer for values
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        // Configure hash operations
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, List<ArtistDto>> artistListRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, List<ArtistDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());

        // Create a copy of the ObjectMapper with the specific type information
        ObjectMapper artistListMapper = objectMapper.copy();

        // Use Jackson2JsonRedisSerializer for values with specific type information
        Jackson2JsonRedisSerializer<List<ArtistDto>> serializer = new Jackson2JsonRedisSerializer<>(
            artistListMapper,
            artistListMapper.getTypeFactory().constructCollectionType(List.class, ArtistDto.class)
        );

        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    public long getCacheTtlMinutes() {
        return cacheTtlMinutes;
    }
}
