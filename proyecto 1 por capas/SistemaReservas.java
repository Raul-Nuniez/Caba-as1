// application/SistemaReservas.java
/**
 * CAPA DE APLICACION (LOGICA DE NEGOCIO) - Control central del sistema de reservas
 * Responsabilidades:
 * - Gestionar cabañas guradadas en catalogo
 * - Crear nuevas reservas
 * - Cancelar reservas
 * - Generar reportes (reservas activas, ranking)
 */
import java.util.ArrayList;
import java.util.List;

public class SistemaReservas {

    // Catálogo de cabañas disponibles
    private CatalogoAlojamientos catalogo;
    // Historial de todas las reservas (activas y canceladas)
    private List<Reserva> reservas = new ArrayList<>();
    // Contador auto-incremental para generar folios únicos (comienza en 1001)
    private int siguienteFolio = 1001;

    // Constructor - Inicializa el sistema cargando cabañas de ejemplo
    public SistemaReservas() {
        this.catalogo = new CatalogoAlojamientos();
        this.catalogo.cargarIniciales();
    }

    public void agregarCabana(Cabana cabana) {
        catalogo.agregar(cabana);
    }

    public List<Cabana> verCabanas() {
        return catalogo.listar();
    }

    // Crea una nueva reserva si la cabaña existe
    // Genera un folio único automáticamente
    public Reserva reservar(int idCabana, String nombre, String telefono,
                            String correo, int noches, String metodoPago, String ultimos4) {
        // Verificar que la cabaña exista
        Cabana cabana = buscarCabana(idCabana);
        if (cabana == null) throw new IllegalArgumentException("Cabana no encontrada");

        // Crear la reserva con un folio único e incrementar el contador
        Reserva r = new Reserva(siguienteFolio++, cabana, nombre, telefono,
                                correo, noches, metodoPago, ultimos4);
        reservas.add(r);
        return r;
    }

    // Cancela una reserva activa por su número de folio
    public void cancelar(int folio) {
        Reserva r = buscarReserva(folio);
        if (r == null) throw new IllegalArgumentException("Reserva no encontrada");
        r.cancelar();
    }

    // Retorna lista de todas las reservas activas (no canceladas)
    public List<Reserva> verReservasActivas() {
        List<Reserva> activas = new ArrayList<>();
        for (Reserva r : reservas) 
            if (r.estaActiva()) activas.add(r);
        return activas;
    }

    // Busca cabañas dentro de un rango de precio por noche
    public List<Cabana> buscarPorPrecio(double min, double max) {
        return catalogo.buscarPorPrecio(min, max);
    }

    /* Genera ranking de cabañas ordenadas por cantidad de reservas
       Muestra cuál es la más solicitada*/
    public List<String> ranking() {
        List<String> lista = new ArrayList<>();
        // Para cada cabaña, contar cuántas reservas tiene
        for (Cabana c : catalogo.listar()) {
            int count = 0;
            for (Reserva r : reservas)
                if (r.getCabana().getId() == c.getId()) count++;
            lista.add(c.getNombre() + ": " + count + " reservas");
        }
        return lista;
    }

        // Busca una cabaña en el catálogo por su ID
    private Cabana buscarCabana(int id) {
        return catalogo.buscarPorId(id);
    }
    
    // Busca una reserva en el historial por su número de folio
    private Reserva buscarReserva(int folio) {
        for (Reserva r : reservas) 
            if (r.getFolio() == folio) return r;
        return null;
    }
}
