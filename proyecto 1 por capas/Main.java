
/**
 * CAPA DE PRESENTACION - Interfaz de consola del sistema de reservas
 * Responsabilidades:
 * - Mostrar menu al usuario
 * - Capturar opciones y delegar al SistemaReservas
 * - Manejar entrada/salida en consola a traves de ConsolaIO
 */
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String MENU = """
        --- SISTEMA DE RESERVAS ---
        1. Ver cabanas
        2. Reservar
        3. Cancelar reserva
        4. Ver reservas activas
        5. Buscar por rango de precio
        6. Ranking de cabanas
        7. Salir
        Seleccione una opcion: 
        """;

    /**
     * Punto de entrada del programa
     * Inicializa el sistema y mantiene el menu activo hasta que el usuario selecciona "Salir"
     */
    public static void main(String[] args) {
        // Inicializar lectura de entrada desde consola
        Scanner sc = new Scanner(System.in);
        ConsolaIO io = new ConsolaIO(sc);
        
        // Crear e inicializa sistema (carga cabañas automaticamente)
        SistemaReservas sistema = new SistemaReservas();

        int opcion;

        do {
            System.out.print(MENU);
            opcion = io.leerEntero("");
            // Maneja la opcion seleccionada por el usuario
            try {
                switch (opcion) {
                    case 1: verCabanas(sistema); break;
                    case 2: registrarReserva(io, sistema); break;
                    case 3: cancelarReserva(io, sistema); break;
                    case 4: verReservasActivas(sistema); break;
                    case 5: buscarPorPrecio(io, sistema); break;
                    case 6: verRanking(sistema); break;
                    case 7: System.out.println("Gracias por usar el sistema"); break;
                    default: System.out.println("Opcion invalida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 7);

        sc.close();
    }

    // === METODOS DE INTERFAZ - Cada metodo corresponde a una opcion del menu
    private static void verCabanas(SistemaReservas sistema) {
        System.out.println("--- CABAÑAS ---");
        List<Cabana> lista = sistema.verCabanas();
        if (lista.isEmpty()) System.out.println("No hay cabanas disponibles");
        else lista.forEach(System.out::println);
    }

    /**
     * Registra una nueva reserva solicitando datos al cliente
     * Flujo: seleccionar cabaña -> ingresar datos cliente -> elegir pago -> crear reserva
     * El usuario puede escribir "cancelar" en cualquier momento para abortar
     */
    private static void registrarReserva(ConsolaIO io, SistemaReservas sistema) {
        System.out.println("--- NUEVA RESERVA ---");
        System.out.println("Escriba 'cancelar' en cualquier campo para abortar.");

        Integer idCabana = io.leerEntero("Id de la cabaña: "); if (idCabana == null) return;
        String nombre = io.leerTexto("Nombre del cliente: "); if (nombre == null) return;
        String telefono = io.leerTelefono("Telefono (10 digitos): "); if (telefono == null) return;
        String correo = io.leerCorreo("Correo: "); if (correo == null) return;
        Integer noches = io.leerNoches("Cuantas noches se va a quedar: "); if (noches == null) return;

        String metodoPago; 
        String ultimos4 = null;

        while (true) {
            String pago = io.leerTexto("Metodo de pago (1 Efectivo / 2 Tarjeta): ");
            if (pago == null) return;

            if (pago.equals("1")) { metodoPago = "Efectivo"; break; }
            if (pago.equals("2")) { metodoPago = "Tarjeta"; ultimos4 = io.leerUltimos4("Ultimos 4 digitos de la tarjeta: "); if (ultimos4 == null) return; break; }

            System.out.println("Opcion invalida");
        }

        Reserva nueva = sistema.reservar(
                idCabana,
                nombre,
                telefono,
                correo,
                noches,
                metodoPago,
                ultimos4
        );

        System.out.println("Reserva registrada con exito");
        System.out.println(nueva);
    }

    /**
     * Cancela una reserva existente por su numero de folio
     */
    private static void cancelarReserva(ConsolaIO io, SistemaReservas sistema) {
        System.out.println("--- CANCELAR RESERVA ---");
        Integer folio = io.leerEntero("Folio de la reserva: ");
        if (folio == null) return;
        
        // Cancelar la reserva y confirmar al usuario
        sistema.cancelar(folio);
        System.out.println("Reserva cancelada con exito");
    }

    private static void verReservasActivas(SistemaReservas sistema) {
        System.out.println("--- RESERVAS ACTIVAS ---");
        List<Reserva> activas = sistema.verReservasActivas();
        if (activas.isEmpty()) System.out.println("No hay reservas activas");
        else activas.forEach(System.out::println);
    }

    /**
     * Busca cabañas dentro de un rango de precios por noche
     */
    private static void buscarPorPrecio(ConsolaIO io, SistemaReservas sistema) {
        System.out.println("--- BUSQUEDA POR PRECIO ---");
        Double min = io.leerDecimal("Precio minimo: "); if (min == null) return;
        
        Double max = io.leerDecimal("Precio maximo: "); if (max == null) return;
        
        // Obtener cabañas dentro del rango y mostrar
        List<Cabana> lista = sistema.buscarPorPrecio(min, max);
        if (lista.isEmpty()) System.out.println("No se encontraron cabanas");
        else for (Cabana c : lista) System.out.println(c);
    }

    private static void verRanking(SistemaReservas sistema) {
        System.out.println("--- RANKING ---");
        List<String> ranking = sistema.ranking();
        if (ranking.isEmpty()) System.out.println("No hay datos");
        else ranking.forEach(System.out::println);
    }
}