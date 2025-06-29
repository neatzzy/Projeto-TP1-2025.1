package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Connection;

public class ControllerTelaInicio {

    private Connection conn;

    @FXML
    private Button botaoCriar;

    @FXML
    private Button botaoLogin;

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    @FXML
    private void abrirTelaCadastro() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaCadastro.fxml"));
            Parent root = loader.load();

            ControllerTelaCadastro controllerCadastro = loader.getController();
            controllerCadastro.setConnection(conn);

            Stage stage = (Stage) botaoCriar.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(botaoCriar.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Cadastro de Usuário");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirTelaLogin() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaLogin.fxml"));
            Parent root = loader.load();

            ControllerTelaLogin controllerLogin = loader.getController();
            controllerLogin.setConnection(conn);

            Stage stage = (Stage) botaoLogin.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(botaoCriar.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
