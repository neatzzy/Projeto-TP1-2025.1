package controller.AdmUserController;

import controller.NavigationManager;
import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Pessoa;
import model.Usuario;
import model.UserType;
import model.Liga;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTelaViewUsuarios {

    @FXML private Button menuMontagem;
    @FXML private TextField tfBuscaUsuario;
    @FXML private Button btnBuscar;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, Void> colAcoes;

    private Connection conn;
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;

    private ObservableList<Usuario> usuariosObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        btnBuscar.setOnAction(e -> buscarUsuarios());

        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipo().name()));
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnDeletar = new Button("Deletar");

            {
                btnDeletar.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 8;");
                btnDeletar.setOnAction(e -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    deletarUsuario(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDeletar);
                }
            }
        });
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        try {
            List<Pessoa> pessoas = usuarioDAO.getAllUsuarios();

            List<Usuario> usuarios = pessoas.stream()
                    .filter(p -> p instanceof Usuario)
                    .map(p -> (Usuario) p)
                    .collect(Collectors.toList());

            usuariosObservable.setAll(usuarios);
            tableUsuarios.setItems(usuariosObservable);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar os usuários.");
            e.printStackTrace();
        }
    }

    private void buscarUsuarios() {
        String termo = tfBuscaUsuario.getText().trim().toLowerCase();

        if (termo.isEmpty()) {
            tableUsuarios.setItems(usuariosObservable);
            return;
        }

        List<Usuario> filtrados = usuariosObservable.stream()
                .filter(u -> u.getNome().toLowerCase().contains(termo))
                .collect(Collectors.toList());

        tableUsuarios.setItems(FXCollections.observableArrayList(filtrados));
    }

    private void deletarUsuario(Usuario usuario) {
        try {
            if (usuario.getTipo() == UserType.ADMLIGA) {
                Liga liga = usuario.getLiga();
                if (liga != null) {
                    // Remove todos os usuários da liga
                    for (Usuario u : liga.getUsuarios()) {
                        usuarioDAO.removerUsuarioDaLiga(u, liga);
                    }
                    // Deleta a liga
                    ligaDAO.deleteLiga(liga.getId());
                }
            }

            // Deleta o usuário (adminLiga ou não)
            usuarioDAO.deleteUsuarioById(usuario.getId());
            mostrarAlerta("Sucesso", "Usuário deletado com sucesso!");
            carregarUsuarios();

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Falha ao deletar o usuário ou a liga.");
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

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}
