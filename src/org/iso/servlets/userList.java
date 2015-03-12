package org.iso.servlets;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class userList extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url="jdbc:odbc:books";
            connection=DriverManager.getConnection(url); 
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        toClient.println("<!DOCTYPE HTML>");
        toClient.println("<html>");
        toClient.println("<head><title>Users</title></head>");
        toClient.println("<body>");
        toClient.println("<a href=\"index.html\">Home</A>");
        toClient.println("<h2>List of users</h2>");
        toClient.println("<table border='1'>");
        
        String sql = "Select ID, username, firstname, surname FROM users";
        System.out.println(sql);
        try {
            Statement statement=connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                toClient.println("<tr>");
                toClient.println("<td>" + result.getString("ID") + "</td>");
                toClient.println("<td>" + result.getString("username") + "</td>");
                toClient.println("<td>" + result.getString("firstname") + "</td>");
                toClient.println("<td>" + result.getString("surname") + "</td>");
                toClient.println("</tr>");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Resulset: " + sql + " Exception: " + e);
        }
        toClient.println("</table>");
        toClient.println("</body>");
        toClient.println("</html>");
        toClient.close();
    }
}