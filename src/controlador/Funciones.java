package controlador;

public class Funciones {

   // Función Suma con mas parametros
    public static double suma(double... numeros) {
        double resultado = 0;
        for (double num : numeros) {
            resultado += num;
        }
        return resultado;
    }
    
    // Función restar con mas parametros
    public static double resta(double... numeros) {
        double resultado = 0;
        for (double num : numeros) {
            resultado -= num;
        }
        return resultado;
    }

    // Función Multiplicacion con mas parametros
    public static double multiplicacion(double... numeros) {
        double resultado = 1; // Inicializar en 1 para la multiplicación
        for (double num : numeros) {
            resultado *= num;
        }
        return resultado;
    }

    // Función División con dos argumentos
    public static double division(double dividendo, double divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return dividendo / divisor;
    }

    // Función División con mas parametros
    public static double division(double... numeros) {
        if (numeros.length < 2) {
            throw new IllegalArgumentException("Se requieren al menos dos números para la división");
        }
        double resultado = numeros[0];
        for (int i = 1; i < numeros.length; i++) {
            if (numeros[i] == 0) {
                throw new ArithmeticException("No se puede dividir por cero");
            }
            resultado /= numeros[i];
        }
        return resultado;
    }
    
    // Función para sacar raíz cuadrada
    public static double raizCuadrada(double numero) {
        if (numero < 0) {
            throw new ArithmeticException("No se puede calcular la raíz cuadrada de un número negativo");
        }
        return Math.sqrt(numero);
    }
    
    //Función para resultado de un numero elevado a otro numero
    public static double elevarPotencia(double base, double exponente) {
        return Math.pow(base, exponente);
    }

    
}
