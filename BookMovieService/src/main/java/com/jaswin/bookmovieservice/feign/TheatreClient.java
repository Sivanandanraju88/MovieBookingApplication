package com.jaswin.bookmovieservice.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jaswin.bookmovieservice.model.TheatreResponse;

@FeignClient(
        name = "theatre-service",
        url = "http://localhost:8081",
        fallback = com.jaswin.bookmovieservice.feign.fallback.TheatreClientFallback.class
)
public interface TheatreClient {

    @GetMapping("/api/theatres/location/{location}")
    List<TheatreResponse> getTheatresByLocation(
            @PathVariable String location);
}
