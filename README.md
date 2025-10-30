# Tarea-Interfaces
Actividad: Optimización de la Aplicación de Gestión de Contactos 
# GUÍA DE USUARIO
## Sistema de Gestión de Contactos Optimizado

---

## 📋 ÍNDICE

1. Introducción
2. Instalación y Configuración
3. Funcionalidades Principales
4. Atajos de Teclado
5. Solución de Problemas
6. Preguntas Frecuentes

---

## 1. INTRODUCCIÓN

El Sistema de Gestión de Contactos es una aplicación de escritorio desarrollada en Java que permite administrar información de contactos de manera eficiente, con funciones de búsqueda, filtrado, ordenamiento y exportación.

### Características Principales
- ✅ Gestión completa de contactos (Agregar, Modificar, Eliminar)
- ✅ Búsqueda en tiempo real
- ✅ Ordenamiento por cualquier columna
- ✅ Exportación a formato CSV
- ✅ Estadísticas automáticas
- ✅ Interfaz intuitiva con pestañas
- ✅ Atajos de teclado para mayor productividad

---

## 2. INSTALACIÓN Y CONFIGURACIÓN

### Requisitos Previos
- **Java JDK:** Versión 8 o superior
- **Sistema Operativo:** Windows, Linux o macOS
- **Espacio en Disco:** Mínimo 50 MB

### Pasos de Instalación

1. **Descargar el código fuente**
   ```
   Descargar los archivos del proyecto
   ```

2. **Compilar el proyecto**
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Ejecutar la aplicación**
   ```bash
   java -cp bin main.AplicacionContactos
   ```

### Primera Ejecución

Al ejecutar por primera vez:
- Se creará automáticamente la carpeta `c:/datosContactos/`
- Se generará el archivo `datosContactos.csv` para almacenar los contactos
- La aplicación estará lista para usar

---

## 3. FUNCIONALIDADES PRINCIPALES

### 3.1 Pestaña "Contactos"

#### Agregar un Nuevo Contacto

1. Completar los campos del formulario:
   - **Nombres:** Nombre completo del contacto
   - **Teléfono:** Número de teléfono
   - **Email:** Correo electrónico
   
2. Seleccionar una **Categoría** (Familia, Amigos, Trabajo)

3. Marcar como **Favorito** (opcional)

4. Hacer clic en el botón **AGREGAR** o presionar `Ctrl + S`

5. El contacto aparecerá en la tabla automáticamente

**Validaciones:**
- Todos los campos son obligatorios
- Debe seleccionar una categoría válida
- Se mostrará un mensaje de confirmación al guardar

#### Modificar un Contacto Existente

**Opción 1: Usando botones**
1. Seleccionar el contacto en la tabla
2. Los datos se cargarán automáticamente en el formulario
3. Modificar los campos deseados
4. Hacer clic en **MODIFICAR**

**Opción 2: Usando menú contextual**
1. Clic derecho sobre el contacto en la tabla
2. Seleccionar "Editar"
3. Modificar los datos
4. Guardar cambios

#### Eliminar un Contacto

**Opción 1: Usando botón**
1. Seleccionar el contacto en la tabla
2. Hacer clic en **ELIMINAR** o presionar `Ctrl + E`
3. Confirmar la eliminación en el diálogo

**Opción 2: Usando menú contextual**
1. Clic derecho sobre el contacto
2. Seleccionar "Eliminar"
3. Confirmar la acción

#### Buscar Contactos

1. Escribir en el campo **BUSCAR** en la parte inferior
2. La tabla se filtrará automáticamente mientras escribe
3. La búsqueda aplica a todos los campos (nombre, teléfono, email, categoría)
4. Para ver todos los contactos nuevamente, borrar el texto de búsqueda

#### Ordenar Contactos

1. Hacer clic en el encabezado de cualquier columna
2. **Primer clic:** Ordena ascendentemente
3. **Segundo clic:** Ordena descendentemente
4. Se puede ordenar por: Nombre, Teléfono, Email, Categoría o Favorito

#### Exportar Contactos a CSV

1. Hacer clic en el botón **EXPORTAR CSV**
2. Seleccionar la ubicación y nombre del archivo
3. Hacer clic en "Guardar"
4. El archivo CSV se generará con todos los contactos

**Formato del archivo exportado:**
```
NOMBRE,TELEFONO,EMAIL,CATEGORIA,FAVORITO
Juan Pérez,0991234567,juan@email.com,Familia,true
María López,0987654321,maria@email.com,Amigos,false
```

### 3.2 Pestaña "Estadísticas"

Muestra información resumida sobre los contactos:

- **Total de Contactos:** Cantidad total de contactos registrados
- **Contactos Favoritos:** Cantidad de contactos marcados como favoritos
- **Por Categoría:**
  - Familia
  - Amigos
  - Trabajo

Las estadísticas se actualizan automáticamente al:
- Agregar un nuevo contacto
- Modificar un contacto existente
- Eliminar un contacto

---

## 4. ATAJOS DE TECLADO

| Atajo | Acción | Descripción |
|-------|--------|-------------|
| `Ctrl + N` | Nuevo Contacto | Limpia el formulario para agregar un nuevo contacto |
| `Ctrl + S` | Guardar | Guarda el contacto actual (equivalente a AGREGAR) |
| `Ctrl + E` | Eliminar | Elimina el contacto seleccionado |
| `Clic Derecho` | Menú Contextual | Muestra opciones rápidas (Editar, Eliminar, Limpiar) |

**Consejo:** Los atajos de teclado aumentan significativamente la productividad al trabajar con muchos contactos.

---

**Formato requerido:**
```
nombre;telefono;email;categoria;favorito
```

