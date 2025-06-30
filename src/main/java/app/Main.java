package app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.*;
import model.*;
import controller.*;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import database.Database;


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
        Simulacao simulacao = new Simulacao();
        simulacao.InicializarConexoes(conn);

        // Carrega todos os clubes e jogadores do banco de dados
        ClubeDAO clubeDAO = new ClubeDAO(conn);
        JogadorDAO jogadorDAO = new JogadorDAO(conn, clubeDAO);

        List<Clube>clubes = clubeDAO.getAllClubes();
        jogadorDAO.getAllJogadores(clubes);

        //Gera as partidas aleatórias
        Simulacao.gerarPartidasAleatorias(clubes);

        Simulacao.simular();

        // Carrega FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaInicio.fxml"));
        Parent root = loader.load();

        // Injeta a conexão no controller da tela inicial
        ControllerTelaInicio controller = loader.getController();
        controller.setConnection(conn);

        // Mostra a tela
        primaryStage.setTitle("Tela de Início");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {

        launch(args); // roda as telas

    }
}