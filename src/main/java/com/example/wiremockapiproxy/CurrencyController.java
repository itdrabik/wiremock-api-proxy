package com.example.wiremockapiproxy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NBP_API_URL = "https://api.nbp.pl/api/exchangerates/rates/A/";

    @GetMapping("/currency")
    public String getCurrencyRate(@RequestParam String code) {
        String url = NBP_API_URL + code + "?format=json";
        return restTemplate.getForObject(url, String.class);
    }
}
