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
import model.UserType;
import model.Usuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaViewLigaAdm {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private Button deletarButton;

    @FXML
    private Button adicionarButton;

    private Connection conn;
    private Liga liga;
    private Usuario usuario; // admin logado
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;

    public void setConnection(Connection conn, Liga liga, Usuario usuario) {
        this.conn = conn;
        this.liga = liga;
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
        this.ligaDAO = new LigaDAO(conn);

        carregarDados();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private void carregarDados() {
        try {

            labelTitulo.setText(liga.getNome()); // Nome da liga no título

            List<Pessoa> usuariosDaLiga = usuarioDAO.getAllUsuariosByLigaId(liga.getId());

            List<Usuario> usuariosDaLiga2 = usuariosDaLiga.stream()
                    .map(u -> (Usuario) u)
                    .toList();

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosDaLiga2);
            listViewUsuarios.setItems(obs);

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar usuários da liga.");
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        deletarButton.setOnAction(e -> deletarLiga()); // Ainda é opcional
        adicionarButton.setOnAction(e -> abrirAdicionarUsuarios());
        listViewUsuarios.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                removerUsuarioSelecionado();
            }
        });
    }

    @FXML
    private void removerUsuarioSelecionado() {
        Usuario selecionado = listViewUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um usuário.");
            return;
        }

        if (selecionado.getId() == usuario.getId()) {
            mostrarAlerta("Erro", "Você não pode remover a si mesmo.");
            return;
        }

        try {
            boolean sucesso = usuarioDAO.removerUsuarioDaLiga(selecionado, this.liga);

            if (sucesso) {
                mostrarAlerta("Sucesso", "Usuário removido da liga.");
                carregarDados();
            } else {
                mostrarAlerta("Erro", "Erro ao remover o usuário.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao tentar remover o usuário.");
        }
    }

    @FXML
    private void deletarLiga() {
        try {

            ligaDAO.deleteLiga(this.liga.getId());
            usuarioDAO.transformarAdminLigaEmUsuario(this.usuario.getId());
            this.usuario.setTipo(UserType.USUARIO);
            this.usuario.sairLiga(liga);

            mostrarAlerta("Sucesso", "Liga removida.");

            NavigationManager.clear();

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                Parent root = loader.load();

                controller.ControllerTelaMenuUsuario controller = loader.getController();
                controller.setConnection(conn);
                controller.setUsuarioLogado(this.usuario);

                Stage stage = (Stage) menuMontagem.getScene().getWindow(); // corrige o botão se necessário
                stage.setScene(new Scene(root));
                stage.setTitle("Menu do Usuário");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao remover liga.");
        }
    }

    @FXML
    private void abrirAdicionarUsuarios() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaAddUsuarioLiga.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Liga");
            NavigationManager.push(sceneInfo);

            controller.ControllerTelaAddUsuarioLiga controller = loader.getController();
            controller.setConnection(conn, this.usuario, this.liga);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Liga");
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
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


