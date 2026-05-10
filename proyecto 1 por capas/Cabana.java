// domain/Cabana.java
/**
 * MODELO - Representa una cabaña disponible para reservar
 * Almacena: ID único, nombre descriptivo, precio por noche
 */
public class Cabana {

    // Identificador único de la cabaña
    private int id;
    // Nombre descriptivo (ej: "Cabana de lujo")
    private String nombre;
    // Precio en pesos por cada noche de hospedaje
    private double precioPorNoche;

    public Cabana(int id, String nombre, double precioPorNoche) {
        this.id = id;
        this.nombre = nombre;
    // Getters - métodos para acceder a los datos
        this.precioPorNoche = precioPorNoche;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioPorNoche() { return precioPorNoche; }
// Formato para mostrar la cabaña en listas
    
    @Override
    public String toString() {
        return id + ". " + nombre + " - $" + precioPorNoche + " por noche";
    }
}
