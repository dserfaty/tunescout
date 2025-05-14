package com.dev1023.tunescout.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        // This allows JPA to convert nested objects, in our example it should be able to
        // map authors when returning or creating books
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.registerModule(new RecordModule());
        return modelMapper;
    }
}
