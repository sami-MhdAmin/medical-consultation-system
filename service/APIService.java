package com.grad.akemha.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class APIService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Initialize objectMapper through constructor
    public APIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String addDocument(String text, Long id) {
        String url = "https://ir-api-1.onrender.com/addDocument";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("id", id);
        String response = restTemplate.postForObject(url, requestBody, String.class);
        return response;
    }

//    public List<Integer> searchDocument(String text, Integer id) throws IOException {
//        String url = UriComponentsBuilder.fromHttpUrl("https://ir-api-1.onrender.com/search")
//                .queryParam("page", id)
//                .toUriString();
//
//        Map<String, Object> requestBody = Map.of("searchText", text);
//        String response = restTemplate.postForObject(url, requestBody, String.class);
//        return objectMapper.readValue(response, new TypeReference<List<Integer>>() {
//        });
//    }

    public List<Integer> searchDocument(String text, Integer page) {
        String url = UriComponentsBuilder.fromHttpUrl("https://ir-api-1.onrender.com/search")
                .queryParam("page", page)
                .toUriString();

        Map<String, Object> requestBody = Map.of("searchText", text);
        try {
            String response = restTemplate.postForObject(url, requestBody, String.class);
            if (response == null) {
                throw new IOException("Received null response from the search service");
            }
            return objectMapper.readValue(response, new TypeReference<List<Integer>>() {});
        } catch (IOException e) {
            // Handle or log the exception as needed
            e.printStackTrace(); // Or use a logging framework
            throw new RuntimeException("Failed to search document", e); // Or another custom exception
        }
    }

    public void deleteDocument(Long id) throws IOException {
        String url = "https://ir-api-1.onrender.com/delDocument/" + id;
        restTemplate.delete(url);
        System.out.println("Document deleted successfully");
    }


}
