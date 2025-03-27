package com.example;

import java.io.FileWriter;
import java.io.IOException;

public class RegistroSolicitudes {
    public void guardarSolicitud(Solicitud s) {
        try (FileWriter fw = new FileWriter("solicitudes.txt", true)) {
            fw.write("[OK] " + s.getFacultad() + " - " + s.getPrograma() + "\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarFallo(Solicitud s) {
        try (FileWriter fw = new FileWriter("fallos.txt", true)) {
            fw.write("[FALLO] " + s.getFacultad() + " - " + s.getPrograma() + "\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
