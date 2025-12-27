package com.jaswin.bookmovieservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jaswin.bookmovieservice.model.ApiResponse;
import com.jaswin.bookmovieservice.model.TheatreMovieResponse;
import com.jaswin.bookmovieservice.service.BookMovieService;

@RestController
@RequestMapping("/api/bookings")
public class BookMovieController {

    @Autowired
    private BookMovieService bookMovieService;

    /**
     * GET BOOKINGS BY LOCATION & DATE
     *
     * Example:
     * GET /api/bookings?location=Bangalore&date=2025-12-25
     *
     * This controller handles 3 scenarios:
     *
     * 1️⃣ Theatre service DOWN
     *    → returns empty list + clear message
     *
     * 2️⃣ Movie service DOWN
     *    → returns theatres with empty movies + clear message
     *
     * 3️⃣ All services UP
     *    → returns full data + success message
     */
    @GetMapping
    public ApiResponse<List<TheatreMovieResponse>> getBookings(
            @RequestParam String location,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        // 🔹 Call service layer
        List<TheatreMovieResponse> data =
                bookMovieService.getBookings(location, date);

        String message;

        // 🔹 CASE 1: Theatre service DOWN
        // Service returns empty list
        if (data.isEmpty()) {
            message = "Theatre service is temporarily unavailable. Showing limited results.";
        }

        // 🔹 CASE 2: Movie service DOWN
        // All theatres exist but movie list is empty
        else if (data.stream().allMatch(r -> r.getMovies().isEmpty())) {
            message = "Movie service is temporarily unavailable. Showing theatres without movies.";
        }

        // 🔹 CASE 3: All services UP
        else {
            message = "Bookings fetched successfully.";
        }

        // 🔹 Wrap response in standard API format
        return new ApiResponse<>(
                "SUCCESS",
                message,
                data
        );
    }
}
