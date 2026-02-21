package com.example.s_balneare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        //  PROVA UUID (da scartare)
        //  UUID uuid = UUID.randomUUID();
        //  System.out.println(uuid);
        //  UUID genera 36 caratteri, quindi va creata nel DB una VARCHAR(36) per UUID

        //  UUID complicato, usiamo direttamente una variabile int per tutti gli ID e nelle table:
        //  id INT PRIMARY KEY AUTO_INCREMENT

        /*  PROVA JDBC + DOCKER
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
         */
    }
}


