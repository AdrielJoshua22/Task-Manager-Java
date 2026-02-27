package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private int id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime startDateTime;
    private LocalDateTime createdAt;
    private String duration;

    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Task(int id, String title, String description, LocalDateTime startDateTime, LocalDateTime createdAt) {
        validarTitulo(title);
        this.id = id;
        this.title = title;
        this.description = (description == null) ? "" : description;
        this.startDateTime = startDateTime;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
        this.completed = false;
    }

    public Task(int id, String title, String description, LocalDateTime startDateTime) {
        this(id, title, description, startDateTime, LocalDateTime.now());
    }

    private void validarTitulo(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
    }

    // --- Getters y Setters ---

    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        validarTitulo(title);
        this.title = title;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public LocalDate getDueDate() {
        return (startDateTime != null) ? startDateTime.toLocalDate() : null;
    }

    public boolean isDueToday() {
        LocalDate date = getDueDate();
        return date != null && date.equals(LocalDate.now());
    }

    @Override
    public String toString() {
        String estado = completed ? "✅" : "⏳";
        String fechaHora = (startDateTime != null) ? startDateTime.format(FULL_FORMATTER) : "Sin fecha";
        return String.format("%d. %s %s (%s)", id, estado, title, fechaHora);
    }
}