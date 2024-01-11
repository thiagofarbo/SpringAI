package com.ai.demo.service;

import com.ai.demo.domain.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class AIService {

    @Autowired
    private final AiClient aiClient;

    @Value("${spring.ai.openai.apikey}")
    private String apiKey;

    @Value("${spring.ai.openai.imageUrl}")
    private String openAIImageUrl;

    public String getJoke(final String question){
        PromptTemplate promptTemplate = new PromptTemplate(question);
        promptTemplate.add("question", question);
        return this.aiClient.generate(promptTemplate.create()).getGeneration().getText();
    }
    public String getBestBook(final String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        AiResponse generate = this.aiClient.generate(promptTemplate.create());
        return generate.getGeneration().getText();
    }
    public InputStreamResource getImage(final String question) throws URISyntaxException {
        PromptTemplate promptTemplate = new PromptTemplate(question.concat(", ")
                .concat(
                "Make a resolution of 512x512, but ensure that it is presented in json it need to be string.\n" +
                "I desire only one creation. Give me as JSON format: prompt, n, size."));
        promptTemplate.add("topic", question);
        String imagePrompt = this.aiClient.generate(promptTemplate.create()).getGeneration().getText();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(imagePrompt,headers);

        String imageUrl = restTemplate.exchange(openAIImageUrl, HttpMethod.POST, httpEntity, ImageResponse.class)
                .getBody().getData().get(0).getUrl();
        byte[] imageBytes = restTemplate.getForObject(new URI(imageUrl), byte[].class);
        assert imageBytes != null;
        return new InputStreamResource(new java.io.ByteArrayInputStream(imageBytes));
    }
}