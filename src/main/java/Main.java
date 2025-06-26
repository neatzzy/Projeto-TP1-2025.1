import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


public class Main extends Application {

    // código para testar tela!
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/screens/TelaMercado.fxml")));

        primaryStage.setTitle("Tela do Mercado");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {

        launch(args); // roda as telas

        System.out.println("Hello, World!");

        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db(db_name, user, pass);

        List<Clube> clubes = db.getAllCLubes(conn);
        db.getAllJogadores(conn, clubes);

        Collections.shuffle(clubes);
        
        Usuario pedro = new Usuario("pedro", "1234");
        TimeUsuario time = pedro.getTimeUsuario();

        for(Jogador jogador : clubes.get(0).getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        Usuario mauroPatrao = new Usuario("patrao", "1234");
        TimeUsuario time2 = mauroPatrao.getTimeUsuario();

        for(Jogador jogador : clubes.get(0).getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        for(Jogador jogador : clubes.get(1).getJogadores()){
            time2.addJogador(jogador);
            time2.setCapitao(jogador);
        }

        time.imprimirTime();
        time2.imprimirTime();

        Liga liga = new Liga("Cicartola");

        liga.addUsuario(pedro);
        liga.addUsuario(mauroPatrao);

        Simulacao.addPartida(clubes.get(0), clubes.get(1));

        System.out.println("ataque over 1: " + clubes.get(0).getOverAtaque());
        System.out.println("defesa over 1: " + clubes.get(0).getOverDefesa());
        System.out.println("ataque over 2: " + clubes.get(1).getOverAtaque());
        System.out.println("defesa over 2: " + clubes.get(1).getOverDefesa());

        Simulacao.gerarPartidasAleatorias(clubes);
        Simulacao.simular(liga);

        liga.exibirRanking(liga.gerarRanking());
    }
}