package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestion_aulas";
    private static final String USER = "tu_usuario"; // Reemplaza con tu usuario real de MySQL
    private static final String PASSWORD = "tu_contraseña"; // Reemplaza con tu contraseña real

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return null;
        }
    }
}
