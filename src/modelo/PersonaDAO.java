package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar la persistencia de contactos en archivo CSV
 */
public class PersonaDAO {
    private File archivo;
    private Persona persona;

    public PersonaDAO(Persona persona) {
        this.persona = persona;
        archivo = new File("C:/Users/mog_0/Documents/deber/datosContactos");
        prepararArchivo();
    }

    /**
     * Prepara el directorio y archivo CSV
     */
    private void prepararArchivo() {
        if (!archivo.exists()) {
            archivo.mkdir();
        }
        archivo = new File(archivo.getAbsolutePath(), "datosContactos.csv");
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                String encabezado = String.format("%s;%s;%s;%s;%s", 
                    "NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA", "FAVORITO");
                escribir(encabezado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Escribe texto en el archivo
     */
    private void escribir(String texto) {
        try {
            FileWriter escribir = new FileWriter(archivo.getAbsolutePath(), true);
            escribir.write(texto + "\n");
            escribir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Escribe un contacto en el archivo
     */
    public boolean escribirArchivo() {
        escribir(persona.datosContacto());
        return true;
    }

    /**
     * Lee todos los contactos del archivo
     */
    public List<Persona> leerArchivo() throws IOException {
        List<Persona> personas = new ArrayList<>();
        
        // Verificar si el archivo existe
        if (!archivo.exists()) {
            return personas;
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String linea;
        
        while ((linea = reader.readLine()) != null) {
            // Saltar líneas vacías
            if (linea.trim().isEmpty()) {
                continue;
            }
            
            String[] campos = linea.split(";");
            
            // Verificar que tenga todos los campos necesarios
            if (campos.length >= 5) {
                Persona p = new Persona();
                p.setNombre(campos[0].trim());
                p.setTelefono(campos[1].trim());
                p.setEmail(campos[2].trim());
                p.setCategoria(campos[3].trim());
                p.setFavorito(Boolean.parseBoolean(campos[4].trim()));
                personas.add(p);
            }
        }
        
        reader.close();
        return personas;
    }
    /**
     * Actualiza todos los contactos en el archivo
     */
    public void actualizarContactos(List<Persona> personas) throws IOException {
        archivo.delete();
        prepararArchivo();
        for (Persona p : personas) {
            new PersonaDAO(p).escribirArchivo();
        }
    }

    /**
     * Exporta contactos a CSV con formato específico
     */
    public static void exportarCSV(List<Persona> contactos, String rutaDestino) throws IOException {
        FileWriter writer = new FileWriter(rutaDestino);
        writer.write("NOMBRE,TELEFONO,EMAIL,CATEGORIA,FAVORITO\n");
        
        for (Persona p : contactos) {
            writer.write(String.format("%s,%s,%s,%s,%s\n",
                p.getNombre(), p.getTelefono(), p.getEmail(), 
                p.getCategoria(), p.isFavorito()));
        }
        writer.close();
    }
}