package repository;

import model.Task;

import java.util.List;

public interface TaskRepository {
    void save (Task Task);
    List<Task> findAll();
    void delete(int id);
    Task findById(int id);
}
