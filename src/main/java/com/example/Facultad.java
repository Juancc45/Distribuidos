package com.example;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class Facultad {
    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Uso: java Facultad <direccion_servidor> <facultad> <semestre> <programa> <salones> <laboratorios>");
            return;
        }

        String direccionServidor = args[0];  // Ejemplo: tcp://localhost:5555 si es local
                                             // o tcp://192.168.1.100:5555 poniendo la ip del pc
                                             // que corra el proceso de servidor si no es en local
        String facultad = args[1];
        String semestre = args[2];
        String programa = args[3];
        int salones;
        int laboratorios;

        try {
            salones = Integer.parseInt(args[4]);
            laboratorios = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.out.println("Error: salones y laboratorios deben ser números enteros.");
            return;
        }

        String solicitud = String.format("%s,%s,%s,%d,%d", facultad, semestre, programa, salones, laboratorios);

        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket requester = context.socket(SocketType.REQ);
            requester.connect(direccionServidor); // <-- Se conecta usando el argumento

            System.out.println("Enviando solicitud: " + solicitud);
            requester.send(solicitud);

            String respuesta = requester.recvStr();
            System.out.println("Respuesta del servidor: " + respuesta);
        }
    }
}
