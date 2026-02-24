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

    public void createTask(String title, String description, LocalDate dueDate) {
        int nextId = repository.findAll().size() + 1;
        Task newTask = new Task(nextId, title, description, dueDate);
        repository.save(newTask);
    }

    public void markTaskAsCompleted(int id) {
        Task task = repository.findById(id);
        if (task != null) {
            task.setCompleted(true);
            repository.save(task);
        }
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public List<Task> getPendingTasks() {
        return repository.findAll().stream()
                .filter(t -> !t.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return repository.findAll().stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksOrderedByDate() {
        return repository.findAll().stream()
                .filter(t -> t.getDueDate() != null)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    public void removeTask(int id) {
        repository.delete(id);
    }
}