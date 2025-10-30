package Aplicacion;

import java.awt.EventQueue;

public class AplicacionContactos {
    
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar la aplicación en el Event Dispatch Thread
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    vista.VentanaPrincipal ventana = new vista.VentanaPrincipal();
                    controlador.LogicaContactos controlador = 
                        new controlador.LogicaContactos(ventana);
                    ventana.setVisible(true);
                    ventana.setLocationRelativeTo(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}	