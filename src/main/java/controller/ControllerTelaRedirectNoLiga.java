package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Usuario;

import java.io.IOException;
import java.sql.Connection;

public class ControllerTelaRedirectNoLiga {

    @FXML
    private Button criarLigaButton;

    @FXML
    private Button menuMontagem;

    @FXML
    private Button acessarLigasButton;

    private Connection conn;
    private Usuario usuario;

    public void setConnection(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    private void irParaCriarLiga() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaCriarLiga.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), "Liga");
            NavigationManager.push(sceneInfo);

            controller.ControllerTelaCriarLiga controller = loader.getController();
            controller.setConnection(conn, usuario);

            Stage stage = (Stage) criarLigaButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Criar Liga");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irParaEntrarLiga() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaEntrarLiga.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), "Liga");
            NavigationManager.push(sceneInfo);

            controller.ControllerTelaEntrarLiga controller = loader.getController();
            controller.setConnection(conn, usuario);

            Stage stage = (Stage) acessarLigasButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Entrar em Liga");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
