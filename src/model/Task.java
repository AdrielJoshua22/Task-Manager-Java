package model;

public class Task {
    private String title ;
    private int priority;

    public Task (String title, int priority){
            if(priority <1|| priority >5 ){
            throw new IllegalArgumentException("La Prioridad de tarea debe estar en un rango de 1 a 5 ");
            }
            if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("El Titulo no puede estar vacio");
            }
            this.title = title;
            this.priority = priority;
        }

        public String getTitle(){return title;}
        public int getPriority(){return priority;}

    @Override
    public String toString(){
        return  "Tarea:  " +title+ " | Prioridad: " + priority;
    }

    }


