package com.exemple.demo.controller;

import com.exemple.demo.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EventController {

    private final SseService sseService;

    public EventController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // Thymeleaf template "index.html"
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> streamEvents() {
        return sseService.getEvents();
    }
}
