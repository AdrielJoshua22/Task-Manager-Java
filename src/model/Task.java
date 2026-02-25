package model;
import java.time.LocalDate;

public class Task {

    //DEFINO LA ESTRUCTURA.

    private int id;
    private String title ;
    private String description;
    private boolean completed;
    private LocalDate dueDate;

    // CONSTRUCTOR.

    public Task (int id, String title, String description, LocalDate dueDate){
            if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("El Titulo no puede estar vacio");
            }
            this.id = id;
            this.title = title;
            this.description = description;
            this.completed = completed;
            this.dueDate = dueDate;

        }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public void setDescription(String description) {this.description = description;}
    public boolean isCompleted() {return completed;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}
    public void setCompleted(boolean completed) {this.completed = completed;}
    public LocalDate getDueDate() {return dueDate;}

    @Override
    public String toString() {
        String estadoTexto = completed ? "COMPLETADA" : "PENDIENTE";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateText = (dueDate != null) ? dueDate.format(formatter) : "Sin fecha";
        return id + ". [" + estadoTexto + "] " + title + " (Vence: " + dateText + ")";
    }

    }




