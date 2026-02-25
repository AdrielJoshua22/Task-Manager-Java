import model.Task;
import dao.TaskDAO;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Task miPrimeraTarea = new Task(1, "Conectar MySQL", "Logré configurar el DAO", LocalDate.now());
        TaskDAO gestor = new TaskDAO();
        System.out.println("Intentando guardar tarea...");
        gestor.guardarTarea(miPrimeraTarea);
    }
}