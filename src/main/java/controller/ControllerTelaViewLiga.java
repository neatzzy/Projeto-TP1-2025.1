package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Liga;
import model.Pessoa;
import model.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaViewLiga {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private Button sairButton;

    private Connection conn;
    private Liga liga;
    private Usuario usuario;
    private UsuarioDAO usuarioDAO;

    public void setConnection(Connection conn, Liga liga, Usuario usuario) {
        this.conn = conn;
        this.liga = liga;
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn)); // Se precisar pode passar LigaDAO também
        carregarDados();
    }

    private void carregarDados() {
        try {
            labelTitulo.setText(liga.getNome()); // Nome da liga no título

            List<Pessoa> usuariosDaLiga = usuarioDAO.getAllUsuariosSemLiga();

            List<Usuario> usuariosDaLiga2 = usuariosDaLiga.stream()
                    .map(u -> (Usuario) u)
                    .toList();

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosDaLiga2);
            listViewUsuarios.setItems(obs);

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Falha ao carregar usuários da liga.");
            e.printStackTrace();
        }
    }

    @FXML
    public void voltar() {
        Scene previous = NavigationManager.pop();
        if (previous != null) {
            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(previous);
        }
    }

    @FXML
    private void abrirMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
            Parent root = loader.load();

            controller.ControllerTelaMenuUsuario controller = loader.getController();
            controller.setConnection(conn);
            controller.setUsuarioLogado(usuario);

            Stage stage = (Stage) sairButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu do Usuário");

            NavigationManager.clear();
            NavigationManager.push(stage.getScene());

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir o menu.");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}

