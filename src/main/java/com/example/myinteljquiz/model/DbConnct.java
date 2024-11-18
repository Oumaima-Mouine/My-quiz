package com.example.myinteljquiz.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnct {
    public static Connection databaseLink;


    public static Connection getConnection(){
        String databaseName = "quizz";
        String dbUser = "root";
        String dbPassword = "";
        String url = "jdbc:mysql://localhost/"+databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url,dbUser,dbPassword);
        }catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return  databaseLink;
    }
    // Method to close the database connection
    public void closeConnection() {
        if (databaseLink != null) {
            try {
                databaseLink.close();
                databaseLink = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
