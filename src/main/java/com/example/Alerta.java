package com.example;

public class Alerta {
    public static void generar(Solicitud s) {
        System.err.println("ALERTA: No hay suficientes aulas para: " +
            s.getFacultad() + " - " + s.getPrograma());
    }
}
