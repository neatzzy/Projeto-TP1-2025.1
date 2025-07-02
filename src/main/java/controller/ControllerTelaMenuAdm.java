package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaMenuAdm {
    private Admin adm;
    private Connection conn;

    @FXML
    private Label labelTitulo;

    @FXML
    private Button simulacaoButton;

    @FXML
    private Button ligasButton;

    @FXML
    private Button usuariosButton;

    @FXML
    private Button clubesButton;

    @FXML
    private Button jogadoresButton;

    @FXML
    private Button partidasButton;

    @FXML
    private Button sairButton;


    public void setUsuarioLogado(Admin adm) { this.adm = adm; }

    public void setConnection(Connection conn) { this.conn = conn; }

    // cada uma desses métodos redireciona para uma parte do programa
    public void abrirSimulacao(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmSimulacaoScreens/TelaSimulacao.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Simulação");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirLigas(){
        if (Simulacao.getOcorreu()) {
            showAlert("Acesso bloqueado enquanto a simulação estiver ocorrendo!");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmLigaScreens/TelaViewLigas.fxml"));
            Parent root = loader.load();

            ControllerTelaViewLigas controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Ligas");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirUsuarios(){
        if (Simulacao.getOcorreu()) {
            showAlert("Acesso bloqueado enquanto a simulação estiver ocorrendo!");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmUserScreens/TelaViewUsuarios.fxml"));
            Parent root = loader.load();

            ControllerTelaViewUsuarios controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Usuários");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirClubes(){
        if (Simulacao.getOcorreu()) {
            showAlert("Acesso bloqueado enquanto a simulação estiver ocorrendo!");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmClubesScreens/TelaRedirectClubesAdm.fxml"));
            Parent root = loader.load();

            ControllerTelaRedirectClubesAdm controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Clubes");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirJogadores(){
        if (Simulacao.getOcorreu()) {
            showAlert("Acesso bloqueado enquanto a simulação estiver ocorrendo!");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmJogadorScreens/TelaRedirectJogadoresAdm.fxml"));
            Parent root = loader.load();

            ControllerTelaRedirectJogadoresAdm controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Jogadores");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirPartidas(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmPartidaScreens/TelaRedirectPartidasAdm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Partidas");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void voltar(){
        try {

            NavigationManager.clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaInicio.fxml"));
            Parent root = loader.load();

            ControllerTelaInicio controller = loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tela de Início");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acesso Bloqueado");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


