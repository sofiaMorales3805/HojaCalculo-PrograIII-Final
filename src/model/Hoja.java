package model;

import java.util.List;

public class Hoja<T> {

    // Clase interna para representar una fila
    private class Fila {
        Celda<T> celda;
        Fila siguiente;

        public Fila(Celda<T> celda) {
            this.celda = celda;
            this.siguiente = null;
        }
    }

    // Atributo para la cabeza de la lista de filas
    private Fila cabeza;

    public Hoja() {
        this.cabeza = null;
    }

    public void addRow(List<Celda<T>> fila) {
        for (Celda<T> celda : fila) {
            addCelda(celda);
        }
    }

    private void addCelda(Celda<T> celda) {
        if (cabeza == null) {
            cabeza = new Fila(celda);
        } else {
            Fila actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = new Fila(celda);
        }
    }

    public void setCelda(int rowIndex, int columnIndex, Celda<T> celda) {
        Fila actual = cabeza;
        for (int i = 0; i < rowIndex && actual != null; i++) {
            actual = actual.siguiente;
        }
        if (actual != null) {
            for (int i = 0; i < columnIndex && actual.celda != null; i++) {
                actual = actual.siguiente;
            }
            if (actual != null) {
                actual.celda = celda;
            }
        }
    }
}
