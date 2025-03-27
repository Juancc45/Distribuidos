package com.example;

public class AsignadorAulas {
    private RecursosDisponibles recursos;
    private RegistroSolicitudes registro;

    public AsignadorAulas(RecursosDisponibles recursos, RegistroSolicitudes registro) {
        this.recursos = recursos;
        this.registro = registro;
    }

    public boolean procesar(Solicitud solicitud) {
        boolean asignado = recursos.asignar(solicitud.getSalones(), solicitud.getLaboratorios());

        if (!asignado) {
            boolean conMoviles = recursos.asignarConMoviles(solicitud.getSalones(), solicitud.getLaboratorios());
            if (!conMoviles) {
                Alerta.generar(solicitud);
                registro.guardarFallo(solicitud);
                return false;
            }
        }

        registro.guardarSolicitud(solicitud);
        return true;
    }
}
