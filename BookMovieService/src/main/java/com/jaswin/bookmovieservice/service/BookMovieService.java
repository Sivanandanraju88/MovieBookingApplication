package com.jaswin.bookmovieservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jaswin.bookmovieservice.feign.MovieClient;
import com.jaswin.bookmovieservice.feign.TheatreClient;
import com.jaswin.bookmovieservice.model.MovieBookingDTO;
import com.jaswin.bookmovieservice.model.TheatreMovieResponse;
import com.jaswin.bookmovieservice.model.TheatreResponse;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class BookMovieService {

    @Autowired
    private TheatreClient theatreClient;

    @Autowired
    private MovieClient movieClient;

    /**
     * MAIN BUSINESS METHOD
     * Theatre failure → CircuitBreaker fallback
     * Movie failure → Partial response (empty movies)
     */
    @CircuitBreaker(
            name = "bookMovieService",
            fallbackMethod = "theatreServiceFallback"
    )
    public List<TheatreMovieResponse> getBookings(
            String location, LocalDate date) {

        // 1️⃣ THEATRE SERVICE (critical dependency)
        List<TheatreResponse> theatres =
                theatreClient.getTheatresByLocation(location);

        if (theatres.isEmpty()) {
            return new ArrayList<>();
        }

        // 2️⃣ Extract theatre IDs
        List<Long> theatreIds = new ArrayList<>();
        for (TheatreResponse theatre : theatres) {
            theatreIds.add(theatre.getId());
        }

        // 3️⃣ MOVIE SERVICE (non-critical dependency)
        List<MovieBookingDTO> movies;
        try {
            movies = movieClient.getMovies(theatreIds, date);
        } catch (FeignException ex) {
            // 🔥 Movie service DOWN → return theatres with empty movies
            return buildResponseWithoutMovies(theatres);
        }

        // 4️⃣ NORMAL SUCCESS RESPONSE
        return buildFullResponse(theatres, movies);
    }

    /**
     * FALLBACK → Theatre service DOWN
     */
    public List<TheatreMovieResponse> theatreServiceFallback(
            String location,
            LocalDate date,
            Throwable ex) {

        System.out.println("⚠ Theatre service DOWN. Returning empty response.");
        return new ArrayList<>();
    }

    /**
     * Helper: Theatre UP, Movie DOWN
     */
    private List<TheatreMovieResponse> buildResponseWithoutMovies(
            List<TheatreResponse> theatres) {

        System.out.println("⚠ Movie service DOWN. Returning theatres without movies.");

        List<TheatreMovieResponse> responseList = new ArrayList<>();

        for (TheatreResponse theatre : theatres) {
            TheatreMovieResponse response = new TheatreMovieResponse();
            response.setTheatre(theatre);
            response.setMovies(new ArrayList<>());
            responseList.add(response);
        }

        return responseList;
    }

    /**
     * Helper: Theatre UP, Movie UP
     */
    private List<TheatreMovieResponse> buildFullResponse(
            List<TheatreResponse> theatres,
            List<MovieBookingDTO> movies) {

        List<TheatreMovieResponse> responseList = new ArrayList<>();

        for (TheatreResponse theatre : theatres) {
            List<MovieBookingDTO> theatreMovies = new ArrayList<>();

            for (MovieBookingDTO movie : movies) {
                if (movie.getTheaterid().equals(theatre.getId())) {
                    theatreMovies.add(movie);
                }
            }

            TheatreMovieResponse response = new TheatreMovieResponse();
            response.setTheatre(theatre);
            response.setMovies(theatreMovies);
            responseList.add(response);
        }

        return responseList;
    }
}
