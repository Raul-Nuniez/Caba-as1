// domain/Reserva.java
/**
 * MODELO - Representa una reserva realizada por un cliente
 * Almacena: datos del cliente, cabaña reservada, fechas, pago y estado (activa/cancelada)
 */
public class Reserva {

    // Identificador único de la reserva
    private int folio;
    // Referencia a la cabaña reservada
    private Cabana cabana;
    // Datos del cliente que realizó la reserva
    private String nombreCliente;
    private String telefono;
    private String correo;
    // Duración de la reserva
    private int noches;
    // Información de pago
    private String metodoPago;
    private String ultimos4; // Últimos 4 dígitos si paga con tarjeta
    // Estado: true = activa, false = cancelada
    private boolean activa;

    public Reserva(int folio, Cabana cabana, String nombreCliente, String telefono, String correo, 
                    int noches, String metodoPago, String ultimos4) {
        this.folio = folio;
        this.cabana = cabana;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.correo = correo;
        this.noches = noches;
        this.metodoPago = metodoPago;
        this.ultimos4 = ultimos4;
        this.activa = true;
    }

    public int getFolio() { return folio; }
    public Cabana getCabana() { return cabana; }
    public boolean estaActiva() { return activa; }

    /**
     * Calcula el total a pagar: cantidad de noches × precio por noche
     */
    public double getTotal() {
        return noches * cabana.getPrecioPorNoche();
    }
    
    /**
     * Marca la reserva como cancelada.
     * Lanza excepción si ya estaba cancelada (evita cancelar dos veces)
     */
    public void cancelar() {
        if (!activa) throw new IllegalStateException("La reserva ya esta cancelada");
        activa = false;
    }

    /**
     * Formato para mostrar detalle completo de la reserva
     */
    @Override
    public String toString() {
        // Enmascarar número de tarjeta por seguridad
        String pago = metodoPago;
        if ("Tarjeta".equals(metodoPago)) pago += " ****" + ultimos4;

        return 
        "Folio: " + folio + "\n" +
        "Cliente: " + nombreCliente + "\n" +
        "Correo: " + correo + "\n" +
        "Telefono: " + telefono + "\n" +
        "Cabana: " + cabana.getNombre() + "\n" +
        "Noches: " + noches + "\n" +
        "Pago: " + pago + "\n" +
        "Total: $" + getTotal() + "\n" +
        "Estado: " + (activa ? "Activa" : "Cancelada");
    }
}
