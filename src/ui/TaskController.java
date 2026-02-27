package ui;

import dao.TaskDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Predicate;

public class TaskController {
    private final TaskDAO gestor = new TaskDAO();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TextField taskInputField, timeInputField;
    @FXML private DatePicker datePicker, sideCalendar;
    @FXML private ListView<Task> taskListView;

    @FXML
    public void initialize() {
        showTodayTasks();

        taskListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(createTaskRow(item));
                }
            }
        });

        configurarEventos();
    }

    // --- LÓGICA DE VISTA (FILTROS) ---

    @FXML
    public void showTodayTasks() {
        updateTaskListView(Task::isDueToday);
        sideCalendar.setValue(null);
    }

    @FXML
    public void showAllTasks() {
        updateTaskListView(t -> true);
        sideCalendar.setValue(null);
    }

    @FXML
    public void showPendingTasks() {
        updateTaskListView(t -> !t.isCompleted());
    }

    @FXML
    public void showCompletedTasks() {
        updateTaskListView(Task::isCompleted);
    }

    @FXML
    private void handleCalendarSelection() {
        LocalDate date = sideCalendar.getValue();
        if (date != null) {
            taskListView.getItems().setAll(gestor.obtenerPorFecha(date));
        }
    }

    private void updateTaskListView(Predicate<Task> filter) {
        List<Task> tasks = gestor.obtenerTodas().stream().filter(filter).toList();
        taskListView.getItems().setAll(tasks);
    }

    // --- ACCIONES DE TAREA ---

    @FXML
    public void handleAddTask() {
        String title = taskInputField.getText();
        LocalDate date = datePicker.getValue();
        String rawTime = timeInputField.getText();

        if (esEntradaInvalida(title, date)) return;

        try {
            LocalTime time = rawTime.isBlank() ? LocalTime.MIN : LocalTime.parse(rawTime, timeFormatter);
            Task nueva = new Task(0, title, "", LocalDateTime.of(date, time));

            gestor.guardarTarea(nueva);
            limpiarInputs();
            refrescarVistaActual();
        } catch (DateTimeParseException e) {
            mostrarAlerta("Formato de hora incorrecto", "Usa HH:mm (ej: 14:30)");
        }
    }

    private void mostrarDetalleTarea(Task task) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Detalle: " + task.getTitle());

        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: #252525; -fx-padding: 20; -fx-min-width: 400;");

        Label lblCreada = new Label("📅 Creada: " + task.getCreatedAt().format(dateTimeFormatter));
        Label lblMeta = new Label("🎯 Programada: " + task.getStartDateTime().format(dateTimeFormatter));
        lblCreada.setStyle("-fx-text-fill: #7f8c8d;");
        lblMeta.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        TextArea txtObs = new TextArea(task.getDescription());
        txtObs.setPromptText("Añadir observaciones...");
        txtObs.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: white;");

        root.getChildren().addAll(lblCreada, lblMeta, new Separator(), new Label("Notas:"), txtObs);
        dialog.getDialogPane().setContent(root);


        ButtonType btnSave = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnStatus = new ButtonType(task.isCompleted() ? "Reabrir" : "Completar", ButtonBar.ButtonData.LEFT);
        ButtonType btnDel = new ButtonType("Eliminar", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, btnStatus, btnDel, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == btnSave) {
                task.setDescription(txtObs.getText());
                gestor.actualizarTarea(task);
            } else if (res == btnStatus) {
                gestor.cambiarEstadoCompletada(task.getId(), !task.isCompleted());
            } else if (res == btnDel) {
                gestor.eliminarTarea(task.getId());
            }
            refrescarVistaActual();
        });
    }

    // --- CONSTRUCCIÓN DE UI ---

    private HBox createTaskRow(Task item) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(5, item.isCompleted() ? Color.LIMEGREEN : Color.TOMATO);
        Label title = new Label(item.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label info = new Label(String.format("[%s] %s",
                item.getStartDateTime().format(timeFormatter),
                item.getDueDate()));
        info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDel = new Button("🗑");
        btnDel.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c;");
        btnDel.setOnAction(e -> { gestor.eliminarTarea(item.getId()); refrescarVistaActual(); });

        row.getChildren().addAll(dot, title, info, spacer, btnDel);
        return row;
    }

    // --- UTILIDADES ---

    private void configurarEventos() {
        taskListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Task t = taskListView.getSelectionModel().getSelectedItem();
                if (t != null) mostrarDetalleTarea(t);
            }
        });
    }

    private void refrescarVistaActual() {
        if (sideCalendar.getValue() != null) handleCalendarSelection();
        else showTodayTasks();
    }

    private boolean esEntradaInvalida(String t, LocalDate d) {
        if (t == null || t.isBlank() || d == null) {
            mostrarAlerta("Atención", "Título y Fecha son obligatorios.");
            return true;
        }
        return false;
    }

    private void limpiarInputs() {
        taskInputField.clear();
        timeInputField.clear();
        datePicker.setValue(null);
    }

    private void mostrarAlerta(String tit, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(tit);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}