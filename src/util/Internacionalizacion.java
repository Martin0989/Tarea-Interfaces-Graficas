package util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase de utilidad para gestionar la internacionalización (i18n).
 */
public class Internacionalizacion {
    private ResourceBundle bundle;
    
    // La ruta base a los archivos de propiedades (debe coincidir con la carpeta 'recursos')
    private static final String BUNDLE_BASE_NAME = "recursos.Mensajes"; 

    public Internacionalizacion(Locale locale) {
        bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }
    
    public String getTexto(String key) {
        try {
            return bundle.getString(key);
        } catch (java.util.MissingResourceException e) {
            System.err.println("Clave de i18n no encontrada: " + key);
            return "!!" + key + "!!";
        }
    }
    
    public static Locale getLocalePorSeleccion(String seleccion) {
        return switch (seleccion) {
            case "Español" -> new Locale("es", "ES");
            case "English" -> new Locale("en", "US");
            case "Português" -> new Locale("pt", "BR");
            default -> new Locale("es", "ES");
        };
    }
}