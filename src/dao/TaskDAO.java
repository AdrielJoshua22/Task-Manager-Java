package dao;

import database.ConexionDB;
import model.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public void guardarTarea(Task tarea) {
        String sql = "INSERT INTO tasks (id, titulo, descripcion, due_date, completada) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tarea.getId());
            pstmt.setString(2, tarea.getTitle());
            pstmt.setString(3, "Sin descripción");

            // Convertimos LocalDate a java.sql.Date
            if (tarea.getDueDate() != null) {
                pstmt.setDate(4, Date.valueOf(tarea.getDueDate()));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }

            pstmt.setBoolean(5, tarea.isCompleted());

            pstmt.executeUpdate();
            System.out.println("Tarea '" + tarea.getTitle() + "' guardada con éxito.");

        } catch (SQLException e) {
            System.out.println("Error al insertar en MySQL: " + e.getMessage());
        }
    }

    public List<Task> obtenerTodas() {
        List<Task> lista = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection con = ConexionDB.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                String desc = rs.getString("descripcion");
                LocalDate fecha = (rs.getDate("due_date") != null) ? rs.getDate("due_date").toLocalDate() : null;
                boolean completada = rs.getBoolean("completada");

                Task t = new Task(id, titulo, desc, fecha);
                t.setCompleted(completada);
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Error al leer de MySQL: " + e.getMessage());
        }
        return lista;
    }

    public void marcarComoCompletada(int id) {
        String sql = "UPDATE tasks SET completada = true WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Tarea #" + id + " marcada como completada en MySQL.");

        } catch (SQLException e) {
            System.out.println("Error al actualizar tarea: " + e.getMessage());
        }
    }
}