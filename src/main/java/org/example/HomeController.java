package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homepage(@RequestParam(value = "city_id", required = false, defaultValue = "1") String city_id,
                           Model model) {

        Map<String, Object> weatherData = WeatherController.retrieveAllAttributes(Integer.parseInt(city_id));
        model.addAllAttributes(weatherData);

        return "homepage";
    }

    @PostMapping("/generate_suggestions")
    public ResponseEntity<Map<Integer, String>> generateSuggestions(@RequestBody Map<String, String> payload) {
        // Convert all to lower case and rid of trailing and leading white spaces
        Map<Integer, String> suggestions = new HashMap<>();
        try {
            String user_input = WeatherController.reformatInput(payload.get("user_input"));
            // Convert offset into an integer
            int offset = Integer.parseInt(payload.get("offset"));
            // Find all similar cities
            suggestions = WeatherController.generateSimilarCities(AppConstants.LIMIT, offset, user_input);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok().body(suggestions);

    }


}