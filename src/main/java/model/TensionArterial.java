package model;

public class TensionArterial {
    private Frecuencia frecuenciaSistolica;
    private Frecuencia frecuenciaDiastolica;

    public TensionArterial() {
        this.frecuenciaSistolica = frecuenciaSistolica;
        this.frecuenciaDiastolica = frecuenciaDiastolica;
    }

    public Frecuencia getFrecuenciaSistolica() {return this.frecuenciaSistolica;}

    public void setFrecuenciaSistolica(Frecuencia frecuenciaSistolica) {
        this.frecuenciaSistolica = frecuenciaSistolica;
    }

    public Frecuencia getFrecuenciaDiastolica() {return this.frecuenciaDiastolica;}

    public void setFrecuenciaDiastolica(Frecuencia frecuenciaDiastolica) {
        this.frecuenciaDiastolica = frecuenciaDiastolica;
    }
}
