package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.domain.dto.ModelArtistsDto;
import com.dev1023.tunescout.mappers.Mapper;
import com.dev1023.tunescout.services.PromptService;
import com.dev1023.tunescout.services.RecommendationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RecommendationsServiceImpl implements RecommendationsService {
    private final ChatClient chatClient;
    private final PromptService promptService;
    private final Mapper<ModelArtistsDto.ModelArtistDto, ArtistDto> artistMapper;

    @Override
    public List<ArtistDto> getRecommendedArtists(String query) {
        log.debug("Processing chat query: {}", query);
        String response = chatClient
                .prompt(promptService.getPrompt(query))
                .call()
                .content();
        log.debug("Chat query processed successfully, response: {}", response);
        var modelResponse = promptService.getModelArtistResponse(response);
        return Arrays.stream(modelResponse.artists()).map(artistMapper::mapTo).toList();
    }
}
