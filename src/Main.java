import java.sql.Connection;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db(db_name, user, pass);

    }
}