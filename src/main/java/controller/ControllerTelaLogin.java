package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXMLLoader;
import model.Admin;
import model.Pessoa;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaLogin {

    private Connection conn;

    private UsuarioDAO db = new UsuarioDAO(conn, new LigaDAO(conn));

    @FXML
    private TextField campoEmail;
    @FXML
    private PasswordField campoSenha;

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
            abrirTelaMenu(usuario, conn);

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

    private void abrirTelaMenu(Pessoa usuario, Connection conn) {
        try {
            FXMLLoader loader;
            if (usuario.isAdmin()) {
                loader = new FXMLLoader(getClass().getResource("/screens/AdmMenuScreens/TelaMenuAdmin.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
            }
            javafx.scene.Parent root = loader.load();

            // Supondo que o controller do menu tenha um método para receber o usuário e a conexão
            Object controller = loader.getController();
            if (controller instanceof ControllerTelaMenuUsuario) {
                ((ControllerTelaMenuUsuario) controller).setUsuario((Usuario) usuario);
                ((ControllerTelaMenuUsuario) controller).setConnection(conn);
            } else if (controller instanceof ControllerTelaMenuAdmin) {
                ((ControllerTelaMenuAdmin) controller).setAdmin((Admin) usuario);
                setConnection(conn);
            }

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

            // Fechar a tela de login
            campoEmail.getScene().getWindow().hide();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Não foi possível abrir o menu: " + e.getMessage());
        }
    }
}