package service;

import model.Task;
import repository.TaskRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    // 1. Lógica para añadir una tarea
    public void createTask(String title, String description, LocalDate dueDate) {

        int nextId = repository.findAll().size() + 1;
        Task newTask = new Task(nextId, title, description, dueDate);
        repository.save(newTask);
    }

    // 2. Lógica para completar una tarea
    public void markTaskAsCompleted(int id) {
        Task task = repository.findById(id);
        if (task != null) {
            task.setCompleted(true);
            repository.save(task); // El repo se encarga de actualizarla
        } else {
            System.out.println("Error: No se encontró la tarea con ID " + id);
        }
    }

    // 3. Obtener todas ordenadas por fecha de vencimiento (Útil para Algoritmos)
    public List<Task> getTasksOrderedByDate() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    // 4. Filtrar solo las pendientes
    public List<Task> getPendingTasks() {
        return repository.findAll().stream()
                .filter(t -> !t.isCompleted())
                .collect(Collectors.toList());
    }

    // 5. Eliminar tarea
    public void removeTask(int id) {
        repository.delete(id);
    }
}

