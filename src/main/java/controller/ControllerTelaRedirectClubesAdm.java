package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class ControllerTelaRedirectClubesAdm {

    private Connection conn;

    @FXML
    private Button menuMontagem;

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void irParaCriarClubes(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmClubesScreens/TelaCriarClubes.fxml"));
            Parent root = loader.load();

            controller.ControllerTelaCriarClubes controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Criar Clubes");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void irParaDeletarClubes(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmClubesScreens/TelaDeleteClubes.fxml"));
            Parent root = loader.load();

            controller.ControllerTelaDeleteClubes controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Deletar Clubes");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
