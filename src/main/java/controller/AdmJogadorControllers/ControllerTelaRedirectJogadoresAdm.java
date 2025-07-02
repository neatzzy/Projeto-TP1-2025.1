package controller.AdmJogadorControllers;

import controller.NavigationManager;
import controller.SceneInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class ControllerTelaRedirectJogadoresAdm {

    private Connection conn;

    @FXML
    private Button menuMontagem;

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void irParaVerJogadores(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrJogadorScreens/TelaViewJogadores.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Jogadores");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void irParaCriarJogadores(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmJogadorScreens/TelaCriarJogador.fxml"));
            Parent root = loader.load();

            ControllerTelaCriarJogador controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Criar Jogador");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void irParaDeletarJogadores(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmJogadorScreens/TelaDeleteJogadores.fxml"));
            Parent root = loader.load();

            ControllerTelaDeleteJogadores controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Deletar Jogadores");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
