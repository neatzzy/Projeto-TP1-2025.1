import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/screens/TelaSimulacao.fxml")));

        primaryStage.setTitle("Tela de Simulacao");
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

        Clube clube = new Clube(conn, "Fluminense");
        clube.addJogador(conn, "Everaldo", Posicao.ATACANTE, 7.0, 76);
        clube.addJogador(conn, "Arias", Posicao.ATACANTE, 15.0, 80);
        clube.addJogador(conn, "K. Serna", Posicao.ATACANTE, 6.0, 75);
        clube.addJogador(conn, "Ganso", Posicao.MEIA, 13.0, 80);
        clube.addJogador(conn, "Martinelli", Posicao.MEIA, 7.0,77);
        clube.addJogador(conn, "Bernal", Posicao.MEIA, 7.0, 75);
        clube.addJogador(conn, "G. Fuentes", Posicao.ZAGUEIRO, 9.0, 75);
        clube.addJogador(conn, "Freytes", Posicao.ZAGUEIRO, 6.0, 72);
        clube.addJogador(conn, "T. Silva", Posicao.ZAGUEIRO, 14.0, 80);
        clube.addJogador(conn, "S. Xavier", Posicao.ZAGUEIRO, 11.0, 76);
        clube.addJogador(conn, "Fábio", Posicao.GOLEIRO, 14.0, 79);
        clube.addJogador(conn, "G. Cano", Posicao.ATACANTE, 13, 78);

        Clube clube2 = new Clube(conn, "Botafogo");
        clube2.addJogador(conn, "Igor Jesus", Posicao.ATACANTE, 14.0, 78);
        clube2.addJogador(conn, "J. Correa", Posicao.ATACANTE, 16.0, 78);
        clube2.addJogador(conn, "Artur", Posicao.ATACANTE, 14.0, 78);
        clube2.addJogador(conn, "Savarino", Posicao.MEIA, 16.0, 79);
        clube2.addJogador(conn, "Gregore", Posicao.MEIA, 13.0, 77);
        clube2.addJogador(conn, "Marlon Freitas", Posicao.MEIA, 15.0, 78);
        clube2.addJogador(conn, "Alex Telles", Posicao.ZAGUEIRO, 16.0, 80);
        clube2.addJogador(conn, "A. Barbosa", Posicao.ZAGUEIRO, 12.0, 78);
        clube2.addJogador(conn, "Jair", Posicao.ZAGUEIRO, 9.0, 73);
        clube2.addJogador(conn, "Cuiabano", Posicao.ZAGUEIRO, 12.0, 77);
        clube2.addJogador(conn, "John", Posicao.GOLEIRO, 12.0, 78);
        clube2.addJogador(conn, "Mastriani", Posicao.ATACANTE, 7.0, 76);
        
        Usuario pedro = new Usuario("pedro", "1234");
        TimeUsuario time = pedro.getTimeUsuario();

        for(Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        Usuario mauroPatrao = new Usuario("patrao", "1234");
        TimeUsuario time2 = mauroPatrao.getTimeUsuario();

        for(Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        for(Jogador jogador : clube2.getJogadores()){
            time2.addJogador(jogador);
            time2.setCapitao(jogador);
        }

        time.imprimirTime();
        time2.imprimirTime();

        Liga liga = new Liga("Cicartola");

        liga.addUsuario(pedro);
        liga.addUsuario(mauroPatrao);

        Simulacao.addPartida(clube, clube2);

        List<Clube> clubes = new ArrayList<>();
        clubes.add(clube);
        clubes.add(clube2);
        System.out.println("ataque over 1: " + clube.getOverAtaque());
        System.out.println("defesa over 1: " + clube.getOverDefesa());
        System.out.println("ataque over 2: " + clube2.getOverAtaque());
        System.out.println("defesa over 2: " + clube2.getOverDefesa());

        Simulacao.gerarPartidasAleatorias(clubes);

        liga.exibirRanking(liga.gerarRanking());
    }
}