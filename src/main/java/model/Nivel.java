package model;

public class Nivel {
    private int nivel;
    private String nombre;

    public Nivel(int nivel, String nombre) {
        this.nivel = nivel;
        this.nombre = nombre;
    }

    public int getNivel() {return nivel;}

    public void setNivel(int nivel) {this.nivel = nivel;}

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}
}
