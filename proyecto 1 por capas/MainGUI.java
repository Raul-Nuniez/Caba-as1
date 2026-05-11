import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaReservas sistema = new SistemaReservas();
            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.setVisible(true);
        });
    }

    private static class VentanaPrincipal extends JFrame {
        private final SistemaReservas sistema;
        private final ReservasTableModel reservasModel;
        private final CabanasTableModel cabanasModel;
        private final JTable tablaReservas;

        VentanaPrincipal(SistemaReservas sistema) {
            this.sistema = sistema;
            this.reservasModel = new ReservasTableModel();
            this.cabanasModel = new CabanasTableModel();
            this.tablaReservas = new JTable(reservasModel);

            setTitle("Sistema de Reservas - GUI");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 500);
            setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Reservas", crearTabReservas());
            tabs.addTab("Cabañas", crearTabCabanas());
            setContentPane(tabs);

            refrescarReservas();
            refrescarCabanas();
        }

        private JPanel crearTabReservas() {
            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

            JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnNueva = new JButton(new AbstractAction("Nueva reserva") {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    abrirDialogoNuevaReserva();
                }
            });

            JButton btnCancelar = new JButton(new AbstractAction("Cancelar reserva") {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cancelarReservaSeleccionada();
                }
            });

            JButton btnActualizar = new JButton(new AbstractAction("Actualizar") {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    refrescarReservas();
                }
            });

            botones.add(btnNueva);
            botones.add(btnCancelar);
            botones.add(btnActualizar);
            panel.add(botones, BorderLayout.SOUTH);
            return panel;
        }

        private JPanel crearTabCabanas() {
            JPanel panel = new JPanel(new BorderLayout(8, 8));
            panel.add(new JScrollPane(new JTable(cabanasModel)), BorderLayout.CENTER);
            return panel;
        }

        private void abrirDialogoNuevaReserva() {
            NuevaReservaDialog dialog = new NuevaReservaDialog(this, sistema, this::refrescarReservas);
            dialog.setVisible(true);
        }

        private void cancelarReservaSeleccionada() {
            int fila = tablaReservas.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una reserva para cancelar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Reserva reserva = reservasModel.getReservaAt(fila);
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Cancelar la reserva folio " + reserva.getFolio() + "?",
                    "Confirmar cancelación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                sistema.cancelar(reserva.getFolio());
                JOptionPane.showMessageDialog(this, "Reserva cancelada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarReservas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refrescarReservas() {
            reservasModel.setData(sistema.verReservasActivas());
        }

        private void refrescarCabanas() {
            cabanasModel.setData(sistema.verCabanas());
        }
    }

    private static class ReservasTableModel extends AbstractTableModel {
        private final String[] columnas = {"Folio", "Cabaña", "Total", "Estado"};
        private List<Reserva> data = new ArrayList<>();

        void setData(List<Reserva> reservas) {
            this.data = new ArrayList<>(reservas);
            fireTableDataChanged();
        }

        Reserva getReservaAt(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnas[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Reserva r = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getFolio();
                case 1 -> r.getCabana().getNombre();
                case 2 -> String.format("$%.2f", r.getTotal());
                case 3 -> r.estaActiva() ? "Activa" : "Cancelada";
                default -> "";
            };
        }
    }

    private static class CabanasTableModel extends AbstractTableModel {
        private final String[] columnas = {"ID", "Nombre", "Precio por noche"};
        private List<Cabana> data = new ArrayList<>();

        void setData(List<Cabana> cabanas) {
            this.data = new ArrayList<>(cabanas);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnas[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Cabana c = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> c.getId();
                case 1 -> c.getNombre();
                case 2 -> String.format("$%.2f", c.getPrecioPorNoche());
                default -> "";
            };
        }
    }

    private static class NuevaReservaDialog extends JDialog {
        private final SistemaReservas sistema;
        private final Runnable onSuccess;

        private final JComboBox<Cabana> comboCabana;
        private final JTextField txtNombre = new JTextField(20);
        private final JTextField txtTelefono = new JTextField(20);
        private final JTextField txtCorreo = new JTextField(20);
        private final JTextField txtNoches = new JTextField(20);
        private final JRadioButton rbEfectivo = new JRadioButton("Efectivo", true);
        private final JRadioButton rbTarjeta = new JRadioButton("Tarjeta");
        private final JTextField txtUltimos4 = new JTextField(8);

        NuevaReservaDialog(Frame owner, SistemaReservas sistema, Runnable onSuccess) {
            super(owner, "Nueva reserva", true);
            this.sistema = sistema;
            this.onSuccess = onSuccess;

            comboCabana = new JComboBox<>();
            comboCabana.setModel(new DefaultComboBoxModel<>(sistema.verCabanas().toArray(new Cabana[0])));

            setLayout(new BorderLayout(8, 8));
            add(crearFormulario(), BorderLayout.CENTER);
            add(crearBotones(), BorderLayout.SOUTH);

            actualizarCampoTarjeta();
            rbEfectivo.addActionListener(e -> actualizarCampoTarjeta());
            rbTarjeta.addActionListener(e -> actualizarCampoTarjeta());

            pack();
            setLocationRelativeTo(owner);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    txtNombre.requestFocusInWindow();
                }
            });
        }

        private JPanel crearFormulario() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int fila = 0;
            agregarFila(panel, gbc, fila++, "Cabaña:", comboCabana);
            agregarFila(panel, gbc, fila++, "Nombre:", txtNombre);
            agregarFila(panel, gbc, fila++, "Teléfono:", txtTelefono);
            agregarFila(panel, gbc, fila++, "Correo:", txtCorreo);
            agregarFila(panel, gbc, fila++, "Noches:", txtNoches);

            JPanel pagos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            ButtonGroup group = new ButtonGroup();
            group.add(rbEfectivo);
            group.add(rbTarjeta);
            pagos.add(rbEfectivo);
            pagos.add(rbTarjeta);
            agregarFila(panel, gbc, fila++, "Método de pago:", pagos);
            agregarFila(panel, gbc, fila, "Últimos 4:", txtUltimos4);

            return panel;
        }

        private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, java.awt.Component campo) {
            gbc.gridx = 0;
            gbc.gridy = fila;
            gbc.weightx = 0;
            panel.add(new JLabel(etiqueta), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(campo, gbc);
        }

        private JPanel crearBotones() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnGuardar = new JButton("Guardar");
            JButton btnCancelar = new JButton("Cancelar");

            btnGuardar.addActionListener(e -> guardarReserva());
            btnCancelar.addActionListener(e -> dispose());

            panel.add(btnGuardar);
            panel.add(btnCancelar);
            return panel;
        }

        private void actualizarCampoTarjeta() {
            boolean tarjeta = rbTarjeta.isSelected();
            txtUltimos4.setEnabled(tarjeta);
            if (!tarjeta) {
                txtUltimos4.setText("");
            }
        }

        private void guardarReserva() {
            try {
                Cabana cabana = (Cabana) comboCabana.getSelectedItem();
                if (cabana == null) {
                    throw new IllegalArgumentException("Debe seleccionar una cabaña.");
                }

                String metodoPago = rbTarjeta.isSelected() ? "Tarjeta" : "Efectivo";
                String ultimos4 = rbTarjeta.isSelected() ? txtUltimos4.getText().trim() : null;

                sistema.reservar(
                        cabana.getId(),
                        txtNombre.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtCorreo.getText().trim(),
                        Integer.parseInt(txtNoches.getText().trim()),
                        metodoPago,
                        ultimos4
                );

                JOptionPane.showMessageDialog(this, "Reserva registrada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                onSuccess.run();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Noches debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
