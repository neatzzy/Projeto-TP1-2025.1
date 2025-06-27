package app;

import java.sql.Connection;
import java.sql.SQLException;

import controller.ControllerTelaClubesUsuario;
import controller.ControllerTelaInicio;

import controller.ControllerTelaVisualizarClube;
import dao.ClubeDAO;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import database.Database;
import model.Clube;


public class Main extends Application {

    // código para testar tela!
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Conexão com banco de dados
        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        // Funções do banco de dados
        Database db = new Database();
        Connection conn = db.connectToDb(db_name, user, pass);

        ClubeDAO clubeDAO = new ClubeDAO(conn);

        Clube clube = clubeDAO.getClubeById(5); // Obtém o clube pelo ID, por exemplo, 5

        // Carrega FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrClubeScreens/TelaVisualizarClube.fxml"));
        Parent root = loader.load();

        // Injeta a conexão no controller da tela inicial
        ControllerTelaVisualizarClube controller = loader.getController();
        controller.abrirTelaVisualizarClube(conn, clube);

        // Mostra a tela
        primaryStage.setTitle("Tela de Clubes");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {

        launch(args); // roda as telas
        /*

        model.Clube clube = new model.Clube(conn, "Fluminense");
        clube.addJogador(conn, "Everaldo", model.Posicao.ATACANTE, 7.0, 76);
        clube.addJogador(conn, "Arias", model.Posicao.ATACANTE, 15.0, 80);
        clube.addJogador(conn, "K. Serna", model.Posicao.ATACANTE, 6.0, 75);
        clube.addJogador(conn, "Ganso", model.Posicao.MEIA, 13.0, 80);
        clube.addJogador(conn, "Martinelli", model.Posicao.MEIA, 7.0,77);
        clube.addJogador(conn, "Bernal", model.Posicao.MEIA, 7.0, 75);
        clube.addJogador(conn, "G. Fuentes", model.Posicao.ZAGUEIRO, 9.0, 75);
        clube.addJogador(conn, "Freytes", model.Posicao.ZAGUEIRO, 6.0, 72);
        clube.addJogador(conn, "T. Silva", model.Posicao.ZAGUEIRO, 14.0, 80);
        clube.addJogador(conn, "S. Xavier", model.Posicao.ZAGUEIRO, 11.0, 76);
        clube.addJogador(conn, "Fábio", model.Posicao.GOLEIRO, 14.0, 79);
        clube.addJogador(conn, "G. Cano", model.Posicao.ATACANTE, 13, 78);

        model.Clube clube2 = new model.Clube(conn, "Botafogo");
        clube2.addJogador(conn, "Igor Jesus", model.Posicao.ATACANTE, 14.0, 78);
        clube2.addJogador(conn, "J. Correa", model.Posicao.ATACANTE, 16.0, 78);
        clube2.addJogador(conn, "Artur", model.Posicao.ATACANTE, 14.0, 78);
        clube2.addJogador(conn, "Savarino", model.Posicao.MEIA, 16.0, 79);
        clube2.addJogador(conn, "Gregore", model.Posicao.MEIA, 13.0, 77);
        clube2.addJogador(conn, "Marlon Freitas", model.Posicao.MEIA, 15.0, 78);
        clube2.addJogador(conn, "Alex Telles", model.Posicao.ZAGUEIRO, 16.0, 80);
        clube2.addJogador(conn, "A. Barbosa", model.Posicao.ZAGUEIRO, 12.0, 78);
        clube2.addJogador(conn, "Jair", model.Posicao.ZAGUEIRO, 9.0, 73);
        clube2.addJogador(conn, "Cuiabano", model.Posicao.ZAGUEIRO, 12.0, 77);
        clube2.addJogador(conn, "John", model.Posicao.GOLEIRO, 12.0, 78);
        clube2.addJogador(conn, "Mastriani", model.Posicao.ATACANTE, 7.0, 76);
        
        model.Usuario pedro = new model.Usuario("pedro", "1234");
        model.TimeUsuario time = pedro.getTimeUsuario();

        for(model.Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        model.Usuario mauroPatrao = new model.Usuario("patrao", "1234");
        model.TimeUsuario time2 = mauroPatrao.getTimeUsuario();

        for(model.Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }

        for(model.Jogador jogador : clube2.getJogadores()){
            time2.addJogador(jogador);
            time2.setCapitao(jogador);
        }

        time.imprimirTime();
        time2.imprimirTime();

        model.Liga liga = new model.Liga("Cicartola");

        liga.addUsuario(pedro);
        liga.addUsuario(mauroPatrao);

        model.Simulacao.addPartida(clube, clube2);

        List<model.Clube> clubes = new ArrayList<>();
        clubes.add(clube);
        clubes.add(clube2);
        System.out.println("ataque over 1: " + clube.getOverAtaque());
        System.out.println("defesa over 1: " + clube.getOverDefesa());
        System.out.println("ataque over 2: " + clube2.getOverAtaque());
        System.out.println("defesa over 2: " + clube2.getOverDefesa());

        model.Simulacao.gerarPartidasAleatorias(clubes);
        model.Simulacao.simular(liga);

        liga.exibirRanking(liga.gerarRanking());

         */
    }
}