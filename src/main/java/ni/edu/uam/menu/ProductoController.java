package ni.edu.uam.menu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Function;

public class ProductoController {

    @FXML
    private TextField codigoField;
    @FXML
    private TextField nombreField;
    @FXML
    private TextField categoriaField;
    @FXML
    private TextField precioField;
    @FXML
    private TextField existenciaField;

    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    @FXML
    private Label mensajeLabel;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    /** Producto actualmente cargado en el formulario para edición; null si se está creando uno nuevo. */
    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));

        colPrecio.setCellFactory(columna -> crearCeldaCentrada(precio ->
                precio == null ? "" : String.format("C$ %.2f", precio)));
        colExistencia.setCellFactory(columna -> crearCeldaCentrada(existencia ->
                existencia == null ? "" : String.valueOf(existencia)));

        tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaProductos.setItems(productos);

        // Cargar datos de ejemplo para probar la aplicación rápidamente.
        productos.add(new Producto("P001", "Arroz Excelencia", "Granos", 25.50, 100));
        productos.add(new Producto("P002", "Aceite Numar", "Abarrotes", 65.00, 40));
        productos.add(new Producto("P003", "Café Presto", "Bebidas", 48.75, 60));
    }

    // ---- Operación: Nuevo (MenuBar Archivo > Nuevo, ToolBar "Nuevo", Ctrl+N) ----
    @FXML
    private void nuevoProducto() {
        tablaProductos.getSelectionModel().clearSelection();
        productoSeleccionado = null;
        limpiarFormulario();
        codigoField.requestFocus();
        mensajeLabel.setText("Formulario listo para registrar un nuevo producto.");
    }

    // ---- Operación: Editar (MenuBar Producto > Editar, ToolBar "Editar", ContextMenu "Editar") ----
    @FXML
    private void editarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("Debe seleccionar un producto de la tabla para editarlo.");
            return;
        }
        cargarFormulario(seleccionado);
        productoSeleccionado = seleccionado;
        mensajeLabel.setText("Editando el producto " + seleccionado.getCodigo() + ". Modifique los datos y presione Guardar.");
    }

    // ---- Operación: Guardar (MenuBar Archivo > Guardar, ToolBar "Guardar", Ctrl+G) ----
    @FXML
    private void guardarProducto() {
        String codigo = codigoField.getText().trim();
        String nombre = nombreField.getText().trim();
        String categoria = categoriaField.getText().trim();
        String precioTexto = precioField.getText().trim();
        String existenciaTexto = existenciaField.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || categoria.isEmpty()
                || precioTexto.isEmpty() || existenciaTexto.isEmpty()) {
            mensajeLabel.setText("Complete todos los campos antes de guardar.");
            return;
        }

        double precio;
        int existencia;
        try {
            precio = Double.parseDouble(precioTexto);
            existencia = Integer.parseInt(existenciaTexto);
        } catch (NumberFormatException e) {
            mensajeLabel.setText("Precio y existencia deben ser valores numéricos.");
            return;
        }

        if (productoSeleccionado != null) {
            // Edición de un producto existente.
            productoSeleccionado.setCodigo(codigo);
            productoSeleccionado.setNombre(nombre);
            productoSeleccionado.setCategoria(categoria);
            productoSeleccionado.setPrecio(precio);
            productoSeleccionado.setExistencia(existencia);
            tablaProductos.refresh();
            mensajeLabel.setText("Producto " + codigo + " actualizado correctamente.");
        } else {
            boolean existeCodigo = productos.stream()
                    .anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo));
            if (existeCodigo) {
                mensajeLabel.setText("Ya existe un producto con el código " + codigo + ".");
                return;
            }
            productos.add(new Producto(codigo, nombre, categoria, precio, existencia));
            mensajeLabel.setText("Producto " + codigo + " agregado correctamente.");
        }

        productoSeleccionado = null;
        tablaProductos.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    // ---- Operación: Eliminar (MenuBar Producto > Eliminar, ToolBar "Eliminar", ContextMenu "Eliminar") ----
    @FXML
    private void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mensajeLabel.setText("Debe seleccionar un producto de la tabla para eliminarlo.");
            return;
        }

        Alert confirmacion = new Alert(AlertType.CONFIRMATION,
                "¿Desea eliminar el producto \"" + seleccionado.getNombre() + "\" (" + seleccionado.getCodigo() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                productos.remove(seleccionado);
                productoSeleccionado = null;
                limpiarFormulario();
                mensajeLabel.setText("Producto eliminado correctamente.");
            } else {
                mensajeLabel.setText("Eliminación cancelada.");
            }
        });
    }

    // ---- Operación: Salir (MenuBar Archivo > Salir, ToolBar "Salir", Ctrl+Q) ----
    @FXML
    private void salirAplicacion() {
        Alert confirmacion = new Alert(AlertType.CONFIRMATION,
                "¿Desea salir de la aplicación?", ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar salida");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait()
                .filter(respuesta -> respuesta == ButtonType.YES)
                .ifPresent(respuesta -> Platform.exit());
    }

    @FXML
    private void mostrarAcercaDe() {
        Alert info = new Alert(AlertType.INFORMATION,
                "Distribuidora El Güegüense\nGestión de inventario de productos.\n"
                        + "Práctica JavaFX: Menús y barras de herramientas.");
        info.setTitle("Acerca de");
        info.setHeaderText(null);
        info.showAndWait();
    }

    private void cargarFormulario(Producto producto) {
        codigoField.setText(producto.getCodigo());
        nombreField.setText(producto.getNombre());
        categoriaField.setText(producto.getCategoria());
        precioField.setText(String.valueOf(producto.getPrecio()));
        existenciaField.setText(String.valueOf(producto.getExistencia()));
    }

    private <T> TableCell<Producto, T> crearCeldaCentrada(Function<T, String> formateador) {
        TableCell<Producto, T> celda = new TableCell<>() {
            @Override
            protected void updateItem(T valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio ? null : formateador.apply(valor));
            }
        };
        celda.setAlignment(Pos.CENTER_RIGHT);
        return celda;
    }

    private void limpiarFormulario() {
        codigoField.clear();
        nombreField.clear();
        categoriaField.clear();
        precioField.clear();
        existenciaField.clear();
    }
}
