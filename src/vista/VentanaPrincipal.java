package vista;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal con JTabbedPane para organizar las pestañas
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
    public DefaultTableModel modeloTabla;
    public JScrollPane scrollTabla;
    public JProgressBar progressBar;
    public JTabbedPane tabbedPane;
    
    // Pestaña de Estadísticas
    public JPanel panelEstadisticas;
    public JLabel lbl_totalContactos;
    public JLabel lbl_favoritos;
    public JLabel lbl_familia;
    public JLabel lbl_amigos;
    public JLabel lbl_trabajo;

    public VentanaPrincipal() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("GESTIÓN DE CONTACTOS - OPTIMIZADA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 1100, 800);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // JTabbedPane principal
        tabbedPane = new JTabbedPane();
        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    private void inicializarComponentes() {
        crearPestanaContactos();
        crearPestanaEstadisticas();
    }

    private void crearPestanaContactos() {
        panelContactos = new JPanel();
        panelContactos.setLayout(null);
        tabbedPane.addTab("Contactos", null, panelContactos, "Gestión de contactos");

        // Etiquetas
        JLabel lbl_nombre = new JLabel("NOMBRES:");
        lbl_nombre.setFont(new Font("Tahoma", Font.BOLD, 14));
        lbl_nombre.setBounds(25, 20, 100, 20);
        panelContactos.add(lbl_nombre);

        JLabel lbl_telefono = new JLabel("TELÉFONO:");
        lbl_telefono.setFont(new Font("Tahoma", Font.BOLD, 14));
        lbl_telefono.setBounds(25, 55, 100, 20);
        panelContactos.add(lbl_telefono);

        JLabel lbl_email = new JLabel("EMAIL:");
        lbl_email.setFont(new Font("Tahoma", Font.BOLD, 14));
        lbl_email.setBounds(25, 90, 100, 20);
        panelContactos.add(lbl_email);

        // Campos de texto
        txt_nombres = new JTextField();
        txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txt_nombres.setBounds(135, 15, 400, 30);
        panelContactos.add(txt_nombres);

        txt_telefono = new JTextField();
        txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txt_telefono.setBounds(135, 50, 400, 30);
        panelContactos.add(txt_telefono);

        txt_email = new JTextField();
        txt_email.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txt_email.setBounds(135, 85, 400, 30);
        panelContactos.add(txt_email);

        // Checkbox favorito
        chb_favorito = new JCheckBox("CONTACTO FAVORITO");
        chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 13));
        chb_favorito.setBounds(25, 125, 180, 25);
        panelContactos.add(chb_favorito);

        // ComboBox categoría
        cmb_categoria = new JComboBox<>();
        cmb_categoria.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cmb_categoria.setBounds(220, 125, 315, 30);
        cmb_categoria.addItem("Elija una Categoria");
        cmb_categoria.addItem("Familia");
        cmb_categoria.addItem("Amigos");
        cmb_categoria.addItem("Trabajo");
        panelContactos.add(cmb_categoria);

        // Botones
        btn_add = new JButton("AGREGAR");
        btn_add.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn_add.setBounds(560, 15, 130, 50);
        panelContactos.add(btn_add);

        btn_modificar = new JButton("MODIFICAR");
        btn_modificar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn_modificar.setBounds(700, 15, 130, 50);
        panelContactos.add(btn_modificar);

        btn_eliminar = new JButton("ELIMINAR");
        btn_eliminar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btn_eliminar.setBounds(840, 15, 130, 50);
        panelContactos.add(btn_eliminar);

        btn_exportar = new JButton("EXPORTAR CSV");
        btn_exportar.setFont(new Font("Tahoma", Font.BOLD, 10));
        btn_exportar.setBounds(980, 15, 90, 50);
        panelContactos.add(btn_exportar);

        // Barra de progreso
        progressBar = new JProgressBar();
        progressBar.setBounds(25, 165, 1045, 20);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        panelContactos.add(progressBar);

        // Tabla de contactos
        String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla_contactos = new JTable(modeloTabla);
        tabla_contactos.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tabla_contactos.setRowHeight(25);
        tabla_contactos.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tabla_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        scrollTabla = new JScrollPane(tabla_contactos);
        scrollTabla.setBounds(25, 230, 1045, 380);
        panelContactos.add(scrollTabla);

        // Búsqueda
        JLabel lbl_buscar = new JLabel("BUSCAR:");
        lbl_buscar.setFont(new Font("Tahoma", Font.BOLD, 14));
        lbl_buscar.setBounds(25, 625, 100, 20);
        panelContactos.add(lbl_buscar);

        txt_buscar = new JTextField();
        txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txt_buscar.setBounds(135, 620, 935, 30);
        panelContactos.add(txt_buscar);
    }

    private void crearPestanaEstadisticas() {
        panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(null);
        tabbedPane.addTab("Estadísticas", null, panelEstadisticas, "Ver estadísticas de contactos");

        JLabel titulo = new JLabel("ESTADÍSTICAS DE CONTACTOS");
        titulo.setFont(new Font("Tahoma", Font.BOLD, 24));
        titulo.setBounds(300, 30, 500, 40);
        panelEstadisticas.add(titulo);

        // Etiquetas de estadísticas
        lbl_totalContactos = new JLabel("Total de Contactos: 0");
        lbl_totalContactos.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lbl_totalContactos.setBounds(100, 120, 400, 30);
        panelEstadisticas.add(lbl_totalContactos);

        lbl_favoritos = new JLabel("Contactos Favoritos: 0");
        lbl_favoritos.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lbl_favoritos.setBounds(100, 180, 400, 30);
        panelEstadisticas.add(lbl_favoritos);

        JLabel lblCategoria = new JLabel("Por Categoría:");
        lblCategoria.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblCategoria.setBounds(100, 240, 400, 30);
        panelEstadisticas.add(lblCategoria);

        lbl_familia = new JLabel("   • Familia: 0");
        lbl_familia.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lbl_familia.setBounds(120, 280, 400, 30);
        panelEstadisticas.add(lbl_familia);

        lbl_amigos = new JLabel("   • Amigos: 0");
        lbl_amigos.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lbl_amigos.setBounds(120, 320, 400, 30);
        panelEstadisticas.add(lbl_amigos);

        lbl_trabajo = new JLabel("   • Trabajo: 0");
        lbl_trabajo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lbl_trabajo.setBounds(120, 360, 400, 30);
        panelEstadisticas.add(lbl_trabajo);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaPrincipal frame = new VentanaPrincipal();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}