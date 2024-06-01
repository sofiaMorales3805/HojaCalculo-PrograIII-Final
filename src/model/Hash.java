package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Hash {

    private ObservableList<Integer>[] table;
    private int size;

    public Hash(int size) {
        this.size = size;
        table = new ObservableList[size];
        initializeTable();
    }
    //Este método inicializa cada bucket en la tabla de dispersión con una lista observable vacía.
    private void initializeTable() {
        for (int i = 0; i < size; i++) {
            table[i] = FXCollections.observableArrayList();
        }
    }

    public void insert(int key, int data) {
        int index = hashFunction(key);
        ObservableList<Integer> list = table[index];
        list.add(data);
    }

    public boolean contains(int key, int data) {
        int index = hashFunction(key);
        ObservableList<Integer> list = table[index];
        return list.contains(data);
    }

    //Devuelve todas los buckets de la tabla de dispersión como una lista observable de listas observables.
    public ObservableList<ObservableList<Integer>> getAllBuckets() {
        ObservableList<ObservableList<Integer>> buckets = FXCollections.observableArrayList();
        for (ObservableList<Integer> bucket : table) {
            buckets.add(bucket);
        }
        return buckets;
    }

    //Es una función hash,toma una clave y devuelve un índice en la tabla de dispersión.
    public int hashFunction(int key) {
        return key % size;
    }
}