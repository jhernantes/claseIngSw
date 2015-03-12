package org.iso.servlets;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class bookOpinion extends HttpServlet {
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

        HttpSession session = req.getSession(false);
        String id = null;
        if (session != null) {
            id = (String)session.getAttribute("id");
        }
        if (session == null || id == null) {
            res.getWriter().println("<h1> You must register to write opinions </h1>" +
              "<a href='index.html'>Home</a>");
            return;
        }
        
        String sql = "INSERT INTO opinions (code, user, description) VALUES (";	           
        sql +=  "'" + req.getParameter("book") + "'";
        sql +=  ", '" + id + "'";
        sql +=  ", '" + req.getParameter("comment") + "')";
        System.out.println("Insert sql: " + sql);
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Resulset: " + sql + " Exception: " + e);
        }

        res.setContentType("text/html");
        PrintWriter toClient = res.getWriter();
        
        toClient.println("<!DOCTYPE HTML>");
        toClient.println("<html>");
        toClient.println("<head><title>Opinions</title></head>");
        toClient.println("<body>");
        toClient.println("<a href=\"index.html\">Home</A>");
        toClient.println("<h2>Opinions</h2>");

        toClient.println("Opinion registered:");
        toClient.println(req.getParameter("comment"));
        toClient.println("</body>");
        toClient.println("</html>");
        toClient.close();
    }
}
