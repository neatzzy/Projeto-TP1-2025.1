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

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega variáveis de ambiente
        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        // Conexão com banco de dados
        Database db = new Database();
        Connection conn = db.connectToDb(db_name, user, pass);
        Simulacao simulacao = new Simulacao();
        simulacao.InicializarConexoes(conn);

        ClubeDAO clubeDAO = new ClubeDAO(conn);
        JogadorDAO jogadorDAO = new JogadorDAO(conn, clubeDAO);
        List<Clube> clubes = clubeDAO.getAllClubes();
        jogadorDAO.getAllJogadores(clubes);
        Simulacao.gerarPartidasAleatorias(clubes);

        // Carrega a tela inicial
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaInicio.fxml"));
        Parent root = loader.load();
        ControllerTelaInicio controller = loader.getController();
        controller.setConnection(conn);

        primaryStage.setTitle("Cartolitos CF");
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}