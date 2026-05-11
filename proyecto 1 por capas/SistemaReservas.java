// application/SistemaReservas.java
/**
 * CAPA DE APLICACION (LOGICA DE NEGOCIO) - Control central del sistema de reservas
 * Responsabilidades:
 * - Gestionar cabañas guardadas en catálogo
 * - Crear nuevas reservas
 * - Cancelar reservas
 * - Generar reportes y consultas (reservas, ranking)
 */
import java.util.ArrayList;
import java.util.List;

public class SistemaReservas {
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String METODO_PAGO_EFECTIVO = "Efectivo";
    private static final String METODO_PAGO_TARJETA = "Tarjeta";

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

    /**
     * Crea una nueva reserva validando todas las reglas de negocio.
     */
    public Reserva reservar(int idCabana, String nombre, String telefono,
                            String correo, int noches, String metodoPago, String ultimos4) {
        Cabana cabana = buscarCabana(idCabana);
        if (cabana == null) throw new IllegalArgumentException("La cabaña no existe");

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }

        if (telefono == null || !telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos");
        }

        if (correo == null || !correo.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }

        if (noches <= 0) {
            throw new IllegalArgumentException("La cantidad de noches debe ser mayor que cero");
        }

        if (!METODO_PAGO_EFECTIVO.equals(metodoPago) && !METODO_PAGO_TARJETA.equals(metodoPago)) {
            throw new IllegalArgumentException("El método de pago debe ser Efectivo o Tarjeta");
        }

        if (METODO_PAGO_TARJETA.equals(metodoPago)) {
            if (ultimos4 == null || !ultimos4.matches("\\d{4}")) {
                throw new IllegalArgumentException("Para pago con tarjeta, los últimos 4 dígitos son obligatorios y deben tener 4 números");
            }
        } else if (ultimos4 != null) {
            throw new IllegalArgumentException("Para pago en efectivo no deben enviarse últimos 4 dígitos");
        }

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

    /**
     * Retorna todas las reservas (activas y canceladas).
     */
    public List<Reserva> verReservas() {
        return new ArrayList<>(reservas);
    }

    /**
     * Retorna lista de reservas activas (no canceladas).
     */
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

    /**
     * Genera el ranking de cabañas contando solo reservas activas.
     */
    public List<String> ranking() {
        List<String> lista = new ArrayList<>();
        for (Cabana c : catalogo.listar()) {
            int count = 0;
            for (Reserva r : reservas)
                if (r.estaActiva() && r.getCabana().getId() == c.getId()) count++;
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
