package com.jaswin.bookmovieservice.feign;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jaswin.bookmovieservice.model.MovieBookingDTO;

@FeignClient(
        name = "movie-service",
        url = "http://localhost:8080",
        fallback = com.jaswin.bookmovieservice.feign.fallback.MovieClientFallback.class
)
public interface MovieClient {

    @GetMapping("/api/movies/by-theatres-date")
    List<MovieBookingDTO> getMovies(
            @RequestParam List<Long> theatreIds,
            @RequestParam LocalDate date);
}