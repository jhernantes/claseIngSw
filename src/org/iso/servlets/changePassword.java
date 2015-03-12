package org.iso.servlets;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class changePassword extends HttpServlet {
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

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String id = null;
        if (session != null) {
            id = (String)session.getAttribute("id");
        }
        if (session == null || id == null) {
            res.getWriter().println("<h1> You must register to change password </h1>" +
              "<a href='index.html'>Home</a>");
            return;
        }
        String oldPassword = req.getParameter("oldpassword");
        String newPassword = req.getParameter("newpassword");

        if(oldPassword==null) {
            System.out.println("Problem reading old password from request");
            return;
        }
        if(newPassword==null) {
          System.out.println("Problem reading new password from request");
          return;
        }
        Statement stmt = null;
        try {
            stmt=connection.createStatement();
            ResultSet rs = null;
            String sql = "SELECT ID, password FROM users WHERE ID = " + id;
            System.out.println(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next() ) {
                String password = rs.getString("password");
                System.out.println("password: " + password);
                if (!password.equals(oldPassword)) {
                    Error_login(req, res);
                    return;
                } else {
                    Statement stmt2 = null;
                    stmt2=connection.createStatement();
                    String sqlUpdate = "UPDATE users SET password='" + newPassword + "' WHERE ID = " + id;
                    System.out.println(sqlUpdate);
                    int registers = stmt2.executeUpdate(sqlUpdate);
                    System.out.println("Updated " + registers + " registers.");
                    res.sendRedirect("bookList");
                    return;
                }
            } else {
                System.out.println("No registers for this id: " + id);
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
        out.println("<!DOCTYPE HTML>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Error in change password</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<BR>");
        out.println("<H2 align=\"center\">Old password incorrect</H2>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

}
