package com.dev1023.tunescout.services.impl;

import com.dev1023.tunescout.domain.dto.ModelArtistsDto;
import com.dev1023.tunescout.services.PromptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromptServiceImpl implements PromptService {
    private static final String PROMPT_TEMPLATE = """
            Recommend artists or bands similar to: %s. In the response, 
            the name field should contain the name of the recommended band;
            the description field should explain the similarity with the requested band;
            the genre field should contain the genre of the recommended band; 
            The result should not contain the requested band.
            Return exactly 5 objects.
            """;

    private final BeanOutputConverter<ModelArtistsDto> converter;

    public PromptServiceImpl() {
        this.converter = new BeanOutputConverter<>(ModelArtistsDto.class);
    }

    @Override
    public Prompt getPrompt(String query) {
        try {
            var options = OllamaOptions.builder()
                    .format(converter.getJsonSchemaMap())
                    .build();
            var content = PROMPT_TEMPLATE.formatted(query);

            return Prompt.builder()
                    .content(content)
                    .chatOptions(options)
                    .build();
        } catch (Exception e) {
            log.error("Error creating prompt options", e);
            return null;
        }
    }

    public ModelArtistsDto getModelArtistResponse(String response) {
        return converter.convert(response);
    }
}
