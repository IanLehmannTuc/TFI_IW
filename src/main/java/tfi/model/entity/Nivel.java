package tfi.model.entity;

import tfi.model.enums.NivelEmergencia;
import java.time.Duration;

public class Nivel {
    private NivelEmergencia nivelEmergencia;
    private String nombre;
    private Duration duracionMaximaEspera;

    public Nivel(NivelEmergencia nivelEmergencia, String nombre, Duration duracionMaximaEspera) {
        this.nivelEmergencia = nivelEmergencia;
        this.nombre = nombre;
        this.duracionMaximaEspera = duracionMaximaEspera;
    }

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Duration getDuracionMaximaEspera() {
        return duracionMaximaEspera;
    }

    public void setDuracionMaximaEspera(Duration duracionMaximaEspera) {
        this.duracionMaximaEspera = duracionMaximaEspera;
    }
}
