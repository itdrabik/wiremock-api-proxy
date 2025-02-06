package com.example.wiremockapiproxy;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NBP_API_URL = "https://api.nbp.pl/api/exchangerates/rates/A/";

    @GetMapping(value = "/currency", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCurrencyRate(@RequestParam String code) {
        String url = NBP_API_URL + code + "?format=json";
        try {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(restTemplate.getForObject(url, String.class));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Currency not found\"}");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Internal Server Error\"}");
        }
    }
}
