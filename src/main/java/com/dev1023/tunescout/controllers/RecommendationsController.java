package com.dev1023.tunescout.controllers;

import com.dev1023.tunescout.domain.dto.APIResponse;
import com.dev1023.tunescout.domain.dto.ArtistDto;
import com.dev1023.tunescout.domain.dto.RecommendationRequestIdDto;
import com.dev1023.tunescout.services.RecommendationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/recommendations")
@AllArgsConstructor
@Slf4j
public class RecommendationsController {
    private RecommendationsService recommendationsService;

    /**
     * Synchronously gets artist recommendations.
     * This endpoint blocks until recommendations are available.
     *
     * @param query The query to use for recommendations
     * @return A response containing the list of recommended artists
     */
    @GetMapping(path = "/artists")
    public ResponseEntity<APIResponse<List<ArtistDto>>> getArtistsRecommendations(@RequestParam String query) {
        log.info("Received synchronous recommendation request with query: {}", query);
        var response = recommendationsService.getRecommendedArtists(query);
        return new ResponseEntity<>(new APIResponse<>(response), HttpStatus.OK);
    }

    /**
     * Asynchronously initiates an artist recommendation request.
     * This endpoint returns immediately with a request ID.
     *
     * @param query The query to use for recommendations
     * @return A response containing the request ID and status
     */
    @PostMapping(path = "/artists/async")
    public ResponseEntity<APIResponse<RecommendationRequestIdDto>> initiateArtistsRecommendation(@RequestParam String query) {
        log.info("Received asynchronous recommendation request with query: {}", query);
        var response = recommendationsService.initiateRecommendation(query);
        log.info("Initiated recommendation request response: {}", response);
        return new ResponseEntity<>(new APIResponse<>(response), HttpStatus.ACCEPTED);
    }

    /**
     * Gets the status of a recommendation request.
     *
     * @param requestId The request ID
     * @return A response containing the request ID and status
     */
    @GetMapping(path = "/artists/status/{requestId}")
    public ResponseEntity<APIResponse<RecommendationRequestIdDto>> getRecommendationStatus(@PathVariable String requestId) {
        log.info("Checking status for recommendation request: {}", requestId);
        var response = recommendationsService.getRecommendationStatus(requestId);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new APIResponse<>(response), HttpStatus.OK);
    }

    /**
     * Gets the recommendations for a given request ID.
     *
     * @param requestId The request ID
     * @return A response containing the list of recommended artists, or 404 if not found or not ready
     */
    @GetMapping(path = "/artists/{requestId}")
    public ResponseEntity<APIResponse<List<ArtistDto>>> getRecommendationById(@PathVariable String requestId) {
        log.info("Getting recommendations for request: {}", requestId);
        var recommendationsOpt = recommendationsService.getRecommendationById(requestId);

        if (recommendationsOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new APIResponse<>(recommendationsOpt.get()), HttpStatus.OK);
    }
}
