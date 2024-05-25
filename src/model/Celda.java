/**
 * @Author JennyMorales 7690-08-6790
 */
package model;

public class Celda<T> {

    //Atributos
    private T contenidoCelda;

    //Constructor
    public Celda(T contenidoCelda) {
        this.contenidoCelda = contenidoCelda;
    }

    public T getContenidoCelda() {
        return contenidoCelda;
    }

    public void setContenidoCelda(T contenidoCelda) {
        this.contenidoCelda = contenidoCelda;
    }

    
}
