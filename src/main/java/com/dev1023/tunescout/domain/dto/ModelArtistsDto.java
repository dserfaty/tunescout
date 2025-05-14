package com.dev1023.tunescout.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModelArtistsDto(
        @JsonProperty(required = true, value = "artists") ModelArtistDto[] artists) {

    public record ModelArtistDto(
            @JsonProperty(required = true, value = "name") String name,
            @JsonProperty(required = true, value = "description") String description,
            @JsonProperty(required = true, value = "genre") String genre
    ) {}
}