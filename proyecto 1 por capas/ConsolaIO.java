// presentation/ConsolaIO.java
/**
 * UTILIDAD DE ENTRADA/SALIDA - Lectura con validación básica desde consola
 * Propósito: Mejorar la experiencia en consola sin reemplazar validaciones de negocio.
 * Cada método leer* solicita entrada, la valida, y permite al usuario escribir "cancelar" para abortar
 */
import java.util.Scanner;
import java.util.function.Predicate;

public class ConsolaIO {

    // Scanner para leer entrada del usuario desde consola
    private final Scanner sc;

    public ConsolaIO(Scanner sc) {
        this.sc = sc;
    }
    
    // === MÉTODOS PÚBLICOS - Lectura con validación automática ===

    /**
     * Lee texto no vacío del usuario
     */
    public String leerTexto(String mensaje) {
        return leer(mensaje, t -> !t.isBlank(),
                "Este campo no puede estar vacio");
    }

    public Integer leerEntero(String mensaje) {
        return leer(mensaje, t -> t.matches("\\d+"),
                "Ingrese un numero valido",
                Integer::parseInt);
    }

    public Double leerDecimal(String mensaje) {
        return leer(mensaje, t -> t.matches("\\d+(\\.\\d+)?"),
                "Ingrese un numero valido",
                Double::parseDouble);
    }

    public String leerTelefono(String mensaje) {
        return leer(mensaje, t -> t.matches("\\d{10}"),
                "Telefono invalido");
    }

    public String leerUltimos4(String mensaje) {
        return leer(mensaje, t -> t.matches("\\d{4}"),
                "Dato invalido");
    }

    public String leerCorreo(String mensaje) {
        return leer(mensaje, t -> t.contains("@"),
                "Correo invalido");
    }

    public Integer leerNoches(String mensaje) {
        return leer(mensaje, t -> t.matches("\\d+") && Integer.parseInt(t) > 0,
                "Las noches deben ser mayores que cero",
                Integer::parseInt);
    }

    // === MÉTODOS PRIVADOS - Lógica reutilizable ===
    
    /**
     * Método genérico para leer y validar una cadena de texto
     * Repite el prompt hasta que el usuario ingrese un valor válido
     * El usuario puede escribir "cancelar" para abortar (retorna null)
     */
    private String leer(String mensaje,
                        Predicate<String> validador,
                        String error) {

        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();

            if (texto.equalsIgnoreCase("cancelar")) return null;
            if (validador.test(texto)) return texto;

            System.out.println(error);
        }
    }

    /**
     * Método genérico para leer, validar y convertir (ej: String → Integer)
     * Utilizado para métodos que necesitan conversión de tipo
     */
    private <T> T leer(
                String mensaje,
                Predicate<String> validador,
                String error,
                Convertidor<T> convertidor) {
        // Primero valida la entrada como string
        String texto = leer(mensaje, validador, error);
        if (texto == null) return null; // Usuario escribió "cancelar"
        // Luego convierte al tipo deseado
        return convertidor.convertir(texto);
    }
    
    /**
     * Interfaz funcional para convertir String a un tipo genérico T
     */
    private interface Convertidor<T> {
        T convertir(String texto);
    }
}
