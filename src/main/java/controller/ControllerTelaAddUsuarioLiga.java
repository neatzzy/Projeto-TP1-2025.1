package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Liga;
import model.Pessoa;
import model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerTelaAddUsuarioLiga {

    @FXML
    private Button menuMontagem;

    @FXML
    private TextField campoBusca;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private Button botaoAdicionar;

    private Connection conn;
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;
    private Usuario usuario;
    private Liga liga;

    private List<Usuario> usuariosDisponiveis = new ArrayList<>();

    public void setConnection(Connection conn, Usuario usuario, Liga liga) {
        this.conn = conn;
        this.usuario = usuario;
        this.liga = liga;
        this.ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        carregarUsuarios();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    private void initialize() {

        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        campoBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarUsuarios(newVal);
        });

        botaoAdicionar.setOnAction(e -> {
            try {
                adicionarUsuario();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void carregarUsuarios() {
        try {
            List<Pessoa> todasPessoas = usuarioDAO.getAllUsuarios();
            usuariosDisponiveis.clear();

            for (Pessoa p : todasPessoas) {
                if (p instanceof Usuario u && u.getLiga() == null && u.getId() != usuario.getId()) {
                    usuariosDisponiveis.add(u);
                }
            }

            listViewUsuarios.setItems(FXCollections.observableArrayList(usuariosDisponiveis));
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar usuários.");
        }
    }

    private void filtrarUsuarios(String texto) {
        List<Usuario> filtrados = new ArrayList<>();

        for (Usuario u : usuariosDisponiveis) {
            if (u.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(u);
            }
        }

        ObservableList<Usuario> obs = FXCollections.observableArrayList(filtrados);
        listViewUsuarios.setItems(obs);
    }

    private void adicionarUsuario() throws SQLException {

        ObservableList<Usuario> selecionados = listViewUsuarios.getSelectionModel().getSelectedItems();

        if (selecionados.isEmpty()) {
            mostrarAlerta("Atenção", "Nenhum usuário selecionado.");
            return;
        }

        usuarioDAO.insertUsuariosLiga(selecionados, liga);

        StringBuilder nomes = new StringBuilder();
        for (Usuario u : selecionados) {
            nomes.append(u.getNome()).append("\n");
        }

        mostrarAlerta("Sucesso", "Usuários adicionados:\n" + nomes);
        carregarUsuarios();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
