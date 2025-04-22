package com.example;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Facultad {

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://10.43.103.67:5555"); // Broker (ROUTER) escucha aquí

            Scanner scanner = new Scanner(System.in);
            System.out.println("Cliente Facultad conectado. Enviando solicitudes...");

            while (true) {
                System.out.println("\nIngrese los datos de la solicitud:");
                System.out.print("Semestre: ");
                String semestre = scanner.nextLine();

                System.out.print("Nombre de la Facultad: ");
                String facultad = scanner.nextLine();

                System.out.print("Nombre del Programa: ");
                String programa = scanner.nextLine();

                System.out.print("Tipo de Aula: ");
                String tipo = scanner.nextLine();

                System.out.print("Cantidad de Aulas/Laboratorios: ");
                int laboratorios = Integer.parseInt(scanner.nextLine());

                // Construir mensaje
                String mensaje = semestre + "," + facultad + "," + programa + "," + tipo + "," + laboratorios;

                // Enviar solicitud
                socket.send(mensaje);

                // Esperar respuesta
                String respuesta = socket.recvStr();
                System.out.println("Respuesta del servidor: " + respuesta);

                System.out.print("\n¿Desea enviar otra solicitud? (s/n): ");
                String continuar = scanner.nextLine();
                if (!continuar.equalsIgnoreCase("s")) break;
            }

            System.out.println("Cliente finalizado.");
        }
    }
}
