// domain/CatalogoAlojamientos.java
/**
 * MODELO - Almacén de cabañas disponibles
 * Responsabilidades:
 * - Cargar cabañas iniciales
 * - Buscar cabañas por ID
 * - Filtrar cabañas por rango de precio
 * - Agregar nuevas cabañas
 */
import java.util.ArrayList;
import java.util.List;

public class CatalogoAlojamientos {

    // Lista de todas las cabañas disponibles en el sistema
    private List<Cabana> alojamientos = new ArrayList<>();

    /**
     * Carga 5 cabañas de ejemplo al iniciar el sistema
     * Esto simula cabañas que existirían en una base de datos
     */
    public void cargarIniciales() {
        alojamientos.add(new Cabana(1, "Cabana vista al lago", 2500));
        alojamientos.add(new Cabana(2, "Cabana familiar", 2000));
        alojamientos.add(new Cabana(3, "Cabana de lujo", 4000));
        alojamientos.add(new Cabana(4, "Cabana estandar", 1500));
        alojamientos.add(new Cabana(5, "Cabana romantica", 3000));
    }

    /**
     * Devuelve lista completa de cabañas
     */
    public List<Cabana> listar() {
        return alojamientos;
    }
    
    /**
     * Agregar una nueva cabaña al catálogo
     */
    public void agregar(Cabana c) {
        alojamientos.add(c);
    }
    
    /**
     * Busca una cabaña específica por su ID
     * Retorna null si no existe
     */
    public Cabana buscarPorId(int id) {
        for (Cabana a : alojamientos)
            if (a.getId() == id) return a;
        return null;
    }
    
    /**
     * Filtra cabañas dentro de un rango de precio por noche
     */
    public List<Cabana> buscarPorPrecio(double min, double max) {
        List<Cabana> resultado = new ArrayList<>();
        for (Cabana a : alojamientos)
            if (a.getPrecioPorNoche() >= min && a.getPrecioPorNoche() <= max)
                resultado.add(a);
        return resultado;
    }
}
