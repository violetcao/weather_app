package org.example;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final Connection conn;

    public RequestHandler() {
        ApplicationHandler application = new ApplicationHandler();
        this.conn = application.getConnection();
    }


    ///// Web requests /////
    public static JsonObject retrieveJSON(String link) {
        try {
            URL url = new URL(link);
            // Retrieving contents
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
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
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            // Converting buffer to string
            String result = sb.toString();


            return new JSONArray(result);

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
        return RequestHandler.retrieveJSON(AppConstants.BASE_URL + "nearest_place?lat=" +
                lat + "&lon=" + lon + "&key=" + AppConstants.API_KEY);

    }


    //// Database requests /////


    public HashMap<Integer, String> retrievePlaceSuggestions(String string, int limit, int offset) {
        HashMap<Integer, String> suggestions = new HashMap<>();

        try {
            String query = "SELECT * FROM City WHERE cityName LIKE ? LIMIT " + limit + " OFFSET " + offset;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, string + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Get country info
                String country = getCountryFromCode(rs.getString("cca2"));

                // if valid country
                if (country != null) {
                    suggestions.put(rs.getInt("cityId"), rs.getString("cityName") + ", " + country);
                }
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return suggestions;

    }

    public String getCountryFromCode(String country_code) {
        try {
            String sql = "SELECT * FROM Country WHERE cca2= ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, country_code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("countryName");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;


    }

    public Map<String, String> convertPlaceIDToCoordinates(int place_id) {
        Map<String, String> coordinates = new HashMap<>();

        try {
            String sql = "SELECT * FROM City WHERE cityID = ? LIMIT 1";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, place_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coordinates.put("lat", rs.getString("lat"));
                coordinates.put("lon", rs.getString("lon"));
            }
            System.out.println(coordinates);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return coordinates;

    }


    ///// Helper functions /////

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
