package ui;

import dao.TaskDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaskController {
    private final TaskDAO gestor = new TaskDAO();

    @FXML private TextField taskInputField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker sideCalendar;
    @FXML private ListView<Task> taskListView;

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
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);

                    Circle indicator = new Circle(6);
                    indicator.setFill(item.isCompleted() ? Color.LIMEGREEN : Color.TOMATO);

                    Label nameLabel = new Label(item.getTitle());
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                    String fechaStr = (item.getDueDate() != null) ? " [" + item.getDueDate() + "]" : "";
                    Label dateLabel = new Label(fechaStr);
                    dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button btnEliminar = new Button("🗑");
                    btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-cursor: hand;");
                    btnEliminar.setOnAction(e -> {
                        gestor.eliminarTarea(item.getId());
                        refrescarVistaActual();
                    });

                    container.getChildren().addAll(indicator, nameLabel, dateLabel, spacer, btnEliminar);
                    setGraphic(container);
                }
            }
        });

        taskListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Task seleccionada = taskListView.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    gestor.cambiarEstadoCompletada(seleccionada.getId(), !seleccionada.isCompleted());
                    refrescarVistaActual();
                }
            }
        });

        // Evento: Tecla Delete para borrar
        taskListView.setOnKeyPressed(event -> {
            Task seleccionada = taskListView.getSelectionModel().getSelectedItem();
            if (seleccionada != null && (event.getCode().name().equals("DELETE") || event.getCode().name().equals("BACK_SPACE"))) {
                gestor.eliminarTarea(seleccionada.getId());
                refrescarVistaActual();
            }
        });
    }

    // Método de utilidad para no repetir lógica de refresco
    private void refrescarVistaActual() {
        if (sideCalendar.getValue() != null) {
            handleCalendarSelection();
        } else {
            taskListView.getItems().setAll(gestor.obtenerTodas());
        }
    }

    @FXML
    private void handleCalendarSelection() {
        LocalDate selectedDate = sideCalendar.getValue();
        if (selectedDate != null) {
            List<Task> tareasDelDia = gestor.obtenerPorFecha(selectedDate);
            taskListView.getItems().setAll(tareasDelDia);
        }
    }

    @FXML
    public void handleAddTask() {
        String title = taskInputField.getText();
        LocalDate date = datePicker.getValue();

        if (title != null && !title.trim().isEmpty() && date != null) {
            LocalDateTime dateTime = date.atStartOfDay();
            Task nuevaTarea = new Task(0, title, "Sin descripción", dateTime);

            gestor.guardarTarea(nuevaTarea);
        }
    }

    @FXML public void showAllTasks() {
        sideCalendar.setValue(null);
        taskListView.getItems().setAll(gestor.obtenerTodas());
    }

    @FXML public void showPendingTasks() {
        sideCalendar.setValue(null);
        taskListView.getItems().setAll(gestor.obtenerTodas().stream().filter(t -> !t.isCompleted()).toList());
    }

    @FXML public void showCompletedTasks() {
        sideCalendar.setValue(null);
        taskListView.getItems().setAll(gestor.obtenerTodas().stream().filter(Task::isCompleted).toList());
    }
}