package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    // Estabelece conexão com o banco de dados
    public Connection connectToDb(String dbname, String user, String pass) {
        Connection conn=null;
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://dpg-d161kqmmcj7s73dtqs7g-a.oregon-postgres.render.com:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            } else {
                System.out.println("Connection Failed");
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return conn;
    }

    public void deleteTable(Connection conn, String table_name){
        String deleteQuery = "DROP TABLE IF EXISTS " + table_name;
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(deleteQuery);
            System.out.println("Table " + table_name + " deleted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
