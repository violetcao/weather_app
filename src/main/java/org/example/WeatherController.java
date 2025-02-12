package org.example;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherController {

    public static String reformatInput(String searchInput) {
        // Trim trailing white spaces front and back
        searchInput = searchInput.toLowerCase().trim();

        // Capitalize each first word
        String[] words = searchInput.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
            sb.append(" ");
        }

        sb.deleteCharAt(sb.length() - 1);
        searchInput = sb.toString();

        return searchInput;
    }

    public static Map<String, Object> retrieveAllAttributes(int city_id) {
        // Create map to return
        Map<String, Object> weatherData = new HashMap<>();

        // Get coordinates
        RequestHandler requestHandler = new RequestHandler();
        Map<String, String> coordinates = requestHandler.convertPlaceIDToCoordinates(city_id);

        CityHandler weather = new CityHandler(coordinates.get("lat"), coordinates.get("lon"));
        String place_name = weather.getPlace_name();

        JsonObject current = weather.getCurrent();
        JsonObject forecast = weather.getForecast();
        String country = weather.getCountry();
        ArrayList<HashMap<String, String>> hashmap = RequestHandler.convertJsonArraysToHashMaps(forecast);

        weatherData.put("place_name", place_name);
        weatherData.put("forecast", forecast);
        weatherData.put("current_temperature", current.get("temperature").getAsDouble());
        weatherData.put("current_icon", current.get("icon_num").getAsString() + ".png");
        weatherData.put("country", country);
        weatherData.put("summary", current.get("summary").getAsString());
        weatherData.put("wind", current.get("wind").getAsJsonObject());
        weatherData.put("precipitation", current.get("precipitation").getAsJsonObject());
        weatherData.put("cloud_cover", current.get("cloud_cover").getAsString());
        weatherData.put("weather_week", hashmap);

        return weatherData;

    }

    public static Map<Integer, String> generateSimilarCities(int limit, int offset, String name) {
        RequestHandler requestHandler = new RequestHandler();
        return requestHandler.retrievePlaceSuggestions(name, limit, offset);

    }

}
