package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = "servidor_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String mensaje) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entrada = "[" + timestamp + "] " + mensaje;

        // Mostrar en consola
        System.out.println(entrada);

        // Guardar en archivo
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(entrada + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }
}
