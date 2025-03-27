package com.example;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorCentral {

    private static RecursosDisponibles recursos = new RecursosDisponibles();
    private static RegistroSolicitudes registro = new RegistroSolicitudes();
    private static AsignadorAulas asignador = new AsignadorAulas(recursos, registro);
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket router = context.createSocket(SocketType.ROUTER);
            router.bind("tcp://*:5555");

            Logger.log("Servidor Central ZeroMQ (ROUTER) iniciado en puerto 5555...");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] identity = router.recv(0);
                router.recv(0); // Empty frame
                String message = router.recvStr(0);

                long tiempoInicio = System.nanoTime();
                Logger.log("Solicitud recibida: " + message);

                pool.execute(() -> {
                    Solicitud solicitud = parsearSolicitud(message);
                    String respuesta;
                    long tiempoFin;
                    long duracionMs;

                    if (solicitud != null) {
                        boolean exito = asignador.procesar(solicitud);
                        respuesta = (exito ? "ASIGNADO" : "NO ASIGNADO") + ": " + solicitud.getPrograma();
                    } else {
                        respuesta = "ERROR: solicitud inválida";
                    }

                    tiempoFin = System.nanoTime();
                    duracionMs = (tiempoFin - tiempoInicio) / 1_000_000;

                    Logger.log("Respuesta enviada: " + respuesta + " | Tiempo de procesamiento: " + duracionMs + " ms");

                    // Enviar respuesta
                    router.send(identity, ZMQ.SNDMORE);
                    router.send("", ZMQ.SNDMORE);
                    router.send(respuesta);
                });
            }
        }
    }

    private static Solicitud parsearSolicitud(String data) {
        try {
            String[] partes = data.split(",");
            String facultad = partes[0];
            String semestre = partes[1];
            String programa = partes[2];
            int salones = Integer.parseInt(partes[3]);
            int labs = Integer.parseInt(partes[4]);
            return new Solicitud(facultad, semestre, programa, salones, labs);
        } catch (Exception e) {
            Logger.log("Error al parsear solicitud: " + e.getMessage());
            return null;
        }
    }
}

