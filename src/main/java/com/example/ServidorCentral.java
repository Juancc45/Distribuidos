package com.example;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.SocketType;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorCentral {

    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("Iniciando Servidor Central Worker...");

        // Verificar conexión a la base de datos antes de continuar
        try (Connection testConn = ConexionDB.conectar()) {
            if (testConn == null || testConn.isClosed()) {
                System.err.println("No se pudo conectar a la base de datos. Terminando el servidor.");
                return;
            } else {
                System.out.println("Conexión a la base de datos verificada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            return;
        }

        try (ZContext context = new ZContext()) {
            ZMQ.Socket worker = context.createSocket(SocketType.DEALER);
            worker.connect("tcp://localhost:5556");

            System.out.println("Worker conectado al broker. Esperando solicitudes...");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] identidad = worker.recv(0);
                worker.recv(0); // frame vacío
                String mensaje = worker.recvStr(0);

                pool.execute(() -> {
                    String respuesta = procesarSolicitud(mensaje);
                    worker.send(identidad, ZMQ.SNDMORE);
                    worker.send("", ZMQ.SNDMORE);
                    worker.send(respuesta);
                });
            }
        }
    }

    private static String procesarSolicitud(String data) {
        try {
            String[] partes = data.split(",");
            String semestre = partes[0];
            String facultad = partes[1];
            String programa = partes[2];
            int cantSalones = Integer.parseInt(partes[3]);
            int cantLabs = Integer.parseInt(partes[4]);

            Connection conn = ConexionDB.conectar();

            int salonesDisponibles = contarAulas(conn, "Salón", semestre, "Disponible");
            int laboratoriosDisponibles = contarAulas(conn, "Laboratorio", semestre, "Disponible");

            boolean asignadoSalones = salonesDisponibles >= cantSalones;
            boolean asignadoLabs = laboratoriosDisponibles >= cantLabs;

            if (asignadoSalones) {
                asignarAulas(conn, programa, semestre, "Salón", cantSalones);
            }

            if (!asignadoLabs && (salonesDisponibles - cantSalones) >= (cantLabs - laboratoriosDisponibles)) {
                asignarAulas(conn, programa, semestre, "Laboratorio", laboratoriosDisponibles);
                asignarAulas(conn, programa, semestre, "Salón", cantLabs - laboratoriosDisponibles);
                asignadoLabs = true;
            } else if (asignadoLabs) {
                asignarAulas(conn, programa, semestre, "Laboratorio", cantLabs);
            }

            String status;
            if (asignadoSalones && asignadoLabs) {
                status = "Aprobada";
            } else {
                System.err.println("⚠️ ALERTA: No hay suficientes aulas para " + programa + " en " + semestre);
                status = "Denegada";
            }

            insertarSolicitud(conn, semestre, facultad, programa, cantSalones, cantLabs, status);
            conn.close();
            return "Resultado: " + status;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error procesando solicitud: " + e.getMessage();
        }
    }

    private static int contarAulas(Connection conn, String tipo, String semestre, String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Aulas a JOIN Programa p ON a.programa_id = p.id " +
                     "WHERE a.tipo = ? AND a.status = ? AND a.semestre = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, estado);
            ps.setString(3, semestre);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void asignarAulas(Connection conn, String programa, String semestre, String tipo, int cantidad) throws SQLException {
        String sql = "UPDATE Aulas SET status = 'Ocupado' WHERE id IN (" +
                     "SELECT id FROM Aulas a JOIN Programa p ON a.programa_id = p.id " +
                     "WHERE a.tipo = ? AND a.status = 'Disponible' AND a.semestre = ? AND p.nombre = ? LIMIT ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, semestre);
            ps.setString(3, programa);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
        }
    }

    private static void insertarSolicitud(Connection conn, String semestre, String facultad, String programa,
                                          int cantSalones, int cantLabs, String status) throws SQLException {
        String sql = "INSERT INTO Solicitud (semestre, facultad_id, programa_id, cant_salon, cant_lab, status) " +
                     "VALUES (?, " +
                     "(SELECT id FROM Facultad WHERE nombre = ?), " +
                     "(SELECT id FROM Programa WHERE nombre = ?), ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, semestre);
            ps.setString(2, facultad);
            ps.setString(3, programa);
            ps.setInt(4, cantSalones);
            ps.setInt(5, cantLabs);
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }
}
