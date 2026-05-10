// domain/Cliente.java
/**
 * Clase disponible para futuras extensiones.
 * Actualmente NO se usa en el sistema (las reservas usan strings directamente para mantener simplicidad).
 * Si necesitas validaciones más estrictas, puedes usarla en lugar de los parámetros string.
 */
public class Cliente {
    private final String nombreCompleto;
    private final String telefono;
    private final String correo;

    /* Constructor con validación robusta de datos
     * Lanza excepciones si algún campo no es válido */
    public Cliente(String nombreCompleto, String telefono, String correo) {
        if (!esNombreValido(nombreCompleto)) {
            throw new IllegalArgumentException("El nombre del cliente no es válido.");
        }
        if (!esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos.");
        }
        if (!esCorreoValido(correo)) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }

        this.nombreCompleto = normalizarEspacios(nombreCompleto);
        this.telefono = telefono;
        this.correo = correo.trim();
    }

    // GETTERS - Metodos de acceso a los datos del cliente, inmutable
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    // VALIDACIONES - Métodos privados para verificar datos del cliente
    
    /**
     * Valida que el nombre no esté vacío y contenga solo caracteres válidos 
     */
    private static boolean esNombreValido(String nombre) {
        if (nombre == null) {
            return false;
        }
        String limpio = normalizarEspacios(nombre);
        return !limpio.isBlank() && limpio.matches("[\\p{L} .'-]+");
    }

    /**
     * Valida que el teléfono tenga exactamente 10 dígitos
     */
    private static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{10}");
    }

    /**
     * Valida que el correo tenga formato válido (contiene @ y dominio)
     */
    private static boolean esCorreoValido(String correo) {
        if (correo == null) {
            return false;
        }
        String limpio = correo.trim();
        return limpio.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Elimina espacios extra y normaliza el texto
     */
    private static String normalizarEspacios(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    /**
     * Formato para mostrar datos completos del cliente
     */
    @Override
    public String toString() {
        return nombreCompleto + " | Tel: " + telefono + " | Correo: " + correo;
    }
}