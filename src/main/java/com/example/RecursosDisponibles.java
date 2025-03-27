package com.example;

public class RecursosDisponibles {
    private int salones = 380;
    private int laboratorios = 60;

    public synchronized boolean asignar(int s, int l) {
        if (salones >= s && laboratorios >= l) {
            salones -= s;
            laboratorios -= l;
            return true;
        }
        return false;
    }

    public synchronized boolean asignarConMoviles(int s, int l) {
        if (salones >= s + l) {
            salones -= (s + l);
            return true;
        }
        return false;
    }

    public synchronized int getSalones() { return salones; }
    public synchronized int getLaboratorios() { return laboratorios; }
}
