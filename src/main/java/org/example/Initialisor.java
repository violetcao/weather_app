package org.example;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class Initialisor {
    private Connection conn;
    public Initialisor() {
        this.conn = initialiseToDatabase();
        requestListOfPlaceID();
    }

    public Connection initialiseToDatabase() {

        try {
            // Pointing to where the database.db is located
            String path = "jdbc:sqlite:src/main/resources/database.db";

            // Creating a connection, will create a new database.db if one doesn't exist.
            conn = DriverManager.getConnection(path);

            // Drop all tables (if exist)
            dropTable(conn, "Country");
            dropTable(conn, "City");

            // Creating all necessary tables for data storage
            createTables(conn);


        } catch (SQLException e) {
            System.out.println("Error when connecting to database: " + e.getMessage());
        }
        if (conn != null) {
            System.out.println("Database connection established");
        }

        return conn;
    }

    // Remove table (if exists) from database
    public static void dropTable(Connection conn, String table) {
        String query = "DROP TABLE IF EXISTS " + table + ";";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error when dropping tables: " + e.getMessage());
        }
    }

    // Create all necessary tables in database
    public void createTables(Connection conn) {
        System.out.println("Creating tables!");

        var countryTable = "CREATE TABLE IF NOT EXISTS Country ("
                + "	countryID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " countryName TEXT UNIQUE NOT NULL,"
                + " cca2 TEXT UNIQUE NOT NULL"
                + ");";

        var cityTable = "CREATE TABLE IF NOT EXISTS City ("
                + " cityID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	cityName TEXT NOT NULL,"
                + " lat REAL NOT NULL,"
                + " lon REAL NOT NULL,"
                + "	cca2 TEXT REFERENCES Country(cca2) NOT NULL,"
                + " CONSTRAINT unique_city_country UNIQUE (cityName, cca2)"
                + ");";

        try {
            conn.createStatement().execute(countryTable);
            conn.createStatement().execute(cityTable);
            System.out.println("Tables created!");

        } catch (SQLException e) {
            System.out.println("Error when creating tables: " + e.getMessage());
        }
    }




    public void requestListOfPlaceID() {
        // Create array from country code for saving cities in each country later
        ArrayList<String> countries = new ArrayList<>();
        // Save countries in the countryTable first
        JSONArray jsonArray = RequestHandler.retrieveJSONArray(AppConstants.COUNTRY_API);
        // Loop through array
        for (int i = 0; i < Objects.requireNonNull(jsonArray).length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            String countryName = object.getJSONObject("name").get("common").toString();
            String cca2 = object.get("cca2").toString();
            if (countryName != null && cca2 != null) {
                // Save to sqlite database
                insertToCountry(countryName, cca2);
                countries.add(cca2);
            }
        }

        // Now save cities
        for (String countryCode : countries) {
            JsonObject json = RequestHandler.retrieveJSON(AppConstants.CITY_BASE_API + "country=" + countryCode +
                    "&featureClass=P&username=" + AppConstants.USERNAME);
            JsonArray js = Objects.requireNonNull(json).getAsJsonArray("geonames");

            for (int i = 0; i < js.size(); i++) {
                JsonObject jsonObject = js.get(i).getAsJsonObject();
                float lat = jsonObject.get("lat").getAsFloat();
                float lon = jsonObject.get("lng").getAsFloat();
                String name = jsonObject.get("name").getAsString();
                if (name != null) {
                    insertToCity(name, lat, lon, countryCode);

                }

            }

        }

    }

    public void insertToCountry(String countryName, String cca2) {
        try {
            String query = "INSERT INTO Country (countryName, cca2)" +
                    "VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);



            statement.setString(1, countryName);
            statement.setString(2, cca2);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("An error occurred while inserting country: " + e.getMessage());
        }
    }


    public void insertToCity(String cityName, float lat, float lon, String cca2) {

        try {
            String query = "INSERT INTO City (cityName, lat, lon, cca2)" +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, cityName);
            statement.setFloat(2, lat);
            statement.setFloat(3, lon);
            statement.setString(4, cca2);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("An error occurred while inserting city: " + e.getMessage());

        }
    }

}
