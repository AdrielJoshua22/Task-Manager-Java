import model.Task;
import repository.TaskRepository;
import repository.impl.InMemoryTaskRepository;
import service.TaskService;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializamos las capas (Inyección de dependencias)
        TaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        System.out.println("=== Iniciando Prueba del Motor TaskManager ===\n");

        // 2. Probamos la creación de tareas
        service.createTask("Estudiar Algoritmos", "Repasar Big O Notation", LocalDate.now().plusDays(2));
        service.createTask("Gym", "Entrenamiento de fuerza", LocalDate.now());

        // 3. Listamos las tareas actuales
        System.out.println("\n--- Lista de Tareas ---");
        service.getTasksOrderedByDate().forEach(System.out::println);

        // 4. Probamos la lógica de completar una tarea
        System.out.println("\nCompletando la tarea ID 1...");
        service.markTaskAsCompleted(1);

        // 5. Verificamos que los filtros funcionen
        System.out.println("\n--- Tareas Pendientes ---");
        service.getPendingTasks().forEach(System.out::println);

        System.out.println("\n=== Prueba Finalizada con Éxito ===");
    }
}