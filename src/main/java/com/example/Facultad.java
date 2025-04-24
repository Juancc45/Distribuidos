package com.example;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Facultad {

    public static void main(String[] args) {
        // Verificar que se proporcionen todos los argumentos necesarios
        if (args.length < 5) {
            System.out.println("Uso: java com.example.Facultad <semestre> <facultad> <programa> <salones> <laboratorios>");
            System.out.println("Ejemplo: java com.example.Facultad 2025-10 \"Ingeniería\" \"Sistemas\" 5 3");
            return;
        }

        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://10.43.103.67:5555"); // Broker (ROUTER) escucha aquí

            // Obtener los parámetros de los argumentos
            String semestre = args[0];
            String facultad = args[1];
            String programa = args[2];
            int salones;
            int laboratorios;

            try {
                salones = Integer.parseInt(args[3]);
                laboratorios = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.err.println("Error: Los valores de salones y laboratorios deben ser números enteros.");
                return;
            }

            System.out.println("Cliente Facultad conectado. Enviando solicitud...");

            // Mostrar los datos de la solicitud
            System.out.println("\nDatos de la solicitud:");
            System.out.println("Semestre: " + semestre);
            System.out.println("Nombre de la Facultad: " + facultad);
            System.out.println("Nombre del Programa: " + programa);
            System.out.println("Cantidad de salones: " + salones);
            System.out.println("Cantidad de laboratorios: " + laboratorios);

            // Construir mensaje
            String mensaje = semestre + "," + facultad + "," + programa + "," + salones + "," + laboratorios;

            // Enviar solicitud
            socket.send(mensaje);

            // Esperar respuesta
            String respuesta = socket.recvStr();
            System.out.println("Respuesta del servidor: " + respuesta);

            System.out.println("Cliente finalizado.");
        }
    }
}