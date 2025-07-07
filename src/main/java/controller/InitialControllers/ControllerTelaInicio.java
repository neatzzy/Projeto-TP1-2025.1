package controller.InitialControllers;

import controller.NavigationManager;
import controller.SceneInfo;
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

    // Define a conexão com o banco (será repassada para outras telas)
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    // Abre a tela de cadastro de usuário
    @FXML
    private void abrirTelaCadastro() {
        try {
            // Carrega o FXML e o controller da tela de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaCadastro.fxml"));
            Parent root = loader.load();

            ControllerTelaCadastro controllerCadastro = loader.getController();
            controllerCadastro.setConnection(conn);

            // Recupera o stage atual
            Stage stage = (Stage) botaoCriar.getScene().getWindow();

            // Salva a cena atual na pilha de navegação
            SceneInfo sceneInfo = new SceneInfo(botaoCriar.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            // Troca para a nova cena
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Cadastro de Usuário");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Abre a tela de login
    @FXML
    private void abrirTelaLogin() {
        try {
            // Carrega o FXML e o controller da tela de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaLogin.fxml"));
            Parent root = loader.load();

            ControllerTelaLogin controllerLogin = loader.getController();
            controllerLogin.setConnection(conn);

            // Recupera o stage atual
            Stage stage = (Stage) botaoLogin.getScene().getWindow();

            // Salva a cena atual na pilha de navegação
            SceneInfo sceneInfo = new SceneInfo(botaoCriar.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            // Troca para a nova cena
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// OBSERVAÇÃO:
// Esse padrão de navegação — carregando FXML, recuperando o controller,
// passando a conexão e empilhando a cena anterior no NavigationManager —
// será usado de forma consistente em todo o programa, garantindo navegação estruturada.
