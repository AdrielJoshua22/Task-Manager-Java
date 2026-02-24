package ui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Task;
import service.TaskService;
import repository.impl.InMemoryTaskRepository;
import java.time.LocalDate;

public class TaskController {
    @FXML private TextField taskInputField;
    @FXML private DatePicker datePicker;
    @FXML private ListView<Task> taskListView;

    private final TaskService taskService = new TaskService(new InMemoryTaskRepository());

    @FXML
    public void initialize() {
        showAllTasks();

        taskListView.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    javafx.scene.shape.Circle indicator = new javafx.scene.shape.Circle(6);

                    if (item.isCompleted()) {
                        indicator.setFill(javafx.scene.paint.Color.LIMEGREEN);
                    } else {
                        indicator.setFill(javafx.scene.paint.Color.TOMATO);
                    }

                    javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(10);
                    javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(item.getId() + ". " + item.getTitle());
                    nameLabel.setStyle("-fx-text-fill: white;");

                    container.getChildren().addAll(indicator, nameLabel);
                    setGraphic(container);
                }
            }
        });

        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Task seleccionada = taskListView.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    taskService.markTaskAsCompleted(seleccionada.getId());
                    taskListView.refresh();
                }
            }
        });
    }

    @FXML
    public void handleAddTask() {
        String title = taskInputField.getText();
        LocalDate date = datePicker.getValue();

        if (title != null && !title.trim().isEmpty()) {
            taskService.createTask(title, "Sin descripción", date);
            taskInputField.clear();
            datePicker.setValue(null);
            showAllTasks();
        }
    }

    @FXML
    public void showAllTasks() {
        taskListView.getItems().setAll(taskService.getAllTasks());
    }

    @FXML
    public void showPendingTasks() {
        taskListView.getItems().setAll(taskService.getPendingTasks());
    }

    @FXML
    public void showCompletedTasks() {
        taskListView.getItems().setAll(taskService.getCompletedTasks());
    }

    private void actualizarVistaSegunFiltro() {
        taskListView.refresh();
    }
}