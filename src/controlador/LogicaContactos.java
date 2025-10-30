package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.VentanaPrincipal;

/**
 * Controlador que maneja la lógica de la aplicación
 * Implementa múltiples listeners para diferentes eventos
 */
public class LogicaContactos implements ActionListener, ListSelectionListener, 
                                         ItemListener, KeyListener, MouseListener {
    private VentanaPrincipal delegado;
    private String nombres, email, telefono, categoria = "";
    private Persona persona;
    private List<Persona> contactos;
    private boolean favorito = false;
    private TableRowSorter<javax.swing.table.DefaultTableModel> sorter;

    public LogicaContactos(VentanaPrincipal delegado) {
        this.delegado = delegado;
        inicializarEventos();
        cargarContactosRegistrados();
        configurarOrdenamiento();
    }

    /**
     * Configura todos los listeners de eventos
     */
    private void inicializarEventos() {
        // ActionListeners para botones
        delegado.btn_add.addActionListener(this);
        delegado.btn_modificar.addActionListener(this);
        delegado.btn_eliminar.addActionListener(this);
        delegado.btn_exportar.addActionListener(this);

        // ItemListeners
        delegado.cmb_categoria.addItemListener(this);
        delegado.chb_favorito.addItemListener(this);

        // KeyListener para búsqueda
        delegado.txt_buscar.addKeyListener(this);

        // Atajos de teclado
        configurarAtajosTeclado();

        // ListSelectionListener para la tabla
        delegado.tabla_contactos.getSelectionModel().addListSelectionListener(this);

        // MouseListener para menú contextual
        delegado.tabla_contactos.addMouseListener(this);
    }

    /**
     * Configura atajos de teclado (Ctrl+N, Ctrl+E, Ctrl+S)
     */
    private void configurarAtajosTeclado() {
        // Ctrl+N para nuevo contacto
        delegado.btn_add.setMnemonic(KeyEvent.VK_N);
        delegado.getRootPane().registerKeyboardAction(
            e -> limpiarCampos(),
            KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Ctrl+E para eliminar
        delegado.getRootPane().registerKeyboardAction(
            e -> eliminarContacto(),
            KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Ctrl+S para guardar/agregar
        delegado.getRootPane().registerKeyboardAction(
            e -> agregarContacto(),
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /**
     * Configura el ordenamiento de la tabla
     */
    private void configurarOrdenamiento() {
        sorter = new TableRowSorter<>(delegado.modeloTabla);
        delegado.tabla_contactos.setRowSorter(sorter);
    }

    /**
     * Inicializa los campos del formulario
     */
    private void inicializacionCampos() {
        nombres = delegado.txt_nombres.getText().trim();
        email = delegado.txt_email.getText().trim();
        telefono = delegado.txt_telefono.getText().trim();
    }

    /**
     * Carga contactos desde el archivo con barra de progreso
     */
    private void cargarContactosRegistrados() {
        SwingWorker<List<Persona>, Integer> worker = new SwingWorker<List<Persona>, Integer>() {
            @Override
            protected List<Persona> doInBackground() throws Exception {
                delegado.progressBar.setVisible(true);
                delegado.progressBar.setIndeterminate(true);
                delegado.progressBar.setString("Cargando contactos...");
                
                try {
                    contactos = new PersonaDAO(new Persona()).leerArchivo();
                } catch (IOException e) {
                    contactos = new ArrayList<>();
                }
                
                return contactos;
            }

            @Override
            protected void done() {
                try {
                    contactos = get();
                    actualizarTabla();
                    actualizarEstadisticas();
                    delegado.progressBar.setVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(delegado, 
                        "Error al cargar contactos: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Actualiza la tabla con los contactos
     */
    private void actualizarTabla() {
        delegado.modeloTabla.setRowCount(0);
        for (Persona p : contactos) {
            if (!p.getNombre().equals("NOMBRE")) { // Omitir encabezado
                delegado.modeloTabla.addRow(p.toTableRow());
            }
        }
    }

    /**
     * Actualiza las estadísticas en la pestaña correspondiente
     */
    private void actualizarEstadisticas() {
        int total = contactos.size() - 1; // -1 por el encabezado
        int favoritos = 0, familia = 0, amigos = 0, trabajo = 0;

        for (Persona p : contactos) {
            if (p.getNombre().equals("NOMBRE")) continue;
            
            if (p.isFavorito()) favoritos++;
            
            switch (p.getCategoria()) {
                case "Familia": familia++; break;
                case "Amigos": amigos++; break;
                case "Trabajo": trabajo++; break;
            }
        }

        delegado.lbl_totalContactos.setText("Total de Contactos: " + Math.max(0, total));
        delegado.lbl_favoritos.setText("Contactos Favoritos: " + favoritos);
        delegado.lbl_familia.setText("   • Familia: " + familia);
        delegado.lbl_amigos.setText("   • Amigos: " + amigos);
        delegado.lbl_trabajo.setText("   • Trabajo: " + trabajo);
    }

    /**
     * Limpia los campos del formulario
     */
    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_telefono.setText("");
        delegado.txt_email.setText("");
        delegado.chb_favorito.setSelected(false);
        delegado.cmb_categoria.setSelectedIndex(0);
        categoria = "";
        favorito = false;
        delegado.tabla_contactos.clearSelection();
    }

    /**
     * Agrega un nuevo contacto
     */
    private void agregarContacto() {
        inicializacionCampos();
        
        if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(delegado, 
                "Todos los campos deben estar llenos", "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (categoria.isEmpty() || categoria.equals("Elija una Categoria")) {
            JOptionPane.showMessageDialog(delegado, 
                "Debe seleccionar una categoría", "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        persona = new Persona(nombres, telefono, email, categoria, favorito);
        new PersonaDAO(persona).escribirArchivo();
        
        limpiarCampos();
        cargarContactosRegistrados();
        JOptionPane.showMessageDialog(delegado, "Contacto registrado exitosamente");
    }

    /**
     * Modifica un contacto existente
     */
    private void modificarContacto() {
        int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(delegado, 
                "Debe seleccionar un contacto para modificar");
            return;
        }

        inicializacionCampos();
        
        if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(delegado, "Todos los campos deben estar llenos");
            return;
        }

        int realIndex = delegado.tabla_contactos.convertRowIndexToModel(filaSeleccionada) + 1;
        
        Persona p = contactos.get(realIndex);
        p.setNombre(nombres);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setCategoria(categoria);
        p.setFavorito(favorito);

        try {
            new PersonaDAO(new Persona()).actualizarContactos(contactos);
            cargarContactosRegistrados();
            JOptionPane.showMessageDialog(delegado, "Contacto modificado exitosamente");
            limpiarCampos();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(delegado, "Error al modificar: " + e.getMessage());
        }
    }

    /**
     * Elimina un contacto
     */
    private void eliminarContacto() {
        int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(delegado, 
                "Debe seleccionar un contacto para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(delegado, 
            "¿Está seguro de eliminar este contacto?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int realIndex = delegado.tabla_contactos.convertRowIndexToModel(filaSeleccionada) + 1;
            contactos.remove(realIndex);

            try {
                new PersonaDAO(new Persona()).actualizarContactos(contactos);
                cargarContactosRegistrados();
                JOptionPane.showMessageDialog(delegado, "Contacto eliminado exitosamente");
                limpiarCampos();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(delegado, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    /**
     * Exporta contactos a CSV
     */
    private void exportarContactos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar contactos como CSV");
        fileChooser.setSelectedFile(new java.io.File("contactos.csv"));

        int userSelection = fileChooser.showSaveDialog(delegado);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                List<Persona> contactosExportar = new ArrayList<>(contactos);
                contactosExportar.remove(0); // Eliminar encabezado
                
                PersonaDAO.exportarCSV(contactosExportar, 
                    fileChooser.getSelectedFile().getAbsolutePath());
                
                JOptionPane.showMessageDialog(delegado, 
                    "Contactos exportados exitosamente");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(delegado, 
                    "Error al exportar: " + e.getMessage());
            }
        }
    }

    /**
     * Filtra la tabla según el texto de búsqueda
     */
    private void filtrarTabla(String texto) {
        if (texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    // ==================== IMPLEMENTACIÓN DE LISTENERS ====================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delegado.btn_add) {
            agregarContacto();
        } else if (e.getSource() == delegado.btn_modificar) {
            modificarContacto();
        } else if (e.getSource() == delegado.btn_eliminar) {
            eliminarContacto();
        } else if (e.getSource() == delegado.btn_exportar) {
            exportarContactos();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
            if (filaSeleccionada != -1) {
                cargarContactoEnFormulario(filaSeleccionada);
            }
        }
    }

    /**
     * Carga los datos del contacto seleccionado en el formulario
     */
    private void cargarContactoEnFormulario(int fila) {
        int realIndex = delegado.tabla_contactos.convertRowIndexToModel(fila) + 1;
        
        if (realIndex < contactos.size()) {
            Persona p = contactos.get(realIndex);
            delegado.txt_nombres.setText(p.getNombre());
            delegado.txt_telefono.setText(p.getTelefono());
            delegado.txt_email.setText(p.getEmail());
            delegado.chb_favorito.setSelected(p.isFavorito());
            delegado.cmb_categoria.setSelectedItem(p.getCategoria());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == delegado.cmb_categoria) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                categoria = delegado.cmb_categoria.getSelectedItem().toString();
            }
        } else if (e.getSource() == delegado.chb_favorito) {
            favorito = delegado.chb_favorito.isSelected();
        }
    }

    // ==================== EVENTOS DE TECLADO ====================

    @Override
    public void keyTyped(KeyEvent e) {
        // No se requiere implementación
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // No se requiere implementación
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == delegado.txt_buscar) {
            filtrarTabla(delegado.txt_buscar.getText());
        }
    }

    // ==================== EVENTOS DE MOUSE ====================

    @Override
    public void mouseClicked(MouseEvent e) {
        // No se requiere implementación específica
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mostrarMenuContextual(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mostrarMenuContextual(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No se requiere implementación
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No se requiere implementación
    }

    /**
     * Muestra menú contextual con clic derecho
     */
    private void mostrarMenuContextual(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int fila = delegado.tabla_contactos.rowAtPoint(e.getPoint());
            if (fila >= 0) {
                delegado.tabla_contactos.setRowSelectionInterval(fila, fila);
                
                JPopupMenu menuContextual = new JPopupMenu();
                
                JMenuItem itemEditar = new JMenuItem("Editar");
                itemEditar.addActionListener(ev -> modificarContacto());
                
                JMenuItem itemEliminar = new JMenuItem("Eliminar");
                itemEliminar.addActionListener(ev -> eliminarContacto());
                
                JMenuItem itemLimpiar = new JMenuItem("Limpiar formulario");
                itemLimpiar.addActionListener(ev -> limpiarCampos());
                
                menuContextual.add(itemEditar);
                menuContextual.add(itemEliminar);
                menuContextual.addSeparator();
                menuContextual.add(itemLimpiar);
                
                menuContextual.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}