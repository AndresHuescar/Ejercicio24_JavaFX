package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    // Ruta de la base de datos SQLite. Se crea automáticamente en la raíz del proyecto.
    private static final String URL = "jdbc:sqlite:empleados.db";

    private TableView<Empleado> tabla;
    private TextField campoNombre;
    private TextField campoSalario;

    @Override
    public void start(Stage stage) {

        // Creo la base de datos y la tabla si no existen
        crearBaseDatos();

        // Inserto datos iniciales si la tabla está vacía
        insertarDatosIniciales();

        Label titulo = new Label("Gestión de empleados - CRUD");

        // Creo la tabla donde se mostrarán los empleados
        tabla = new TableView<>();

        TableColumn<Empleado, Integer> columnaId = new TableColumn<>("ID");
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Empleado, String> columnaNombre = new TableColumn<>("Nombre");
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Empleado, Double> columnaSalario = new TableColumn<>("Salario");
        columnaSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        columnaId.setPrefWidth(80);
        columnaNombre.setPrefWidth(220);
        columnaSalario.setPrefWidth(120);

        tabla.getColumns().addAll(columnaId, columnaNombre, columnaSalario);

        // Cargo los datos de la base de datos en la tabla
        tabla.setItems(cargarEmpleados());

        // Campos del formulario
        Label labelNombre = new Label("Nombre:");
        campoNombre = new TextField();
        campoNombre.setPromptText("Nombre del empleado");

        Label labelSalario = new Label("Salario:");
        campoSalario = new TextField();
        campoSalario.setPromptText("Salario del empleado");

        // Botones CRUD
        Button botonAgregar = new Button("Agregar");
        Button botonActualizar = new Button("Actualizar");
        Button botonEliminar = new Button("Eliminar");
        Button botonLimpiar = new Button("Limpiar");

        botonAgregar.setPrefWidth(120);
        botonActualizar.setPrefWidth(120);
        botonEliminar.setPrefWidth(120);
        botonLimpiar.setPrefWidth(120);

        // Acción para agregar un empleado nuevo
        botonAgregar.setOnAction(event -> agregarEmpleado());

        // Acción para actualizar el empleado seleccionado
        botonActualizar.setOnAction(event -> actualizarEmpleado());

        // Acción para eliminar el empleado seleccionado
        botonEliminar.setOnAction(event -> eliminarEmpleado());

        // Acción para limpiar los campos
        botonLimpiar.setOnAction(event -> limpiarCampos());

        // Cuando selecciono un empleado en la tabla, sus datos pasan al formulario
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antiguo, empleadoSeleccionado) -> {
            if (empleadoSeleccionado != null) {
                campoNombre.setText(empleadoSeleccionado.getNombre());
                campoSalario.setText(String.valueOf(empleadoSeleccionado.getSalario()));
            }
        });

        // Formulario organizado con GridPane
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(labelNombre, 0, 0);
        formulario.add(campoNombre, 1, 0);

        formulario.add(labelSalario, 0, 1);
        formulario.add(campoSalario, 1, 1);

        formulario.add(botonAgregar, 0, 2);
        formulario.add(botonActualizar, 1, 2);
        formulario.add(botonEliminar, 0, 3);
        formulario.add(botonLimpiar, 1, 3);

        formulario.setStyle("-fx-alignment: center;");

        // Contenedor principal
        VBox root = new VBox(15);
        root.getChildren().addAll(titulo, tabla, formulario);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 650, 520);

        stage.setTitle("Ejercicio 24 - CRUD JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    // Crea la tabla empleados si no existe
    private void crearBaseDatos() {
        String sql = """
                CREATE TABLE IF NOT EXISTS empleados (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    salario REAL NOT NULL
                );
                """;

        try (Connection conexion = DriverManager.getConnection(URL);
             Statement sentencia = conexion.createStatement()) {

            sentencia.execute(sql);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo crear la base de datos: " + e.getMessage());
        }
    }

    // Inserta empleados de ejemplo solo si la tabla está vacía
    private void insertarDatosIniciales() {
        String comprobar = "SELECT COUNT(*) FROM empleados";
        String insertar = "INSERT INTO empleados (nombre, salario) VALUES (?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL);
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(comprobar)) {

            if (resultado.next() && resultado.getInt(1) == 0) {

                try (PreparedStatement ps = conexion.prepareStatement(insertar)) {

                    ps.setString(1, "Andrés");
                    ps.setDouble(2, 1500.00);
                    ps.executeUpdate();

                    ps.setString(1, "María");
                    ps.setDouble(2, 1800.50);
                    ps.executeUpdate();

                    ps.setString(1, "Carlos");
                    ps.setDouble(2, 2100.75);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron insertar los datos iniciales: " + e.getMessage());
        }
    }

    // Lee los empleados de la base de datos
    private ObservableList<Empleado> cargarEmpleados() {
        ObservableList<Empleado> empleados = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, salario FROM empleados";

        try (Connection conexion = DriverManager.getConnection(URL);
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {

            while (resultado.next()) {
                empleados.add(new Empleado(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getDouble("salario")
                ));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los empleados: " + e.getMessage());
        }

        return empleados;
    }

    // CREATE: añade un empleado nuevo
    private void agregarEmpleado() {
        String nombre = campoNombre.getText();
        String salarioTexto = campoSalario.getText();

        if (nombre.isEmpty() || salarioTexto.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debes rellenar el nombre y el salario.");
            return;
        }

        try {
            double salario = Double.parseDouble(salarioTexto);

            String sql = "INSERT INTO empleados (nombre, salario) VALUES (?, ?)";

            try (Connection conexion = DriverManager.getConnection(URL);
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setDouble(2, salario);
                ps.executeUpdate();
            }

            actualizarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El salario debe ser un número válido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el empleado: " + e.getMessage());
        }
    }

    // UPDATE: modifica el empleado seleccionado
    private void actualizarEmpleado() {
        Empleado empleadoSeleccionado = tabla.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta("Sin selección", "Debes seleccionar un empleado de la tabla.");
            return;
        }

        String nombre = campoNombre.getText();
        String salarioTexto = campoSalario.getText();

        if (nombre.isEmpty() || salarioTexto.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debes rellenar el nombre y el salario.");
            return;
        }

        try {
            double salario = Double.parseDouble(salarioTexto);

            String sql = "UPDATE empleados SET nombre = ?, salario = ? WHERE id = ?";

            try (Connection conexion = DriverManager.getConnection(URL);
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setDouble(2, salario);
                ps.setInt(3, empleadoSeleccionado.getId());
                ps.executeUpdate();
            }

            actualizarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El salario debe ser un número válido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar el empleado: " + e.getMessage());
        }
    }

    // DELETE: elimina el empleado seleccionado
    private void eliminarEmpleado() {
        Empleado empleadoSeleccionado = tabla.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta("Sin selección", "Debes seleccionar un empleado de la tabla.");
            return;
        }

        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conexion = DriverManager.getConnection(URL);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, empleadoSeleccionado.getId());
            ps.executeUpdate();

            actualizarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar el empleado: " + e.getMessage());
        }
    }

    // Recarga los datos del TableView
    private void actualizarTabla() {
        tabla.setItems(cargarEmpleados());
    }

    // Limpia los campos del formulario
    private void limpiarCampos() {
        campoNombre.clear();
        campoSalario.clear();
        tabla.getSelectionModel().clearSelection();
    }

    // Muestra alertas para errores o avisos
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

    // Clase modelo que representa un empleado
    public static class Empleado {

        private int id;
        private String nombre;
        private double salario;

        public Empleado(int id, String nombre, double salario) {
            this.id = id;
            this.nombre = nombre;
            this.salario = salario;
        }

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public double getSalario() {
            return salario;
        }
    }
}