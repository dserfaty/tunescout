package com.dev1023.tunescout.mappers.impl;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.mappers.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import static com.dev1023.tunescout.domain.dto.ModelArtistsDto.*;

@Component
@AllArgsConstructor
public class ModelArtistMapper implements Mapper<ModelArtistDto, ArtistDto> {
    private final ModelMapper modelMapper;

    @Override
    public ArtistDto mapTo(ModelArtistDto modelArtistDto) {
        return modelMapper.map(modelArtistDto, ArtistDto.class);
    }

    @Override
    public ModelArtistDto mapFrom(ArtistDto artistDto) {
        return modelMapper.map(artistDto, ModelArtistDto.class);
    }
}
