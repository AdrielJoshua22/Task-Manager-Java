package dao;

import database.ConexionDB;
import model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private static final String INSERT_SQL = "INSERT INTO tasks (titulo, descripcion, due_date, completada) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM tasks";
    private static final String SELECT_BY_DATE_SQL = "SELECT * FROM tasks WHERE DATE(due_date) = ?";
    private static final String UPDATE_COMPLETED_SQL = "UPDATE tasks SET completada = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM tasks WHERE id = ?";

    public void guardarTarea(Task tarea) {
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(INSERT_SQL)) {

            pstmt.setString(1, tarea.getTitle());
            pstmt.setString(2, tarea.getDescription());

            if (tarea.getStartDateTime() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(tarea.getStartDateTime()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            pstmt.setBoolean(4, tarea.isCompleted());
            pstmt.executeUpdate();
            System.out.println(" Tarea '" + tarea.getTitle() + "' guardada con éxito.");
        } catch (SQLException e) {
            logError("Error al guardar tarea", e);
        }
    }

    public List<Task> obtenerPorFecha(LocalDate fecha) {
        List<Task> tareasDelDia = new ArrayList<>();
        // Usamos DATE(due_date) para comparar solo la parte de la fecha en SQL
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(SELECT_BY_DATE_SQL)) {

            pstmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tareasDelDia.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            logError("Error al filtrar por fecha", e);
        }
        return tareasDelDia;
    }

    public List<Task> obtenerTodas() {
        List<Task> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                lista.add(mapResultSetToTask(rs));
            }
        } catch (SQLException e) {
            logError("Error al leer todas las tareas", e);
        }
        return lista;
    }

    public void cambiarEstadoCompletada(int id, boolean estado) {
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(UPDATE_COMPLETED_SQL)) {

            pstmt.setBoolean(1, estado);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logError("Error al actualizar estado", e);
        }
    }

    public void eliminarTarea(int id) {
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("🗑️ Tarea #" + id + " eliminada.");
        } catch (SQLException e) {
            logError("Error al eliminar tarea", e);
        }
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String desc = rs.getString("descripcion");

        Timestamp ts = rs.getTimestamp("due_date");
        LocalDateTime fechaHora = (ts != null) ? ts.toLocalDateTime() : null;

        boolean completada = rs.getBoolean("completada");

        Task t = new Task(id, titulo, desc, fechaHora);
        t.setCompleted(completada);
        return t;
    }

    private void logError(String mensaje, Exception e) {
        System.err.println("X " + mensaje + ": " + e.getMessage());
    }
}