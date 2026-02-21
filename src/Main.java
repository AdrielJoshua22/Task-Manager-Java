import model.Task;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("--- Probando creación correcta ---");
            Task tarea1 = new Task("Estudiar Algoritmos", 5);
            System.out.println(tarea1);

            System.out.println("\n--- Probando validación de error ---");

            Task tareaError = new Task("Tarea Fallida", 10);

        } catch (IllegalArgumentException e) {
            System.out.println("Error capturado correctamente: " + e.getMessage());
        }
    }
}