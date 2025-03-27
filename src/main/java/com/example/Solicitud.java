package com.example;

public class Solicitud {
    private String facultad;
    private String semestre;
    private String programa;
    private int salones;
    private int laboratorios;

    public Solicitud(String facultad, String semestre, String programa, int salones, int laboratorios) {
        this.facultad = facultad;
        this.semestre = semestre;
        this.programa = programa;
        this.salones = salones;
        this.laboratorios = laboratorios;
    }

    public String getFacultad() { return facultad; }
    public String getSemestre() { return semestre; }
    public String getPrograma() { return programa; }
    public int getSalones() { return salones; }
    public int getLaboratorios() { return laboratorios; }
}

