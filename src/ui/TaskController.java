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
    @FXML private ComboBox<String> repeatComboBox;

    @FXML
    public void initialize() {
        // Inicialización de la vista
        showTodayTasks();

        // Configuración de celdas personalizadas
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

        // Opciones de recurrencia
        if (repeatComboBox != null) {
            repeatComboBox.getItems().setAll("Una vez", "Semanal (1 mes)", "Semanal (2 meses)");
            repeatComboBox.setValue("Una vez");
        }

        configurarEventos();
    }

    // --- LÓGICA DE VISTA (FILTROS) ---

    @FXML public void showTodayTasks() { updateTaskListView(Task::isDueToday); sideCalendar.setValue(null); }
    @FXML public void showAllTasks() { updateTaskListView(t -> true); sideCalendar.setValue(null); }
    @FXML public void showPendingTasks() { updateTaskListView(t -> !t.isCompleted()); }
    @FXML public void showCompletedTasks() { updateTaskListView(Task::isCompleted); }

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
        String repeatOption = (repeatComboBox != null) ? repeatComboBox.getValue() : "Una vez";

        if (esEntradaInvalida(title, date)) return;

        try {
            LocalTime time = rawTime.isBlank() ? LocalTime.MIN : LocalTime.parse(rawTime, timeFormatter);
            LocalDateTime baseDateTime = LocalDateTime.of(date, time);

            int repeticiones = 1;
            if (repeatOption.contains("1 mes")) repeticiones = 4;
            else if (repeatOption.contains("2 meses")) repeticiones = 8;


            for (int i = 0; i < repeticiones; i++) {
                LocalDateTime fechaIterada = baseDateTime.plusWeeks(i);
                Task nueva = new Task(0, title, "", fechaIterada);
                gestor.guardarTarea(nueva);
            }

            limpiarInputs();
            refrescarVistaActual();

        } catch (DateTimeParseException e) {
            mostrarAlerta("Error", "Formato de hora incorrecto (HH:mm).");
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

        Label lblRepeat = new Label("Programar repeticiones futuras:");
        lblRepeat.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        ComboBox<String> detailRepeatCombo = new ComboBox<>();
        detailRepeatCombo.getItems().addAll("No repetir", "Semanal (Próximo mes)", "Semanal (Próximos 2 meses)");
        detailRepeatCombo.setValue("No repetir");
        detailRepeatCombo.setMaxWidth(Double.MAX_VALUE);
        detailRepeatCombo.setStyle("-fx-background-color: #333; -fx-text-fill: white;");

        root.getChildren().addAll(lblCreada, lblMeta, new Separator(), new Label("Notas:"), txtObs, new Separator(), lblRepeat, detailRepeatCombo);
        dialog.getDialogPane().setContent(root);

        ButtonType btnSave = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnStatus = new ButtonType(task.isCompleted() ? "Reabrir" : "Completar", ButtonBar.ButtonData.LEFT);
        ButtonType btnDel = new ButtonType("Eliminar", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, btnStatus, btnDel, ButtonType.CANCEL);


        dialog.getDialogPane().setStyle("-fx-background-color: #2b2b2b;");
        dialog.getDialogPane().lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white;"));

        dialog.showAndWait().ifPresent(res -> {
            if (res == btnSave) {
                task.setDescription(txtObs.getText());
                gestor.actualizarTarea(task);


                String option = detailRepeatCombo.getValue();
                if (!option.equals("No repetir")) {
                    int semanas = option.contains("1 mes") ? 4 : 8;
                    for (int i = 1; i < semanas; i++) {
                        LocalDateTime nuevaFecha = task.getStartDateTime().plusWeeks(i);
                        Task copia = new Task(0, task.getTitle(), task.getDescription(), nuevaFecha);
                        gestor.guardarTarea(copia);
                    }
                }
            } else if (res == btnStatus) {
                gestor.cambiarEstadoCompletada(task.getId(), !task.isCompleted());
            } else if (res == btnDel) {
                confirmarEliminacion(task);
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
        title.setStyle(item.isCompleted() ? "-fx-text-fill: #7f8c8d; -fx-strikethrough: true;" : "-fx-text-fill: white; -fx-font-weight: bold;");

        Label info = new Label(String.format("[%s] %s", item.getStartDateTime().format(timeFormatter), item.getDueDate()));
        info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDel = new Button("🗑");
        btnDel.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-cursor: hand;");
        btnDel.setOnAction(e -> confirmarEliminacion(item));

        row.getChildren().addAll(dot, title, info, spacer, btnDel);
        return row;
    }

    private void confirmarEliminacion(Task item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Borrar tarea: \"" + item.getTitle() + "\"?");

        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: #2b2b2b;");
        dp.lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: white;"));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                gestor.eliminarTarea(item.getId());
                refrescarVistaActual();
            }
        });
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
        repeatComboBox.setValue("Una vez");
    }

    private void mostrarAlerta(String tit, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(tit);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}