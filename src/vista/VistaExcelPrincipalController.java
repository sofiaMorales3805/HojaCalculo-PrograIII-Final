package vista;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Celda;
import model.Hoja;
import controlador.Funciones;

public class VistaExcelPrincipalController<T> implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuBar menuOpcionesTop;
    @FXML
    private TabPane tabPaneMenuBottom;
    @FXML
    private Tab tabNameHoja;
    @FXML
    private Tab tabAddHoja;
    @FXML
    private TableView<ObservableList<Celda<String>>> tblExcel;

    private Hoja<String> hoja;
    private Funciones funcion;
    @FXML
    private Menu menuUnicoArchivo;
    @FXML
    private MenuItem menuOpcionGuardar;
    @FXML
    private MenuItem menuOpcionSuma;
    @FXML
    private MenuItem menuOpcionResta;
    @FXML
    private MenuItem menuOpcionMulti;
    @FXML
    private MenuItem menuOpcionDiv;
    @FXML
    private MenuItem menuOpcionCargar;
    @FXML
    private MenuItem menuOpcionHash;
    @FXML
    private Menu menuUnicoAyuda;
    @FXML
    private MenuItem menuOpcionCerrar;
    
    private TableView<ObservableList<Celda<String>>> nuevaTabla;
    private int numeroHoja = 2; // Contador para el número de hojas

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hoja = new Hoja<>();
        menuOpcionGuardar.setOnAction(event -> guardarArchivo());
        menuOpcionCargar.setOnAction(event -> cargarArchivo());
        menuOpcionHash.setOnAction(event -> abrirVistaHash());
        menuOpcionSuma.setOnAction(event -> insertarFormula(menuOpcionSuma));
        menuOpcionResta.setOnAction(event -> insertarFormula(menuOpcionResta));
        menuOpcionMulti.setOnAction(event -> insertarFormula(menuOpcionMulti));
        menuOpcionDiv.setOnAction(event -> insertarFormula(menuOpcionDiv));
        
        tblExcel.getSelectionModel().setCellSelectionEnabled(true);

        tabPaneMenuBottom.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab.equals(tabNameHoja)) {
                renombrarHoja();
            }
        });
        

        crearColumnasDinamicas(nuevaTabla);
        crearFilasEnumeradas(nuevaTabla);
        configurarEdicionCeldas(nuevaTabla);
        menuOpcionCerrar.setOnAction(event -> cerrarAplicacion());
        
        tabAddHoja.setOnSelectionChanged(event -> {
            if (tabAddHoja.isSelected()) {
                agregarHoja();
            }
        });

    }

    /**
     * Metodo para agregar una nueva hoja o tab en el excel
     */
    private void agregarHoja() {
        // Crear una nueva hoja
        Hoja<String> nuevaHojaTab = new Hoja<>();

        // Crear una nueva TableView para la hoja
        TableView<ObservableList<Celda<String>>> nuevaTablaAdd = new TableView<>();

        // Establecer las propiedades necesarias para la nueva tabla
        nuevaTablaAdd.setEditable(true);
        nuevaTablaAdd.getSelectionModel().setCellSelectionEnabled(true);
        //configurarEdicionCeldas();

        // Crear columnas y filas para la nueva hoja
        crearColumnasDinamicas(nuevaTablaAdd);
        crearFilasEnumeradas(nuevaTablaAdd);

        // Establecer el nombre de la nueva hoja
        String nombreHoja = "Hoja " + numeroHoja;
        numeroHoja++; // Incrementar el contador para la próxima hoja

        // Agregar la nueva tabla a una pestaña con el nombre de la hoja
        Tab nuevaPestaña = new Tab(nombreHoja);
        nuevaPestaña.setContent(nuevaTablaAdd);

        // Agregar la nueva pestaña al TabPane
        tabPaneMenuBottom.getTabs().add(tabPaneMenuBottom.getTabs().size() - 1, nuevaPestaña);
    }
    

    /**
     * Método para crear las columnas dinamicas del A a la AZ
     * @param tabla 
     */
    private void crearColumnasDinamicas(TableView<ObservableList<Celda<String>>> tabla) {
        int longitudNombreColumna = 1;
        for (int i = 0; i < 26 + Math.pow(26, longitudNombreColumna); i++) {
            // Calcular el índice actual dentro del bucle
            int currentIndex = i;
            StringBuilder columnName = new StringBuilder();
            // Generar el nombre de la columna
            while (currentIndex >= 0) {
                columnName.insert(0, (char) ('A' + currentIndex % 26));
                currentIndex /= 26;
                currentIndex -= 1; // ajustar al rango [0, 25]
            }
            // Crear la columna con el nombre generado
            TableColumn<ObservableList<Celda<String>>, String> column = new TableColumn<>(columnName.toString());
            final int columnIndex = tblExcel.getColumns().size();
            column.setCellValueFactory(cellData -> {
                ObservableList<Celda<String>> row = cellData.getValue();
                if (columnIndex >= row.size()) {
                    row.addAll(Collections.nCopies(columnIndex - row.size() + 1, new Celda<>("")));
                }
                return new SimpleStringProperty(row.get(columnIndex).getContenidoCelda());
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(event -> {
                ObservableList<Celda<String>> rowValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
                rowValue.get(event.getTablePosition().getColumn()).setContenidoCelda(event.getNewValue());
            });
            column.setEditable(true);
            tblExcel.getColumns().add(column);
        }
    }
    
    /**
     * Método que coloca el numero de cada fila
     * @param tabla 
     */
    private void crearFilasEnumeradas(TableView<ObservableList<Celda<String>>> tabla) {
        for (int i = 1; i <= 100; i++) {
            ObservableList<Celda<String>> row = FXCollections.observableArrayList();

            // Agregar número de fila en la primera columna
            row.add(new Celda<>(String.valueOf(i)));

            // Agregar celdas vacías en las columnas restantes
            for (int j = 1; j < tblExcel.getColumns().size(); j++) {
                row.add(new Celda<>(""));
            }
            tblExcel.getItems().add(row);
        }
    }

    /**
     * Para agregar editar celdas con texto normal
     * @param tabla 
     */
    private void configurarEdicionCeldas(TableView<ObservableList<Celda<String>>> tabla) {
        tblExcel.setEditable(true);
        tblExcel.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ObservableList<Celda<String>> selectedRow = tblExcel.getSelectionModel().getSelectedItem();
                int rowIndex = tblExcel.getSelectionModel().getSelectedIndex();
                int columnIndex = tblExcel.getSelectionModel().getSelectedCells().get(0).getColumn();
                
                 Celda<String> selectedCell = selectedRow.get(columnIndex);
                String formula = selectedCell.getContenidoCelda();

                if (formula.startsWith("=")) {
                    String[] tokens = formula.substring(1).split("\\(");
                    String operacion = tokens[0].toUpperCase();
                    String[] argumentos = tokens[1].replaceAll("\\)", "").split(",");

                    if (operacion.equals("SUMA")) {
                        double[] args = new double[argumentos.length];
                        for (int i = 0; i < argumentos.length; i++) {
                            args[i] = Double.parseDouble(argumentos[i]);
                        }
                        selectedCell.setContenidoCelda(String.valueOf(Funciones.suma(args)));
                    } else if (operacion.equals("RESTA")) {
                        double[] args = new double[argumentos.length];
                        for (int i = 0; i < argumentos.length; i++) {
                            args[i] = Double.parseDouble(argumentos[i]);
                        }
                        selectedCell.setContenidoCelda(String.valueOf(Funciones.resta(args)));
                    } else if (operacion.equals("MULTIPLICAR")) {
                        double[] args = new double[argumentos.length];
                        for (int i = 0; i < argumentos.length; i++) {
                            args[i] = Double.parseDouble(argumentos[i]);
                        }
                        selectedCell.setContenidoCelda(String.valueOf(Funciones.multiplicacion(args)));
                    } else if (operacion.equals("DIVIDIR")) {
                        double[] args = new double[argumentos.length];
                        for (int i = 0; i < argumentos.length; i++) {
                            args[i] = Double.parseDouble(argumentos[i]);
                        }
                        selectedCell.setContenidoCelda(String.valueOf(Funciones.division(args)));
                    }
                }
                
            }
        });
    }
    
    /**
     * Metodo para Insertar formulas
     */
    public void insertarFormula(MenuItem menuItem) {
        tblExcel.setEditable(true);
        ObservableList<Celda<String>> selectedRow = tblExcel.getSelectionModel().getSelectedItem();
        int rowIndex = tblExcel.getSelectionModel().getSelectedIndex();
        int columnIndex = tblExcel.getSelectionModel().getSelectedCells().get(0).getColumn();
        
        Celda<String> selectedCell = selectedRow.get(columnIndex);
        // Obtener el texto del elemento del menú seleccionado
        String menuItemText = menuItem.getText();

        // Configurar el contenido de la celda dependiendo del elemento del menú seleccionado
        if (menuItemText.equals("Suma")) {
            selectedCell.setContenidoCelda("=suma(");
        } else if (menuItemText.equals("Resta")) {
            selectedCell.setContenidoCelda("=resta(");
        } else if (menuItemText.equals("Multiplicacion")) {
            selectedCell.setContenidoCelda("=multiplicar(");
        } else if (menuItemText.equals("Division")) {
            selectedCell.setContenidoCelda("=dividir(");
        }

        // Agregar un paréntesis de cierre para que el usuario pueda completar la fórmula
        selectedCell.setContenidoCelda(selectedCell.getContenidoCelda() + ")");
    }

    private void renombrarHoja() {
        TextInputDialog dialog = new TextInputDialog(tabNameHoja.getText());
        dialog.setTitle("Renombrar hoja");
        dialog.setHeaderText(null);
        dialog.setContentText("Ingrese el nuevo nombre de la hoja:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            tabNameHoja.setText(nombre);
        });
    }

    private void guardarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo CSV");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo CSV", "*.csv"));
        Stage stage = (Stage) rootPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Escribir encabezados de columnas
                String headers = tblExcel.getColumns().stream()
                        .map(TableColumn::getText)
                        .collect(Collectors.joining(","));
                writer.println(headers);

                // Escribir datos de celdas
                tblExcel.getItems().forEach(row -> {
                    String rowData = row.stream()
                            .map(Celda::getContenidoCelda)
                            .collect(Collectors.joining(","));
                    writer.println(rowData);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar archivo CSV");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivo CSV", "*.csv"));
        Stage stage = (Stage) rootPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                // Leer encabezados de columnas
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] headers = line.split(",");
                    // Crear columnas dinámicamente
                    for (String header : headers) {
                        TableColumn<ObservableList<Celda<String>>, String> column = new TableColumn<>(header);
                        column.setCellValueFactory(cellData -> {
                            ObservableList<Celda<String>> row = cellData.getValue();
                            int columnIndex = tblExcel.getColumns().indexOf(column);
                            if (columnIndex < row.size()) {
                                return new SimpleStringProperty(row.get(columnIndex).getContenidoCelda());
                            }
                            return new SimpleStringProperty("");
                        });
                        column.setCellFactory(TextFieldTableCell.forTableColumn());
                        column.setOnEditCommit(event -> {
                            ObservableList<Celda<String>> rowValue = event.getTableView().getItems().get(event.getTablePosition().getRow());
                            rowValue.get(event.getTablePosition().getColumn()).setContenidoCelda(event.getNewValue());
                        });
                        column.setEditable(true);
                        tblExcel.getColumns().add(column);
                    }
                }

                // Leer datos de celdas
                tblExcel.getItems().clear();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] rowData = line.split(",");
                    ObservableList<Celda<String>> row = FXCollections.observableArrayList();
                    for (String cellData : rowData) {
                        row.add(new Celda<>(cellData));
                    }
                    tblExcel.getItems().add(row);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirVistaHash() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaHash.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Tabla Hash");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  

    private void cerrarAplicacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar aplicación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas cerrar la aplicación?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
            System.out.println("Cerrando aplicación...");
        }
    }
}
