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
    private String duration;

    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Task(int id, String title, String description, LocalDateTime startDateTime) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.completed = false;
    }

    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) this.title = title;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

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
        String duracionStr = (duration != null && !duration.isEmpty()) ? " - Duración: " + duration : "";

        return String.format("%d. %s %s (%s)%s", id, estado, title, fechaHora, duracionStr);
    }
}