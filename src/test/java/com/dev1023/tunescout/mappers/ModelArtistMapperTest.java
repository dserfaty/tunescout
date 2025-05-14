package com.dev1023.tunescout.mappers;

import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.mappers.impl.ModelArtistMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.dev1023.tunescout.domain.dto.ModelArtistsDto.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ModelArtistMapperTest {
    @Autowired
    private ModelArtistMapper mapper;

    @Test
    public void testThatModelArtistMapperWorksProperly() {
        var name = "an artist";
        var description = "a description";
        var genre = "a genre";

        ModelArtistDto modelArtistDto = new ModelArtistDto(name, description, genre);
        assertEquals(new ArtistDto(name, description, genre), mapper.mapTo(modelArtistDto));
    }
}
