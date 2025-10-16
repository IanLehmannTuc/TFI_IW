package tfi.model.enums;

public enum NivelEmergencia {
    CRITICA(5),
    EMERGENCIA(4),
    URGENCIA(3),
    URGENCIA_MENOR(2),
    SIN_URGENCIA(1);
    
    private final int prioridad;
    
    NivelEmergencia(int prioridad) {
        this.prioridad = prioridad;
    }
    
    public int getPrioridad() {
        return prioridad;
    }
}

