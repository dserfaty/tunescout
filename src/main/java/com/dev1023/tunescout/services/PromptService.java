package com.dev1023.tunescout.services;

import com.dev1023.tunescout.domain.dto.ModelArtistsDto;
import org.springframework.ai.chat.prompt.Prompt;

public interface PromptService {
    Prompt getPrompt(String query);
    ModelArtistsDto getModelArtistResponse(String response);
}
