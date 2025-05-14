package com.dev1023.tunescout.services;

import com.dev1023.tunescout.domain.dto.ArtistDto;

import java.util.List;

public interface RecommendationsService {
    List<ArtistDto> getRecommendedArtists(String query);
}
