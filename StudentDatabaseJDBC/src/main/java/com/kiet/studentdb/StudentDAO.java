
package com.kiet.studentdb;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

@Repository
public class StudentDAO {

    private final DataSource dataSource;

    public StudentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Validate scores
    private boolean validScores(double p, double m) {
        return p >= 0 && p <= 100 && m >= 0 && m <= 100;
    }

    // Add student
    public String addStudent(Student s) {

        if (!validScores(s.getProgrammingScore(), s.getMathematicsScore()))
            return "Scores must be between 0 and 100";

        s.calculateAverage();
        s.calculatePerformance();
        s.calculateStrongerSubject();

        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, s.getStudentId());
            ps.setString(2, s.getStudentName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getCourse());
            ps.setDouble(5, s.getProgrammingScore());
            ps.setDouble(6, s.getMathematicsScore());

            ps.executeUpdate();
            return "Student added successfully";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "Duplicate student ID or email";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    // Retrieve all students
    public List<Student> getAllStudents() {

        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getDouble("programming_score"),
                    rs.getDouble("mathematics_score")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Search by ID, email or course
    public List<Student> search(String field, String value) {

        List<Student> list = new ArrayList<>();

        if (!field.equals("student_id") &&
            !field.equals("email") &&
            !field.equals("course")) {
            return list;
        }

        String sql = "SELECT * FROM students WHERE " + field + " = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getDouble("programming_score"),
                    rs.getDouble("mathematics_score")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Search student by ID
    public Student getStudentById(int id) {

        List<Student> list = search("student_id", String.valueOf(id));

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    // Merit list
    public List<Student> getMeritList() {

        List<Student> list = getAllStudents();

        list.sort((a, b) ->
            Double.compare(b.getAverage(), a.getAverage()));

        return list;
    }

    // Programming < 50 and Mathematics >= 75
    public List<Student> getLowProgrammingStudents() {

        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM students " +
                     "WHERE programming_score < 50 " +
                     "AND mathematics_score >= 75";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getDouble("programming_score"),
                    rs.getDouble("mathematics_score")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Common technical skills
    public List<Map<String, Object>> getCommonSkills() {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql = "SELECT skill, GROUP_CONCAT(student_id) AS student_ids " +
                     "FROM student_skills GROUP BY skill " +
                     "HAVING COUNT(DISTINCT student_id) > 1";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();

                map.put("skill", rs.getString("skill"));
                map.put("studentIds", rs.getString("student_ids"));

                list.add(map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update student scores and skills
    public String updateStudent(int id, StudentUpdateRequest request) {

        double p = request.getProgrammingScore();
        double m = request.getMathematicsScore();

        if (!validScores(p, m))
            return "Scores must be between 0 and 100";

        if (request.getSkills() == null)
            return "Skills are required";

        String updateSQL =
            "UPDATE students SET programming_score=?, mathematics_score=? WHERE student_id=?";

        String deleteSQL =
            "DELETE FROM student_skills WHERE student_id=?";

        String insertSQL =
            "INSERT INTO student_skills(student_id, skill) VALUES (?, ?)";

        try (Connection con = getConnection()) {

            con.setAutoCommit(false);

            try {
                int rows;

                try (PreparedStatement ps = con.prepareStatement(updateSQL)) {
                    ps.setDouble(1, p);
                    ps.setDouble(2, m);
                    ps.setInt(3, id);
                    rows = ps.executeUpdate();
                }

                if (rows == 0) {
                    con.rollback();
                    return "Student not found";
                }

                try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                    for (String skill : request.getSkills()) {
                        ps.setInt(1, id);
                        ps.setString(2, skill);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return "Student scores and skills updated successfully";

            } catch (SQLException e) {
                con.rollback();
                return e.getMessage();
            }

        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    // Delete student
    public String deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE student_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                return "Student deleted successfully";

            return "Student not found";

        } catch (SQLException e) {
            return e.getMessage();
        }
    }
}
