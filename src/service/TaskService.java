package service;

import dao.TaskDAO;
import model.Task;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    private final TaskDAO taskDAO;

    public TaskService() {
        this.taskDAO = new TaskDAO();
    }

    public void createTask(String title, String description, LocalDateTime startDateTime) {
        Task newTask = new Task(0, title, description, startDateTime);
        taskDAO.guardarTarea(newTask);
    }

    public List<Task> getAllTasks() {
        return taskDAO.obtenerTodas();
    }

    public List<Task> getPendingTasks() {
        return getAllTasks().stream()
                .filter(t -> !t.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return getAllTasks().stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksOrderedByDate() {
        return getAllTasks().stream()
                .filter(t -> t.getStartDateTime() != null)
                .sorted(Comparator.comparing(Task::getStartDateTime))
                .collect(Collectors.toList());
    }

    public void markTaskAsCompleted(int id) {
        taskDAO.cambiarEstadoCompletada(id, true);
    }
}