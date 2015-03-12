package org.iso.servlets;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class opinionList extends HttpServlet {
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
        String usernameStr = req.getParameter("username");
        String id = null;
        if (usernameStr == null) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                id = (String)session.getAttribute("id");
                usernameStr = id;
            }
            if (session == null || id == null) {
                res.getWriter().println("<h1> You must register to see opinions </h1>" +
                  "<a href='index.html'>Home</a>");
                return;
            }
        }

        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        toClient.println("<!DOCTYPE HTML>");
        toClient.println("<html>");
        toClient.println("<head><title>Opinions</title></head>");
        toClient.println("<body>");
        toClient.println("<a href=\"index.html\">Home</A>");
        toClient.println("<h2>List of opinions</h2>");
        toClient.println("<table border=\"1\">");

        String sql = "SELECT title, user, description, title FROM opinions, books WHERE opinions.code = books.code AND user='" + usernameStr + "'";
        System.out.println(sql);
        try {
            Statement statement=connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()) {
                toClient.println("<tr>");            
                String titleStr = result.getString("title");
                String opinionStr = result.getString("description");
                toClient.println("<td>" + titleStr + "</td>");
                toClient.println("<td>" + opinionStr + "</td>");
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
