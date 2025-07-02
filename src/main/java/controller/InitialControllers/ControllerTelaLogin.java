package controller.InitialControllers;

import controller.AdmMenuController.ControllerTelaMenuAdm;
import controller.UsrMenuController.ControllerTelaMenuUsuario;
import controller.NavigationManager;
import controller.SceneInfo;
import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaLogin {

    private Connection conn;
    private UsuarioDAO db;

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;

    // Volta para a tela anterior usando o NavigationManager
    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) campoEmail.getScene().getWindow());
    }

    // Define a conexão e instancia o DAO de usuários
    public void setConnection(Connection conn) {
        this.conn = conn;
        this.db = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    // Tenta fazer login com os dados fornecidos
    @FXML
    private void loginUsuario() {
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        // Verifica se os campos estão preenchidos
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos.");
            return;
        }

        Pessoa usuario;

        try {
            // Busca o usuário no banco pelo email
            usuario = db.getUsuarioByEmail(email);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao cadastrar: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Se não encontrar o usuário
        if (usuario == null) {
            mostrarAlerta("Erro", "Usuário não encontrado com esse email.");
            return;
        }

        String senhaHash = usuario.getSenha();

        // Verifica se a senha digitada corresponde ao hash
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean senhaValida = encoder.matches(senha, senhaHash);

        if (senhaValida) {
            mostrarAlerta("Sucesso", "Login realizado com sucesso!");
            limparCampos();

            try {
                if (usuario instanceof Usuario){
                    Usuario usr = (Usuario) usuario;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                    Parent root = loader.load();
                    ControllerTelaMenuUsuario controllerMenuUsuario = loader.getController();
                    controllerMenuUsuario.setUsuarioLogado(usr);
                    controllerMenuUsuario.setConnection(conn);
                    Stage stage = (Stage) campoEmail.getScene().getWindow();
                    SceneInfo sceneInfo = new SceneInfo(campoEmail.getScene(), stage.getTitle());
                    NavigationManager.push(sceneInfo);
                    stage.setScene(new Scene(root));
                    stage.setTitle("Menu do Usuário");
                    stage.show();

                } else if (usuario instanceof Admin){
                    Admin adm = (Admin) usuario;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmMenuScreens/TelaMenuAdm.fxml"));
                    Parent root = loader.load();
                    ControllerTelaMenuAdm controllerMenuAdm = loader.getController();
                    controllerMenuAdm.setUsuarioLogado(adm);
                    controllerMenuAdm.setConnection(conn);
                    Stage stage = (Stage) campoEmail.getScene().getWindow();
                    SceneInfo sceneInfo = new SceneInfo(campoEmail.getScene(), stage.getTitle());
                    NavigationManager.push(sceneInfo);
                    stage.setScene(new Scene(root));
                    stage.setTitle("Menu Admin");
                    stage.show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Senha incorreta
            mostrarAlerta("Erro", "Senha incorreta.");
            campoSenha.clear();
        }
    }

    // Limpa os campos do formulário
    private void limparCampos() {
        campoEmail.clear();
        campoSenha.clear();
    }

    // Exibe um alerta com título e mensagem
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
