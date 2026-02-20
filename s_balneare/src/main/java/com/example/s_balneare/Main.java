package com.example.s_balneare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Must match the "ports" mapping in docker-compose (3307 -> 3306)
        String url = "jdbc:mysql://localhost:3307/test";
        String user = "root";
        String password = "prova";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to Docker MySQL successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


