package org.example;

import com.google.gson.JsonObject;

import java.util.Objects;

public class WeatherHandler {
    private String place_name;
    private String country;
    private String place_id;
    private String lat;
    private String lon;
    private JsonObject forecast;
    private int elevation;
    private JsonObject current;

    public WeatherHandler(String lat, String lon) {
        JsonObject jsonObject = Objects.requireNonNull(RequestHandler.convertCoordinatesToPlace(lat, lon));

        this.place_name = jsonObject.get("name").getAsString();
        this.country = jsonObject.get("country").getAsString();
        this.lat = jsonObject.get("lat").getAsString();
        this.lon = jsonObject.get("lon").getAsString();
        this.place_id = jsonObject.get("place_id").getAsString();

        jsonObject = Objects.requireNonNull(RequestHandler.requestWeatherInfo(place_id));

        this.elevation = jsonObject.get("elevation").getAsInt();
        this.forecast = jsonObject.get("daily").getAsJsonObject();
        this.current = jsonObject.get("current").getAsJsonObject();


    }

    public String getPlace_id() {
        return place_id;
    }

    public String getPlace_name() { return place_name; }

    public String getCountry() {
        return country;
    }

    public JsonObject getForecast() {
        return forecast;
    }

    public int getElevation() {
        return elevation;
    }

    public JsonObject getCurrent() {
        return current;
    }

}
