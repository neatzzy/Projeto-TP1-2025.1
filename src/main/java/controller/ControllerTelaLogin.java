package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    @FXML
    public void voltar(){
        Scene previous = NavigationManager.pop();
        if (previous != null) {
            Stage stage = (Stage) campoEmail.getScene().getWindow();
            stage.setScene(previous);
        }
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.db = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    @FXML
    private void loginUsuario() {

        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos.");
            return;
        }

        Pessoa usuario;

        try {
            usuario = db.getUsuarioByEmail(email);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao cadastrar: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (usuario == null) {
            mostrarAlerta("Erro", "Usuário não encontrado com esse email.");
            return;
        }

        String senhaHash = usuario.getSenha();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean senhaValida = encoder.matches(senha, senhaHash);

        if (senhaValida) {
            mostrarAlerta("Sucesso", "Login realizado com sucesso!");
            limparCampos();


            try {

                NavigationManager.push(campoEmail.getScene());

                if (usuario instanceof Usuario){

                    Usuario usr = (Usuario) usuario;

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                    Parent root = loader.load();

                    ControllerTelaMenuUsuario controllerMenuUsuario = loader.getController();
                    controllerMenuUsuario.setUsuarioLogado(usr);
                    controllerMenuUsuario.setConnection(conn);

                    Stage stage = (Stage) campoEmail.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Menu Usuário");
                    stage.show();
                }
                else if (usuario instanceof Admin){

                    Admin adm = (Admin) usuario;

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmMenuScreens/TelaMenuAdm.fxml"));
                    Parent root = loader.load();

                    ControllerTelaMenuAdm controllerMenuAdm = loader.getController();
                    controllerMenuAdm.setUsuarioLogado(adm);
                    controllerMenuAdm.setConnection(conn);

                    Stage stage = (Stage) campoEmail.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Menu Admin");
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mostrarAlerta("Erro", "Senha incorreta.");
            campoSenha.clear();
        }

    }

    private void limparCampos() {
        campoEmail.clear();
        campoSenha.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
