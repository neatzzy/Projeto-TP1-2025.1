package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaCadastro {

    private Connection conn;

    // DAO para operações com usuários
    private UsuarioDAO db = new UsuarioDAO(conn, new LigaDAO(conn));

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;

    // Volta para a tela anterior
    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) campoEmail.getScene().getWindow());
    }

    // Define a conexão com o banco de dados e inicializa o DAO
    public void setConnection(Connection conn) {
        this.conn = conn;
        this.db = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    // Realiza o cadastro do usuário
    @FXML
    private void cadastrarUsuario() {
        String nome = campoNome.getText();
        String email = campoEmail.getText();
        String senha = campoSenha.getText();
        String confirmar = campoConfirmarSenha.getText();

        // Verifica se todos os campos estão preenchidos
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos.");
            return;
        }

        // Verifica se as senhas coincidem
        if (!senha.equals(confirmar)) {
            mostrarAlerta("Erro", "As senhas não coincidem.");
            return;
        }

        try {
            // Insere o usuário no banco
            int usuarioId = db.insertUsuario(nome, email, "user", senha, null);
            mostrarAlerta("Sucesso", "Usuário cadastrado com ID: " + usuarioId);
            limparCampos();

            // Navega para a tela de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaLogin.fxml"));
            Parent root = loader.load();

            ControllerTelaLogin controllerLogin = loader.getController();
            controllerLogin.setConnection(conn);

            Scene loginScene = new Scene(root);
            Stage stage = (Stage) campoEmail.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(campoEmail.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(loginScene);
            stage.setTitle("Tela de Login");
            stage.show();

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao cadastrar: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao carregar tela de login: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Limpa os campos do formulário
    private void limparCampos() {
        campoNome.clear();
        campoSenha.clear();
        campoConfirmarSenha.clear();
    }

    // Exibe um alerta informativo
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
