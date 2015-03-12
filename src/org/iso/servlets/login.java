package org.iso.servlets;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class login extends HttpServlet {
    Connection connection;
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String url="jdbc:odbc:books";
            connection=DriverManager.getConnection(url); 
        } catch(Exception e) {
            System.out.println("Problem creating connection");
            e.printStackTrace();
        }
    }

    public void destroy () {
        super.destroy();
        System.out.print("Closing connection ...");
        try {
            connection.close();
            System.out.println("Connection closed");
        } catch(SQLException ex){
            System.out.println("Problem closing connection");
            System.out.println(ex.getMessage());
        }
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if(username==null) {
            System.out.println("Problem reading username from request");
            return;
        }
        if(password==null) {
          System.out.println("Problem reading password from request");
          return;
        }
        Statement stmt = null;
        try {
            stmt=connection.createStatement();
            ResultSet rs = null;
            rs = stmt.executeQuery("SELECT ID, username, password, surname, firstname FROM users WHERE username = '"
               + username + "' AND password = '" + password + "'");

            if(rs.next()==false){
                Error_login(req, resp);
                return;
            } else {
                HttpSession session = req.getSession(true);
                String name = rs.getString("firstname") + " " + rs.getString("surname");
                session.setAttribute("name", name);
                session.setAttribute("username", username);
                session.setAttribute("id", rs.getString("ID"));
                resp.sendRedirect("bookList");
                return;
            }
        } catch (SQLException sql) {
            System.out.println("Error creating Statement");
            System.out.println(sql.getMessage());
            return;
        } finally {      
            if(stmt!=null) {
                try {
                    stmt.close();
                } catch(SQLException e) {
                    System.out.println("Error closing Statement");
                    System.out.println(e.getMessage());
                    return;
                }
            }
        } 
    } 

    public void Error_login(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html");
        PrintWriter out=null;
        try {
            out=resp.getWriter();
        } catch (IOException io) {
            System.out.println("Error opening PrintWriter");
        }
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Error in login</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<BR>");
        out.println("<H2 align=\"center\">Username or password incorrect</H2>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

}
