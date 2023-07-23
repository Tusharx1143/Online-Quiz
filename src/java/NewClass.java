/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tush
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class NewClass {
  public static Connection getConn() {
    Connection con = null;
    String loadDriver = "com.mysql.jdbc.Driver";
    String dbURL = "jdbc:mysql://localhost:3306/java";
    String dbUSERNAME = "root";
    String dbPASSWORD = "";
    try {
      Class.forName(loadDriver);
      con = DriverManager.getConnection(dbURL, dbUSERNAME, dbPASSWORD);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return con;
  }
}
