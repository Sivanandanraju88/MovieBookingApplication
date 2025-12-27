package com.jaswin.bookmovieservice.feign.fallback;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jaswin.bookmovieservice.feign.MovieClient;
import com.jaswin.bookmovieservice.model.MovieBookingDTO;

@Component
public class MovieClientFallback implements MovieClient {

    @Override
    public List<MovieBookingDTO> getMovies(List<Long> theatreIds, LocalDate date) {
        // Movie service is DOWN
        return Collections.emptyList();
    }
}
