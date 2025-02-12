package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApplicationHandler {
    protected Connection conn;


    public ApplicationHandler() {
        try {
            // Pointing to where the database.db is located
            String path = "jdbc:sqlite:src/main/resources/database.db";

            // Creating a connection, will create a new database.db if one doesn't exist.
            conn = DriverManager.getConnection(path);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public Connection getConnection() {
        return conn;
    }



}
