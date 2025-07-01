package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Pessoa;
import model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaViewUsuarios {

    @FXML private Button menuMontagem;
    @FXML private Button btnAtualizar;
    @FXML private TextField tfNomeUsuario;
    @FXML private TextField tfEmail;
    @FXML private TextField tfId;
    @FXML private ComboBox<String> cbTipo;
    @FXML private VBox usuariosBox;

    private Connection conn;
    private UsuarioDAO usuarioDAO;

    private Usuario usuarioSelecionado;

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList("user", "adminLiga", "admin"));
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        LigaDAO ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        usuariosBox.getChildren().clear();

        try {
            List<Pessoa> pessoas = usuarioDAO.getAllUsuarios();

            if (pessoas.isEmpty()) {
                Label vazio = new Label("Nenhum usuário encontrado.");
                vazio.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                usuariosBox.getChildren().add(vazio);
                return;
            }

            for (Pessoa p : pessoas) {
                if (p instanceof Usuario u) {
                    usuariosBox.getChildren().add(criarLinhaUsuario(u));
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar os usuários.");
            e.printStackTrace();
        }
    }

    private HBox criarLinhaUsuario(Usuario usuario) {
        HBox linha = new HBox(20);
        linha.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 10;");
        linha.setPrefHeight(50.0);

        Label nomeLabel = new Label(usuario.getNome());
        nomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nomeLabel.setPrefWidth(200.0);

        Button selecionarBtn = new Button("Selecionar");
        selecionarBtn.setStyle("-fx-background-color: #00796B; -fx-text-fill: white; -fx-background-radius: 8;");
        selecionarBtn.setOnAction(e -> preencherCampos(usuario));

        linha.getChildren().addAll(nomeLabel, selecionarBtn);
        return linha;
    }

    private void preencherCampos(Usuario usuario) {
        this.usuarioSelecionado = usuario;

        tfNomeUsuario.setText(usuario.getNome());
        cbTipo.setValue(usuario.getTipo().name().toLowerCase());
        tfId.setText(String.valueOf(usuario.getId()));
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}
