package com.exemple.demo.service;

import com.exemple.demo.model.EventMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class SseService {

    private final Sinks.Many<String> sink;
    private final ObjectMapper mapper;

    public SseService() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.mapper = new ObjectMapper();
    }

    public void sendEvent(EventMessage event) {
        try {
            String json = mapper.writeValueAsString(event);
            sink.tryEmitNext(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Flux<String> getEvents() {
        return sink.asFlux();
    }
}