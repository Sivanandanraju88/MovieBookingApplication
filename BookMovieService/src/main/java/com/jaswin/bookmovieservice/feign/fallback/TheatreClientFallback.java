package com.jaswin.bookmovieservice.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.jaswin.bookmovieservice.feign.TheatreClient;
import com.jaswin.bookmovieservice.model.TheatreResponse;

@Component
public class TheatreClientFallback implements TheatreClient {

    @Override
    public List<TheatreResponse> getTheatresByLocation(String location) {
        // Theatre service is DOWN
        return Collections.emptyList();
    }
}
