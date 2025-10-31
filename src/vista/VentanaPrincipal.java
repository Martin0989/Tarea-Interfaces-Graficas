package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal con JTabbedPane, utilizando Layout Managers para un diseño óptimo.
 */
public class VentanaPrincipal extends JFrame {
    // Pestaña de Contactos
    public JPanel panelContactos;
    public JTextField txt_nombres;
    public JTextField txt_telefono;
    public JTextField txt_email;
    public JTextField txt_buscar;
    public JCheckBox chb_favorito;
    public JComboBox<String> cmb_categoria;
    public JButton btn_add;
    public JButton btn_modificar;
    public JButton btn_eliminar;
    public JButton btn_exportar;
    public JTable tabla_contactos;
    public DefaultTableModel modeloTabla; // 👈 Referencia al modelo de la tabla
    public JScrollPane scrollTabla;
    public JProgressBar progressBar;
    public JTabbedPane tabbedPane; // 👈 Necesario para agregar el ListSelectionListener
    
    // Pestaña de Estadísticas
    public JPanel panelEstadisticas;
    public JLabel lbl_totalContactos;
    public JLabel lbl_favoritos;
    public JLabel lbl_familia;
    public JLabel lbl_amigos;
    public JLabel lbl_trabajo;
    
    // Componente para I18N
    public JComboBox<String> cmb_idioma; 

    // JLabels del Formulario y Búsqueda (para poder ser traducidos)
    public JLabel lbl_nombres, lbl_telefono, lbl_email, lbl_categoria, lbl_buscar;

    // Etiquetas de Estadísticas para traducción
    public JLabel lbl_tituloTotal, lbl_tituloFavoritos, lbl_tituloCategoria;

    public VentanaPrincipal() {
        // La configuración inicial se hace en el constructor
        setTitle("GESTIÓN DE CONTACTOS - OPTIMIZADA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 600); // Tamaño inicial que permite redimensionamiento
        
        // 1. Configurar BorderLayout en el ContentPane
        this.getContentPane().setLayout(new BorderLayout()); 
        
        // 2. Inicializar componentes
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        
        // Configuración del JComboBox de idioma (Se coloca en el Norte del JFrame)
        cmb_idioma = new JComboBox<>(new String[]{"Español", "English", "Português"});
        cmb_idioma.setSelectedIndex(0); 

        // Panel para el JComboBox de idioma (FlowLayout a la derecha)
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelHeader.add(cmb_idioma);
        
        this.getContentPane().add(panelHeader, BorderLayout.NORTH);
        
        // JTabbedPane principal (Se coloca en el Centro del JFrame)
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER); 
        
        // Creación de pestañas
        crearPestanaContactos();
        crearPestanaEstadisticas();
    }
    
    // -------------------------------------------------------------
    // MÉTODOS DE CREACIÓN DE PESTAÑAS (Implementando Layouts)
    // -------------------------------------------------------------

    private void crearPestanaContactos() {
        // Panel principal de contactos con BorderLayout (Formulario en NORTH, Tabla en CENTER)
        panelContactos = new JPanel(new BorderLayout(10, 10)); 
        panelContactos.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margen exterior

        // ********************************
        // 1. Panel de Formulario (Usando GridBagLayout)
        // ********************************
        
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Hace que los campos de texto llenen el espacio
        
        // Inicializar JLabels (se traducirán en el controlador)
        lbl_nombres = new JLabel("Nombres:"); 
        lbl_telefono = new JLabel("Teléfono:");
        lbl_email = new JLabel("Email:");
        lbl_categoria = new JLabel("Categoría:");
        lbl_buscar = new JLabel("Buscar Contacto:");
        
        // --- FILA 1: Nombres ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        panelFormulario.add(lbl_nombres, gbc);
        txt_nombres = new JTextField(25); 
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txt_nombres, gbc);
        
        // --- FILA 2: Teléfono ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; 
        panelFormulario.add(lbl_telefono, gbc);
        txt_telefono = new JTextField(25);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txt_telefono, gbc);

        // --- FILA 3: Email ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; 
        panelFormulario.add(lbl_email, gbc);
        txt_email = new JTextField(25);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(txt_email, gbc);

        // --- FILA 4: Categoría y Favorito ---
        cmb_categoria = new JComboBox<>(new String[]{"Familia", "Amigos", "Trabajo"});
        chb_favorito = new JCheckBox("Marcar como Favorito"); 
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST; 
        panelFormulario.add(lbl_categoria, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        // Panel interno para agrupar ComboBox y Checkbox
        JPanel panelComboCheck = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelComboCheck.add(cmb_categoria);
        panelComboCheck.add(chb_favorito);
        panelFormulario.add(panelComboCheck, gbc);

        // --- PANEL DE BOTONES (FlowLayout) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btn_add = new JButton("Agregar"); 
        btn_modificar = new JButton("Modificar");
        btn_eliminar = new JButton("Eliminar");
        btn_exportar = new JButton("Exportar a CSV");
        
        panelBotones.add(btn_add);
        panelBotones.add(btn_modificar);
        panelBotones.add(btn_eliminar);
        panelBotones.add(btn_exportar);

        // Agregar botones en dos columnas, en la fila 5
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelFormulario.add(panelBotones, gbc);
        
        // 2. Panel de Búsqueda (Se coloca bajo el formulario)
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 0));
        txt_buscar = new JTextField(40); 
        panelBusqueda.add(lbl_buscar, BorderLayout.WEST);
        panelBusqueda.add(txt_buscar, BorderLayout.CENTER);
        
        // 3. Unir Formulario y Búsqueda en el Norte
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH); // Formulario arriba

        // Inicializar JProgressBar antes de agregarlo
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false); // Mantener oculto inicialmente

        panelNorte.add(progressBar, BorderLayout.CENTER); // Barra de progreso entre formulario y tabla
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH); // Búsqueda antes de la tabla

        // ********************************
        // 4. Configurar Tabla (Centro) - CORRECCIÓN CLAVE
        // ********************************
        
        modeloTabla = new DefaultTableModel(new Object[]{"Nombre", "Teléfono", "Email", "Categoría", "Favorito"}, 0) {
            
            // 🔴 SOLUCIÓN 1: Evita ClassCastException, la columna 4 es Boolean.class
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // La columna Favorito (índice 4) es de tipo Boolean.
                if (columnIndex == 4) {
                    return Boolean.class;
                }
                // El resto de columnas son de tipo String.
                return String.class;
            }

            // 🟢 SOLUCIÓN 2: Controla qué celdas son editables. Solo el checkbox debería serlo.
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna 4 (Favorito) debe ser editable para el checkbox
                return column == 4;
            }
        };
        tabla_contactos = new JTable(modeloTabla);
        tabla_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollTabla = new JScrollPane(tabla_contactos);
        
        // Añadir elementos al panel de contactos
        panelContactos.add(panelNorte, BorderLayout.NORTH);
        panelContactos.add(scrollTabla, BorderLayout.CENTER);
        
        tabbedPane.addTab("Contactos", panelContactos);
    }

    private void crearPestanaEstadisticas() {
        panelEstadisticas = new JPanel(new GridBagLayout()); // Usar GridBagLayout
        panelEstadisticas.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbcStats = new GridBagConstraints();
        gbcStats.insets = new Insets(10, 10, 10, 10);
        gbcStats.anchor = GridBagConstraints.WEST;
        gbcStats.fill = GridBagConstraints.HORIZONTAL;
        gbcStats.weightx = 1.0; 

        // Inicializar Labels de Título (para traducción)
        lbl_tituloTotal = new JLabel("Contactos Totales:");
        lbl_tituloFavoritos = new JLabel("Contactos Favoritos:");
        lbl_tituloCategoria = new JLabel("Por Categoría:");
        
        // Inicializar Labels de Datos 
        lbl_totalContactos = new JLabel("0");
        lbl_favoritos = new JLabel("0");
        lbl_familia = new JLabel("0"); 
        lbl_amigos = new JLabel("0"); 
        lbl_trabajo = new JLabel("0"); 
        
        // Estilos
        lbl_tituloTotal.setFont(new Font("Tahoma", Font.BOLD, 20));
        lbl_totalContactos.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lbl_tituloFavoritos.setFont(new Font("Tahoma", Font.BOLD, 18));
        lbl_favoritos.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lbl_tituloCategoria.setFont(new Font("Tahoma", Font.BOLD, 18));

        // Posicionamiento de Estadísticas
        gbcStats.gridx = 0; gbcStats.gridy = 0; gbcStats.gridwidth = 1; gbcStats.weightx = 0;
        panelEstadisticas.add(lbl_tituloTotal, gbcStats);
        gbcStats.gridx = 1; gbcStats.gridy = 0; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_totalContactos, gbcStats);

        gbcStats.gridx = 0; gbcStats.gridy = 1; gbcStats.gridwidth = 1; gbcStats.weightx = 0;
        panelEstadisticas.add(lbl_tituloFavoritos, gbcStats);
        gbcStats.gridx = 1; gbcStats.gridy = 1; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_favoritos, gbcStats);

        // Separador visual
        gbcStats.gridx = 0; gbcStats.gridy = 2; gbcStats.gridwidth = 2; gbcStats.weightx = 1.0;
        panelEstadisticas.add(new JSeparator(), gbcStats);
        
        gbcStats.gridx = 0; gbcStats.gridy = 3; gbcStats.gridwidth = 2; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_tituloCategoria, gbcStats);

        // Estadísticas por categoría
        gbcStats.gridx = 0; gbcStats.gridy = 4; gbcStats.gridwidth = 1; gbcStats.weightx = 0;
        panelEstadisticas.add(new JLabel("   • Familia:"), gbcStats); 
        gbcStats.gridx = 1; gbcStats.gridy = 4; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_familia, gbcStats);

        gbcStats.gridx = 0; gbcStats.gridy = 5; gbcStats.gridwidth = 1; gbcStats.weightx = 0;
        panelEstadisticas.add(new JLabel("   • Amigos:"), gbcStats);
        gbcStats.gridx = 1; gbcStats.gridy = 5; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_amigos, gbcStats);

        gbcStats.gridx = 0; gbcStats.gridy = 6; gbcStats.gridwidth = 1; gbcStats.weightx = 0;
        panelEstadisticas.add(new JLabel("   • Trabajo:"), gbcStats);
        gbcStats.gridx = 1; gbcStats.gridy = 6; gbcStats.weightx = 1.0;
        panelEstadisticas.add(lbl_trabajo, gbcStats);

        tabbedPane.addTab("Estadísticas", panelEstadisticas);
    }
}