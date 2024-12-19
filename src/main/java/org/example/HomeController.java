package org.example;

import com.fasterxml.jackson.core.type.TypeReference;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("message", "Welcome to my Java website!");
        // Hardcode a city to get information from
        // CHANGING THIS LATER
        WeatherHandler weather = new WeatherHandler("-33.8688", "151.2093");
        String place_name = weather.getPlace_name();

        JsonObject current = weather.getCurrent();
        JsonObject forecast = weather.getForecast();
        String country = weather.getCountry();


        model.addAttribute("place_name", place_name);
        model.addAttribute("forecast", forecast);
        model.addAttribute("current_temperature", current.get("temperature").getAsDouble());
        model.addAttribute("country", country);
        ArrayList<HashMap<String, String>> hashmap = RequestHandler.convertJsonArraysToHashMaps(forecast);
        model.addAttribute("hashmap", hashmap);


        //  {"icon":"partly_sunny","icon_num":4,"summary":"Partlysunny","temperature":23.8,"wind":{"speed":5.8,"angle":65,"dir":"ENE"},"precipitation":{"total":0.0,"type":"none"},"cloud_cover":20}
        System.out.println(forecast);


        return "homepage";
    }




}