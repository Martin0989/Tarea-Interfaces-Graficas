package controlador;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.VentanaPrincipal;
import util.Internacionalizacion; 

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale; 
import java.util.stream.Collectors;

/**
 * Controlador que maneja la lógica de la aplicación
 * Implementa múltiples listeners para diferentes eventos
 */
public class LogicaContactos implements ActionListener, ListSelectionListener, 
                                         ItemListener, KeyListener, MouseListener {
    private VentanaPrincipal delegado;
    private Internacionalizacion i18n; 
    private String nombres, email, telefono, categoria = "";
    private Persona persona;
    private List<Persona> contactos;
    private boolean favorito = false;
    private TableRowSorter<javax.swing.table.DefaultTableModel> sorter;

    // -------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------
    public LogicaContactos(VentanaPrincipal delegado) {
        this.delegado = delegado;
        
        // Inicializar i18n con el idioma por defecto (Español)
        i18n = new Internacionalizacion(new Locale("es", "ES")); 
        
        contactos = new ArrayList<>(); 
        
        inicializarEventos();
        cargarContactosRegistrados();
        configurarOrdenamiento();
        
        // Llamada inicial para aplicar textos al iniciar
        actualizarTextosInterfaz(); 
    }

    // -------------------------------------------------------------
    // EVENTOS Y LISTENERS
    // -------------------------------------------------------------
    
    private void inicializarEventos() {
        // ActionListeners para botones
        delegado.btn_add.addActionListener(this);
        delegado.btn_modificar.addActionListener(this);
        delegado.btn_eliminar.addActionListener(this);
        delegado.btn_exportar.addActionListener(this);
        
        // Listener para el JComboBox de cambio de idioma
        delegado.cmb_idioma.addActionListener(this); 

        // ItemListeners
        delegado.cmb_categoria.addItemListener(this);
        delegado.chb_favorito.addItemListener(this);
        
        // KeyListener para búsqueda
        delegado.txt_buscar.addKeyListener(this);
        
        // Atajos de teclado
        configurarAtajosTeclado();
        
        // ListSelectionListener para la tabla
        delegado.tabla_contactos.getSelectionModel().addListSelectionListener(this);
        
        // Listener para detectar cambio de pestaña y actualizar estadísticas
        delegado.tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Verificar si la pestaña de Estadísticas (índice 1) ha sido seleccionada
                if (delegado.tabbedPane.getSelectedIndex() == 1) {
                    actualizarEstadisticas();
                }
            }
        });
        
        // MouseListener para menú contextual
        delegado.tabla_contactos.addMouseListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Manejar el evento del ComboBox de Idioma
        if (e.getSource() == delegado.cmb_idioma) {
            String seleccion = (String) delegado.cmb_idioma.getSelectedItem();
            i18n = new Internacionalizacion(Internacionalizacion.getLocalePorSeleccion(seleccion));
            actualizarTextosInterfaz();
            return; 
        } 
        
        // Lógica de botones
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
    
    // -------------------------------------------------------------
    // MÉTODOS DE LÓGICA DE NEGOCIO Y ASÍNCRONOS
    // -------------------------------------------------------------
    
    private void actualizarTextosInterfaz() {
        // 1. JFrame Title
        delegado.setTitle(i18n.getTexto("app.titulo"));

        // 2. TABS
        delegado.tabbedPane.setTitleAt(0, i18n.getTexto("tab.contactos"));
        delegado.tabbedPane.setTitleAt(1, i18n.getTexto("tab.estadisticas"));
        
        // 3. Labels del Formulario
        delegado.lbl_nombres.setText(i18n.getTexto("lbl.nombres") + ":");
        delegado.lbl_telefono.setText(i18n.getTexto("lbl.telefono") + ":");
        delegado.lbl_email.setText(i18n.getTexto("lbl.email") + ":");
        delegado.lbl_categoria.setText(i18n.getTexto("lbl.categoria") + ":");
        delegado.lbl_buscar.setText(i18n.getTexto("lbl.buscar") + ":");
        
        // 4. Botones y CheckBox
        delegado.btn_add.setText(i18n.getTexto("btn.agregar"));
        delegado.btn_modificar.setText(i18n.getTexto("btn.modificar"));
        delegado.btn_eliminar.setText(i18n.getTexto("btn.eliminar"));
        delegado.btn_exportar.setText(i18n.getTexto("btn.exportar"));
        delegado.chb_favorito.setText(i18n.getTexto("chb.favorito"));

        // 5. Labels de Estadísticas (Títulos)
        delegado.lbl_tituloTotal.setText(i18n.getTexto("lbl.total"));
        delegado.lbl_tituloFavoritos.setText(i18n.getTexto("lbl.favoritos"));
        delegado.lbl_tituloCategoria.setText(i18n.getTexto("lbl.por_categoria"));
        
        // 6. Encabezados de Tabla (Actualiza el modelo de la tabla)
        String[] colNames = new String[]{
            i18n.getTexto("col.nombre"), 
            i18n.getTexto("col.telefono"), 
            i18n.getTexto("col.email"), 
            i18n.getTexto("col.categoria"), 
            i18n.getTexto("col.favorito")
        };
        delegado.modeloTabla.setColumnIdentifiers(colNames);
        
        // 7. Re-ejecutar estadísticas para traducir los subtítulos de categoría con valores
        actualizarEstadisticas();
    }
    
    private void cargarContactosRegistrados() {
        SwingWorker<List<Persona>, Integer> worker = new SwingWorker<List<Persona>, Integer>() {
            @Override
            protected List<Persona> doInBackground() throws Exception {
                // Preparación de la GUI en el EDT
                SwingUtilities.invokeLater(() -> {
                    delegado.progressBar.setString(i18n.getTexto("progress.cargando")); 
                    delegado.progressBar.setVisible(true);
                    delegado.progressBar.setIndeterminate(true);
                });
                
                try {
                    // Tarea pesada de I/O
                    return new PersonaDAO(new Persona()).leerContactos(); 
                } catch (IOException e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    contactos = get();
                    actualizarTabla();
                    // Solo actualiza las estadísticas si la pestaña está activa
                    if (delegado.tabbedPane.getSelectedIndex() == 1) {
                         actualizarEstadisticas();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(delegado, 
                        i18n.getTexto("error.cargar_contactos") + ": " + e.getMessage(), 
                        i18n.getTexto("error.titulo"), 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Ocultar la barra de progreso
                    delegado.progressBar.setVisible(false);
                    delegado.progressBar.setIndeterminate(false);
                    delegado.progressBar.setString("");
                }
            }
        }; 
        worker.execute();
    }

    private void actualizarTabla() {
        delegado.modeloTabla.setRowCount(0);
        for (Persona p : contactos) {
            // Se asume que contactos.get(0) es el encabezado "NOMBRE"
            if (!p.getNombre().equals("NOMBRE")) { 
                delegado.modeloTabla.addRow(p.toTableRow()); 
            }
        }
    }

    private void actualizarEstadisticas() {
        // Evitar cálculos si la lista está vacía o solo tiene el encabezado
        if (contactos.isEmpty() || (contactos.size() == 1 && contactos.get(0).getNombre().equals("NOMBRE"))) {
             delegado.lbl_totalContactos.setText(i18n.getTexto("lbl.total") + " 0");
             delegado.lbl_favoritos.setText(i18n.getTexto("lbl.favoritos") + " 0");
             delegado.lbl_familia.setText("   • " + i18n.getTexto("cat.familia") + ": 0");
             delegado.lbl_amigos.setText("   • " + i18n.getTexto("cat.amigos") + ": 0");
             delegado.lbl_trabajo.setText("   • " + i18n.getTexto("cat.trabajo") + ": 0");
             return;
        }

        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() throws Exception {
                // Excluir el encabezado (índice 0)
                List<Persona> dataContactos = contactos.subList(1, contactos.size());
                
                int total = dataContactos.size();
                
                long favoritos = dataContactos.stream().filter(Persona::isFavorito).count();
                // Asumo que las categorías están guardadas en español (Familia, Amigos, Trabajo)
                long familia = dataContactos.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase("Familia")).count();
                long amigos = dataContactos.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase("Amigos")).count();
                long trabajo = dataContactos.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase("Trabajo")).count();
                
                return new int[]{total, (int) favoritos, (int) familia, (int) amigos, (int) trabajo};
            }

            @Override
            protected void done() {
                try {
                    int[] results = get();
                    // Actualización de la GUI en el EDT
                    delegado.lbl_totalContactos.setText(i18n.getTexto("lbl.total") + " " + results[0]);
                    delegado.lbl_favoritos.setText(i18n.getTexto("lbl.favoritos") + " " + results[1]);
                    // Se usan las claves de i18n para los textos, los resultados son los números.
                    delegado.lbl_familia.setText("   • " + i18n.getTexto("cat.familia") + ": " + results[2]); 
                    delegado.lbl_amigos.setText("   • " + i18n.getTexto("cat.amigos") + ": " + results[3]); 
                    delegado.lbl_trabajo.setText("   • " + i18n.getTexto("cat.trabajo") + ": " + results[4]); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void inicializacionCampos() {
        nombres = delegado.txt_nombres.getText().trim();
        email = delegado.txt_email.getText().trim();
        telefono = delegado.txt_telefono.getText().trim();
    }

    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_telefono.setText("");
        delegado.txt_email.setText("");
        delegado.chb_favorito.setSelected(false);
        delegado.cmb_categoria.setSelectedIndex(0);
        categoria = (String) delegado.cmb_categoria.getSelectedItem();
        favorito = false;
        delegado.tabla_contactos.clearSelection();
    }
    
    private void agregarContacto() {
        inicializacionCampos();
        if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
             JOptionPane.showMessageDialog(delegado, 
                 i18n.getTexto("error.campos_vacios"), i18n.getTexto("error.advertencia"), 
                 JOptionPane.WARNING_MESSAGE);
             return;
         }

        // 🟢 CORRECCIÓN FINAL: Verificar si el placeholder (índice 0) está seleccionado
        if (delegado.cmb_categoria.getSelectedIndex() == 0) { 
             JOptionPane.showMessageDialog(delegado, 
                 i18n.getTexto("error.seleccionar_categoria"), i18n.getTexto("error.advertencia"), 
                 JOptionPane.WARNING_MESSAGE);
             return;
         }

        persona = new Persona(nombres, telefono, email, categoria, favorito);
        new PersonaDAO(persona).escribirArchivo();
        
        limpiarCampos();
        cargarContactosRegistrados();
        JOptionPane.showMessageDialog(delegado, i18n.getTexto("exito.contacto_agregado"));
    }

    private void modificarContacto() {
        int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
        if (filaSeleccionada == -1) {
             JOptionPane.showMessageDialog(delegado, 
                 i18n.getTexto("error.seleccionar_modificar"));
             return;
         }

        inicializacionCampos();
        if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
             JOptionPane.showMessageDialog(delegado, i18n.getTexto("error.campos_vacios"));
             return;
         }

        // Mapear índice de tabla (vista) a índice de lista (modelo)
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
            JOptionPane.showMessageDialog(delegado, i18n.getTexto("exito.contacto_modificado"));
            limpiarCampos();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(delegado, i18n.getTexto("error.modificar") + ": " + e.getMessage());
        }
    }

    private void eliminarContacto() {
        int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
        if (filaSeleccionada == -1) {
             JOptionPane.showMessageDialog(delegado, 
                 i18n.getTexto("error.seleccionar_eliminar"));
             return;
         }

        int confirm = JOptionPane.showConfirmDialog(delegado, 
             i18n.getTexto("confirm.eliminar_mensaje"), 
             i18n.getTexto("confirm.eliminar_titulo"), 
             JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Mapear índice de tabla (vista) a índice de lista (modelo)
            int realIndex = delegado.tabla_contactos.convertRowIndexToModel(filaSeleccionada) + 1;
            contactos.remove(realIndex);

            try {
                new PersonaDAO(new Persona()).actualizarContactos(contactos);
                cargarContactosRegistrados();
                JOptionPane.showMessageDialog(delegado, i18n.getTexto("exito.contacto_eliminado"));
                limpiarCampos();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(delegado, i18n.getTexto("error.eliminar") + ": " + e.getMessage());
            }
        }
    }

    private void exportarContactos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(i18n.getTexto("exportar.titulo")); 
        fileChooser.setSelectedFile(new java.io.File("contactos.csv"));

        int userSelection = fileChooser.showSaveDialog(delegado);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                // Excluir el encabezado (índice 0)
                List<Persona> contactosExportar = contactos.size() > 0 ? 
                    contactos.subList(1, contactos.size()) : new ArrayList<>();
                
                PersonaDAO.exportarCSV(contactosExportar, 
                    fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(delegado, 
                    i18n.getTexto("exito.contactos_exportados"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(delegado, 
                    i18n.getTexto("error.exportar") + ": " + e.getMessage());
            }
        }
    }
    
    private void configurarOrdenamiento() {
        sorter = new TableRowSorter<>(delegado.modeloTabla);
        delegado.tabla_contactos.setRowSorter(sorter);
    }
    
    private void filtrarTabla(String texto) {
        if (texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Filtrado que ignora mayúsculas/minúsculas (?i)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void cargarContactoEnFormulario(int fila) {
        // Mapear índice de tabla (vista) a índice de lista (modelo)
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
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == delegado.tabla_contactos.getSelectionModel() && !e.getValueIsAdjusting()) {
            int filaSeleccionada = delegado.tabla_contactos.getSelectedRow();
            if (filaSeleccionada != -1) {
                cargarContactoEnFormulario(filaSeleccionada);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == delegado.cmb_categoria) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Actualiza la variable de instancia para agregar/modificar
                categoria = delegado.cmb_categoria.getSelectedItem().toString();
            }
        } else if (e.getSource() == delegado.chb_favorito) {
            // Actualiza la variable de instancia
            favorito = delegado.chb_favorito.isSelected();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == delegado.txt_buscar) {
            filtrarTabla(delegado.txt_buscar.getText());
        }
    }
    
    @Override public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mostrarMenuContextual(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mostrarMenuContextual(e);
    }
    
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    
    private void configurarAtajosTeclado() {
         delegado.btn_add.setMnemonic(KeyEvent.VK_N);
         delegado.getRootPane().registerKeyboardAction(
             e -> limpiarCampos(),
             KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
         delegado.getRootPane().registerKeyboardAction(
             e -> eliminarContacto(),
             KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
         delegado.getRootPane().registerKeyboardAction(
             e -> agregarContacto(),
             KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
             JComponent.WHEN_IN_FOCUSED_WINDOW
         );
    }

    private void mostrarMenuContextual(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int fila = delegado.tabla_contactos.rowAtPoint(e.getPoint());
            if (fila >= 0) {
                delegado.tabla_contactos.setRowSelectionInterval(fila, fila);
                JPopupMenu menuContextual = new JPopupMenu();
                
                // Usar i18n para los textos del menú
                JMenuItem itemEditar = new JMenuItem(i18n.getTexto("menu.editar"));
                itemEditar.addActionListener(ev -> modificarContacto());
                
                JMenuItem itemEliminar = new JMenuItem(i18n.getTexto("menu.eliminar"));
                itemEliminar.addActionListener(ev -> eliminarContacto());
                
                JMenuItem itemLimpiar = new JMenuItem(i18n.getTexto("menu.limpiar"));
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