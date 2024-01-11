package com.ai.demo.controller;

import com.ai.demo.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/joke")
    public String getJoke(@RequestParam (name = "question") final String question) {
        return aiService.getJoke(question);
    }
    @GetMapping("/book")
    public String getBook(@RequestParam(name = "question") final String question) {
        return aiService.getBestBook(question);
    }
    @GetMapping(value = "/image", produces = "image/jpeg")
    public ResponseEntity<InputStreamResource> getImage(@RequestParam(name = "question") final String question) throws URISyntaxException {
        return ResponseEntity.ok().body(aiService.getImage(question));
    }
}