package com.dev1023.tunescout.controllers;

import com.dev1023.tunescout.domain.dto.APIResponse;
import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.services.RecommendationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/recommendations")
@AllArgsConstructor
@Slf4j
public class RecommendationsController {
    private RecommendationsService recommendationsService;

    @GetMapping(path = "/artists")
    public ResponseEntity<APIResponse<List<ArtistDto>>> getArtistsRecommendations(@RequestParam String query) {
        var response = recommendationsService.getRecommendedArtists(query);
        return new ResponseEntity<>(new APIResponse<>(response), HttpStatus.OK);
    }
}


