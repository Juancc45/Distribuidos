package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecursosDisponibles {

    private int salones;
    private int laboratorios;
    private String semestre = "2025-10";

    public RecursosDisponibles() {
        cargarRecursosDesdeBD();
    }

    private void cargarRecursosDesdeBD() {
        String sql = "SELECT salones, laboratorios FROM recursos WHERE semestre = ? LIMIT 1";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, semestre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salones = rs.getInt("salones");
                laboratorios = rs.getInt("laboratorios");
            } else {
                salones = 380;
                laboratorios = 60;
                guardarRecursosIniciales();
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar recursos desde MySQL: " + e.getMessage());
        }
    }

    private void guardarRecursosIniciales() {
        String sql = "INSERT INTO recursos (semestre, salones, laboratorios) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, semestre);
            pstmt.setInt(2, salones);
            pstmt.setInt(3, laboratorios);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar recursos iniciales en MySQL: " + e.getMessage());
        }
    }

    public synchronized boolean asignar(int s, int l) {
        if (salones >= s && laboratorios >= l) {
            salones -= s;
            laboratorios -= l;
            actualizarRecursos();
            return true;
        }
        return false;
    }

    public synchronized boolean asignarConMoviles(int s, int l) {
        if (salones >= s + l) {
            salones -= (s + l);
            actualizarRecursos();
            return true;
        }
        return false;
    }

    private void actualizarRecursos() {
        String sql = "UPDATE recursos SET salones = ?, laboratorios = ? WHERE semestre = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, salones);
            pstmt.setInt(2, laboratorios);
            pstmt.setString(3, semestre);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar recursos en MySQL: " + e.getMessage());
        }
    }

    public synchronized int getSalones() { return salones; }
    public synchronized int getLaboratorios() { return laboratorios; }
}
