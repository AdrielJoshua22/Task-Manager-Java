import dao.TaskDAO;
import model.Task;
import service.CalendarService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskDAO taskDAO = new TaskDAO();
        CalendarService calendarService = new CalendarService();
        Scanner scanner = new Scanner(System.in);
        int opcion;

        System.out.println("=== BIENVENIDO A TASKAPP ===");

        do {
            System.out.println("\n--- PANEL PRINCIPAL ---");
            System.out.println("1. Ver todas las tareas");
            System.out.println("2. Ver Calendario y Panel Lateral");
            System.out.println("3. Crear nueva tarea con horario");
            System.out.println("4. Marcar tarea como completada");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    taskDAO.obtenerTodas().forEach(System.out::println);
                    break;

                case 2:
                    calendarService.mostrarCalendarioMesActual();
                    System.out.print("\nIngrese el día para ver detalle (0 para volver): ");
                    int diaBusqueda = scanner.nextInt();
                    if (diaBusqueda != 0) {
                        LocalDate fecha = LocalDate.now().withDayOfMonth(diaBusqueda);
                        List<Task> tareas = taskDAO.obtenerPorFecha(fecha);
                        System.out.println("\n--- TAREAS DEL DÍA " + diaBusqueda + " ---");
                        if (tareas.isEmpty()) System.out.println("No hay tareas.");
                        else tareas.forEach(System.out::println);
                    }
                    break;

                case 3:
                    System.out.print("Título: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Descripción: ");
                    String desc = scanner.nextLine();
                    System.out.print("Día (número): ");
                    int d = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Hora (formato HH:mm, ej 10:30): ");
                    String horaStr = scanner.nextLine();

                    try {
                        LocalTime hora = LocalTime.parse(horaStr.isEmpty() ? "00:00" : horaStr);
                        LocalDateTime fechaHora = LocalDateTime.of(LocalDate.now().withDayOfMonth(d), hora);
                        Task nueva = new Task(0, titulo, desc, fechaHora);

                        System.out.print("Duración estimada (ej: 1h 30m) o Enter para saltar: ");
                        String duracion = scanner.nextLine();
                        if(!duracion.isEmpty()) nueva.setDuration(duracion);

                        taskDAO.guardarTarea(nueva);
                    } catch (DateTimeParseException e) {
                        System.out.println(" Formato de hora inválido. Usa HH:mm.");
                    }
                    break;

                case 4:
                    System.out.print("ID de la tarea a completar: ");
                    int id = scanner.nextInt();
                    taskDAO.cambiarEstadoCompletada(id, true);
                    break;

                case 0:
                    System.out.println("¡Hasta luego!");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);

        scanner.close();
    }
}