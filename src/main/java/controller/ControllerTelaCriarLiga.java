package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerTelaCriarLiga {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private TextField tfNomeLiga;

    @FXML
    private PasswordField pfSenhaLiga;

    @FXML
    private Button btnCriarLiga;

    @FXML
    private ListView<Usuario> lvUsuarios;

    private Connection conn;
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;
    private Usuario usuario;

    public void setConnection(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;
        this.ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        carregarUsuarios();

    }

    private void carregarUsuarios() {
        try {

            List<Pessoa> todasPessoas = usuarioDAO.getAllUsuarios();
            List<Usuario> usuariosSemLiga = new ArrayList<>();

            for (Pessoa p : todasPessoas) {
                if (p instanceof Usuario u && u.getLiga() == null && u.getId() != this.usuario.getId()) {
                    usuariosSemLiga.add(u);
                }
            }

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosSemLiga);
            lvUsuarios.setItems(obs);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar usuários.");
        }
    }

    @FXML
    private void initialize() {
        btnCriarLiga.setOnAction(e -> criarLiga());
        lvUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void criarLiga() {
        String nomeLiga = tfNomeLiga.getText().trim();
        String senhaLiga = pfSenhaLiga.getText();

        if (nomeLiga.isEmpty() || senhaLiga.isEmpty()) {
            mostrarAlerta("Aviso", "Preencha todos os campos.");
            return;
        }

        try {
            int idNovaLiga = ligaDAO.insertLiga(nomeLiga, senhaLiga);
            Liga novaLiga = ligaDAO.getLigaByID(idNovaLiga);

            ObservableList<Usuario> selecionados = lvUsuarios.getSelectionModel().getSelectedItems();
            List<Usuario> listaSelecionados = new ArrayList<>(selecionados);
            listaSelecionados.add(this.usuario);

            usuarioDAO.insertUsuariosLiga(listaSelecionados, novaLiga);
            usuarioDAO.transformarUsuarioEmAdminLiga(this.usuario.getId());
            this.usuario.setTipo(UserType.ADMLIGA);

            mostrarAlerta("Sucesso", "Liga criada e usuários adicionados!");
            tfNomeLiga.clear();
            pfSenhaLiga.clear();
            lvUsuarios.getSelectionModel().clearSelection();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao criar liga.");
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
