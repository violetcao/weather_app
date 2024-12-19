package org.example;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RequestHandler {

    public static JsonObject retrieveJSON(String link) {
        try {
            URL url = new URL(link);
            // Retrieving contents
            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }
            // Converting buffer to string
            String result = sb.toString();

            // Convert String to JSON Object using Gson
            return JsonParser.parseString(result).getAsJsonObject();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static JSONArray retrieveJSONArray(String link) {
        try {
            URL url = new URL(link);
            // Retrieving contents
            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }
            // Converting buffer to string
            String result = sb.toString();
            String fixedText = new String(result.getBytes("ISO-8859-1"), "UTF-8");

            return new JSONArray(fixedText);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static JsonObject requestWeatherInfo(String place_id) {
        return RequestHandler.retrieveJSON(AppConstants.BASE_URL + "point?place_id=" +
                place_id + "&sections=all&timezone=UTC&language=en&units=metric&key=" + AppConstants.API_KEY);

    }

    public static JsonObject convertCoordinatesToPlace(String lat, String lon) {
       // https://www.meteosource.com/api/v1/free/nearest_place?lat=51.5&lon=0&key=YOUR-API-KEY
        return RequestHandler.retrieveJSON(AppConstants.BASE_URL + "nearest_place?lat=" +
                lat + "&lon=" + lon + "&key=" + AppConstants.API_KEY);

    }

    public static ArrayList<HashMap<String, String>> convertJsonArraysToHashMaps(JsonObject forecast) {
        ArrayList<HashMap<String, String>> hashmap_array = new ArrayList<>();


        for(JsonElement j : forecast.getAsJsonArray("data")) {
            HashMap<String, String> values = new HashMap<>();
            values.put("date", j.getAsJsonObject().get("day").getAsString());
            values.put("day_of_week", convertDateToDayOfWeek(j.getAsJsonObject().get("day").getAsString()));
            values.put("summary", j.getAsJsonObject().get("summary").getAsString());
            JsonObject all_day = j.getAsJsonObject().get("all_day").getAsJsonObject();
            values.put("temperature", all_day.get("temperature").getAsString());
            values.put("temperature_min", all_day.get("temperature_min").getAsString());
            values.put("temperature_max", all_day.get("temperature_max").getAsString());
            hashmap_array.add(values);
        }


        return hashmap_array;
    }

    public static String convertDateToDayOfWeek(String dateString) {
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);

        // Get the day of the week
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Convert to a String (e.g., "WEDNESDAY")
        String dayName = dayOfWeek.toString();

        return dayName.substring(0, 1).toUpperCase() + dayName.substring(1).toLowerCase();
    }

    
 
}
