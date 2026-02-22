
import model.Task;
import repository.TaskRepository;
import repository.impl.InMemoryTaskRepository;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new InMemoryTaskRepository();
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        System.out.println("--- BIENVENIDO A TU TASK MANAGER ---");

        while (option != 3) {
            System.out.println("\n1. Agregar Tarea");
            System.out.println("2. Listar Tareas");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                System.out.print("Título de la tarea: ");
                String title = scanner.nextLine();

                System.out.print("Descripción: ");
                String description = scanner.nextLine();

                int id = repository.findAll().size() + 1;

                Task newTask = new Task(id, title, description, LocalDate.now());
                repository.save(newTask);

            } else if (option == 2) {
                System.out.println("\n--- TUS TAREAS ---");
                if (repository.findAll().isEmpty()) {
                    System.out.println("No hay tareas pendientes.");
                } else {
                    for (Task t : repository.findAll()) {
                        System.out.println(t);
                    }
                }
            }
        }
        System.out.println("¡Suerte con el estudio! Cerrando programa...");
        scanner.close();
    }
}