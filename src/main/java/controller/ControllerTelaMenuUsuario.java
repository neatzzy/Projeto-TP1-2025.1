package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Usuario;

import java.sql.Connection;

public class ControllerTelaMenuUsuario {
    private Connection conn;
    private Usuario usuario;

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = (Usuario) usuario;
        labelTitulo.setText("Bem-vindo, " + usuario.getNome() + "!");
    }


    @FXML
    private Label labelTitulo;
    @FXML
    private Button ligaButton;
    @FXML
    private Button escalarButton;
    @FXML
    private Button partidasButton;
    @FXML
    private Button jogadoresButton;
    @FXML
    private Button editarPerfilButton;
    @FXML
    private Button sairButton;

    @FXML
    private void initialize() {
        // Inicialização, se necessário
    }

    @FXML
    private void onLigaButton(ActionEvent event) {
        // Lógica para abrir a tela da Liga
    }

    @FXML
    private void onEscalarButton(ActionEvent event) {
        // Lógica para abrir a tela de Escalação
    }

    @FXML
    private void onPartidasButton(ActionEvent event) {
        // Lógica para abrir a tela de Partidas
    }

    @FXML
    private void onJogadoresButton(ActionEvent event) {
        // Lógica para abrir a tela de Jogadores
    }

    @FXML
    private void onEditarPerfilButton(ActionEvent event) {
        // Lógica para abrir a tela de edição de perfil
    }

    @FXML
    private void onSairButton(ActionEvent event) {
        // Lógica para logout ou voltar para tela de login
    }
}
