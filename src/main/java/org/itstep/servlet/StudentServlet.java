package org.itstep.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class StudentServlet extends HttpServlet {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/academy" +
            "?characterEncoding=UTF-8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private Connection connection;
    private String errorConnection;

    @Override
    public void init() {
        connectDb(); // подключение к б/д
    }

    private synchronized void connectDb() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            errorConnection = e.getLocalizedMessage();
        }
    }

    /**
     * Displays table with all existed students in data base and provide form for addition of new student
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        PrintWriter out = resp.getWriter();

        out.println("<!doctype html>" +
                "<html>" +
                "   <head>" +
                "       <title>Jdbc and Servlets lesson</title>" +
                "       <meta charset='utf-8'/>" +
                "       <link rel='stylesheet' href='/resources/css/style.css' />" +
                "   </head>" +
                "<body>");

        out.println("<h1>Students</h1>");

        if (connection == null) {
            connectDb();
        }

        if (connection == null) {
            // нет подключения к б/д
            out.println("<p style='color: red'>Error connection: " + errorConnection + "</p>");
        } else {
            // получаем данные с б/д
            try {
                if (connection != null && !connection.isClosed()) {
                    Statement stmt = connection.createStatement();
                    printStudentsTable(out);
                    printStudentsAdditionForm(out, stmt);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        out.print("</body>" +
                "</html>");
    }

    /**
     * Adds a student to a data base and return a page with all existed students and
     * an empty form for adding new student
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        int age = Integer.parseInt(req.getParameter("age"));
        String email = req.getParameter("email");
        String groupName = req.getParameter("group_name");
        try {
            if (connection != null && !connection.isClosed()) {
                //Get group id by group name from the data base
                Statement stmt = connection.createStatement();
                String sqlGroup = String.format("SELECT*FROM `group` WHERE name='%s'", groupName);
                ResultSet resultSetGroup = stmt.executeQuery(sqlGroup);
                resultSetGroup.next();
                int groupId = resultSetGroup.getInt("id");

                //Addition of a student to the data base
                String sql = String.format("INSERT INTO student(first_name,last_name,age,email,group_id) " +
                        "VALUES('%s','%s','%d','%s','%d')", firstName, lastName, age, email, groupId);
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        doGet(req, resp);
    }

    /**
     * Creates a table with all existed students
     *
     * @param out - stream of output
     */
    private synchronized void printStudentsTable(PrintWriter out) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement stmt = connection.createStatement();

            //Creating of string of sql request to get information about students
            // which include name of group for each student
            String sqlStudent = "SELECT student.id, first_name, last_name, age, email,`group`.name FROM student " +
                    "INNER JOIN `group` ON student.group_id=`group`.id";
            ResultSet resultSetStudent = stmt.executeQuery(sqlStudent);

            //Displaying table with information about all existed students
            out.println("<table>");
            out.println(" <thead>");
            out.println("     <tr>");
            out.println("         <th>Id</th>");
            out.println("         <th>First name</th>");
            out.println("         <th>Last name</th>");
            out.println("         <th>Age</th>");
            out.println("         <th>Email</th>");
            out.println("         <th>Group</th>");
            out.println("     </tr>");
            out.println(" </thead>");
            out.println(" <tbody>");
            while (resultSetStudent.next()) {
                out.println(" <tr>");
                out.format("     <td>%d</td>%n", resultSetStudent.getInt("id"));
                out.format("     <td>%s</td>%n", resultSetStudent.getString("first_name"));
                out.format("     <td>%s</td>%n", resultSetStudent.getString("last_name"));
                out.format("     <td>%s</td>%n", resultSetStudent.getString("age"));
                out.format("     <td>%s</td>%n", resultSetStudent.getString("email"));
                out.format("     <td>%s</td>%n", resultSetStudent.getString("group.name"));
                out.println(" </tr>");
            }
            out.println(" </tbody>");
            out.println(" </table>");
        }
    }

    /**
     * Creates a form for adding students
     */
    private synchronized void printStudentsAdditionForm(PrintWriter out, Statement stmt) throws SQLException {
        //Creating of string of sql request to get all existed groups
        String sqlGroup = "SELECT name FROM `group`";
        ResultSet resultSetGroup = stmt.executeQuery(sqlGroup);

        //Displaying form to add a student
        out.println("<h2>Add new Student:</h2>");
        out.println("<form method='post'>");
        out.println("<input type='text' name='first_name' placeholder='Enter a first name'>");
        out.println("<input type='text' name='last_name' placeholder='Enter a last name'>");
        out.println("<input type='text' name='age' placeholder='Enter an age'>");
        out.println("<input type='text' name='email' placeholder='Enter an email'>");
        out.println("<select name='group_name'>");
        out.println("<option selected disabled>Choose a group</option>");
        while (resultSetGroup.next()) {
            out.printf("<option >%s</option>", resultSetGroup.getString("name"));
        }
        out.println("</select>");
        out.println("<input type='submit' value='submit'>");
        out.println("</form>");
    }

    @Override
    public void destroy() {
        disconnectDb();
    }

    private void disconnectDb() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
