package service;

import dao.TaskDAO;
import model.Task;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarService {
    private TaskDAO taskDAO = new TaskDAO();

    public void mostrarCalendarioMesActual() {
        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);

        System.out.println("\n--- CALENDARIO: " + mesActual.getMonth() + " " + mesActual.getYear() + " ---");
        System.out.println(" Lun  Mar  Mie  Jue  Vie  Sab  Dom ");

        LocalDate primerDia = mesActual.atDay(1);
        int desplazamiento = primerDia.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < desplazamiento; i++) {
            System.out.print("     ");
        }

        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fechaEvaluar = mesActual.atDay(dia);
            List<Task> tareasDelDia = taskDAO.obtenerPorFecha(fechaEvaluar);

            String marca = tareasDelDia.isEmpty() ? "" : "*";
            System.out.printf(" %2d%s ", dia, marca);

            if ((dia + desplazamiento) % 7 == 0) System.out.println();
        }
        System.out.println("\n------------------------------------------");
        System.out.println("(*) Indica días con tareas pendientes.");
    }
}