/**
 * @author JennyMorales
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.Hash;
import controlador.FuncionHash;
import javafx.beans.property.SimpleIntegerProperty;

public class HashController implements Initializable {

    @FXML
    private Button btnCrearTablaHash;
    @FXML
    private TextField txtBuckets;
    @FXML
    private TextField txtIngresarDatos;
    @FXML
    private Button btnInsertInTablaHash;
    @FXML
    private TableView<Bucket> tblHash;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnBorrarTodo;
    @FXML
    private Button btnAbrir;

    private Hash hash;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnCrearTablaHash.setOnAction(event -> crearTablaHash());
        btnInsertInTablaHash.setOnAction(event -> insertarEnTabla());
        btnBorrarTodo.setOnAction(event -> vaciar());
    }

    
     private void crearTablaHash() {
        
        int buckets = obtenerPrimoAleatorio();
        hash = new Hash(buckets);

        tblHash.getItems().clear();
        tblHash.getColumns().clear();

            TableColumn<Bucket, String> columnaDato = new TableColumn<>("A");
            columnaDato.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValor()));
            
            TableColumn<Bucket, String> columnaClave = new TableColumn<>("B");
            columnaClave.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClave()));

        tblHash.getColumns().addAll(columnaDato, columnaClave);

        ObservableList<Bucket> bucketsList = FXCollections.observableArrayList();

        for (int i = 0; i < buckets; i++) {
            Bucket bucket = new Bucket(null,null);
            bucketsList.add(bucket);
        }

        tblHash.setItems(bucketsList);

        //txtBuckets.setDisable(true);
        btnCrearTablaHash.setDisable(true);
    }
    
    
    private int obtenerPrimoAleatorio() {
        while (true) {
            int numero = (int) (Math.random() * (224 - 2)) + 2; // Genera un número aleatorio 
            if (esPrimo(numero)) {
                return numero;
            }
        }
    }
    //Función para sabersi es primo y devolverlo para crear
    private boolean esPrimo(int numero) {
        if (numero <= 1) {
            return false;
        }
        if (numero <= 3) {
            return true;
        }
        if (numero % 2 == 0 || numero % 3 == 0) {
            return false;
        }
        for (int i = 5; i * i <= numero; i = i + 6) {
            if (numero % i == 0 || numero % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    private void insertarEnTabla() {
        System.out.println("Insertando dato en la tabla");

        if (hash != null) {
            String  dataIngresada = txtIngresarDatos.getText();

            if (tablaLlena()) {
                mostrarAlertaTablaLlena();
            } else {
                 // Calcula la clave utilizando la función hash
                String clave = String.valueOf(FuncionHash.calcularHash(dataIngresada));
                ObservableList<Bucket> bucketsList = tblHash.getItems();
                System.out.println("Clave :"+clave);
                System.out.println("dataIngresada :"+dataIngresada);
                
                 // Agrega el nuevo elemento al inicio de la lista
                Bucket nuevoBucket = new Bucket(dataIngresada, clave);
                bucketsList.add(0,nuevoBucket);
                
                if (bucketsList.isEmpty()) {
                    mostrarAlertaIndiceInvalido();
                }
            }
            txtIngresarDatos.clear();
        }
    }

    
    private void vaciar() {
        tblHash.getItems().clear();
        txtBuckets.setDisable(false);
        btnCrearTablaHash.setDisable(false);
    }

    private void mostrarAlertaIndiceInvalido() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText("Ha Ocurrido un Error al Insertar!");
        alert.showAndWait();
    }

    private boolean tablaLlena() {
        for (Bucket bucket : tblHash.getItems()) {
            if (bucket.getValor() == null) {
                return false;
            }
        }
        return true;
    }

    public void mostrarAlertaTablaLlena() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Tabla Hash Llena");
        alert.setHeaderText("Tabla Hash Llena");
        alert.setContentText("La Tabla Hash Esta Llena");
        alert.showAndWait();
    }

    public static class Bucket {

        private final StringProperty valor;
       private final StringProperty clave;

        public Bucket(String valor, String clave) {
            this.valor = new SimpleStringProperty(valor);
            this.clave = new SimpleStringProperty(clave);
                    }

        public String getValor() {
            return valor.get();
        }

        public void setValor(String valor) {
            this.valor.set(valor);
        }

        public StringProperty valorProperty() {
            return valor;
        }

       public String getClave() {
            return clave.get();
        }
        public void setClave(String clave) {
            this.valor.set(clave);
        }
        
        public StringProperty claveProperty() {
            return clave;
        }
        
        public List<String> getValores() {
            List<String> valores = new ArrayList<>();
            if (clave != null && clave.get() != null && !clave.get().isEmpty()) {
                valores.add(clave.get());
            }
            return valores;
        }
    }

}