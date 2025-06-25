package controller;

import dao.UsuarioDAO;
import model.Pessoa;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaLogin {

    private Connection conn;

    private UsuarioDAO db = new UsuarioDAO(conn);

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.db = new UsuarioDAO(conn);
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
            // aqui você pode abrir a próxima tela, etc.
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
