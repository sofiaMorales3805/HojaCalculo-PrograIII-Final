package controlador;

/**
 *
 * @author Jenny
 */
public class FuncionHash {
    public static int calcularHash(String dato) {
        // Algoritmo de función hash simple
        int hash = 0;
        for (int i = 0; i < dato.length(); i++) {
            hash = 31 * hash + dato.charAt(i);
        }
        return hash;
    }
}
