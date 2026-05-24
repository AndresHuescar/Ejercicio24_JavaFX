# Ejercicio 24 - CRUD con JavaFX y SQLite

## Descripción

Este proyecto corresponde al ejercicio 24 del bloque BC5 de JavaFX.

El objetivo del ejercicio es implementar las operaciones CRUD en una aplicación JavaFX conectada a una base de datos.

CRUD significa:

- Crear
- Leer
- Actualizar
- Eliminar

En este proyecto se utiliza SQLite como base de datos local y JDBC para realizar la conexión desde Java.

## Tecnologías utilizadas

- Java
- JavaFX
- Maven
- SQLite
- JDBC
- IntelliJ IDEA

## Funcionamiento

La aplicación muestra una tabla `TableView` con empleados almacenados en una base de datos SQLite.

La base de datos se llama:

```text
empleados.db
```

Dentro de la base de datos se crea una tabla llamada `empleados` con los campos:

- `id`
- `nombre`
- `salario`

La aplicación permite:

- Agregar nuevos empleados.
- Ver todos los empleados en una tabla.
- Seleccionar un empleado y modificar sus datos.
- Eliminar un empleado seleccionado.
- Limpiar los campos del formulario.

## Operaciones CRUD

### Crear

Se introducen el nombre y el salario en los campos del formulario y se pulsa el botón **Agregar**.

### Leer

Los empleados se cargan automáticamente desde la base de datos y se muestran en el `TableView`.

### Actualizar

Se selecciona un empleado de la tabla, se modifican sus datos en el formulario y se pulsa el botón **Actualizar**.

### Eliminar

Se selecciona un empleado de la tabla y se pulsa el botón **Eliminar**.

## Conceptos utilizados

- `Application`: clase base para crear aplicaciones JavaFX.
- `Stage`: ventana principal.
- `Scene`: contenido visual de la ventana.
- `TableView`: tabla visual para mostrar datos.
- `TableColumn`: columnas de la tabla.
- `PropertyValueFactory`: conecta las columnas con los atributos del objeto.
- `TextField`: campos para introducir datos.
- `Button`: botones para realizar acciones.
- `GridPane`: layout usado para organizar el formulario.
- `VBox`: layout usado para organizar la interfaz principal.
- `ObservableList`: lista observable utilizada por JavaFX.
- `JDBC`: conexión entre Java y la base de datos.
- `Connection`: conexión con la base de datos.
- `PreparedStatement`: consulta SQL preparada.
- `ResultSet`: resultado de una consulta SQL.
- `Alert`: ventana de aviso para mostrar errores o mensajes.

## Estructura del proyecto

```text
Ejercicio24_JavaFX
 ├── pom.xml
 ├── empleados.db
 └── src
     └── main
         └── java
             └── org
                 └── example
                     └── Main.java
```

## Cómo ejecutar el proyecto

Para ejecutar el proyecto desde IntelliJ IDEA:

1. Abrir el proyecto en IntelliJ.
2. Sincronizar el archivo `pom.xml` con Maven.
3. Abrir el panel Maven.
4. Ejecutar:

```bash
mvn javafx:run
```

También se puede ejecutar desde la terminal con:

```bash
mvn javafx:run
```

## Autor

Andrés Huéscar Fernández
