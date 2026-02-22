package repository.impl;

import model.Task;
import repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskRepository implements TaskRepository {

    private List<Task> tasks = new ArrayList<>();

    @Override
    public void save(Task task) {
        tasks.add(task);
        System.out.println("Tarea guardada: " + task.getTitle());
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void delete(int id) {
        tasks.removeIf(t -> t.getId() == id);
    }

    @Override
    public Task findById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}