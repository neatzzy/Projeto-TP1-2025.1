package controller.UsrLigaControllers;

import controller.NavigationManager;
import dao.*;
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
    private Usuario usuario; // usuário atual (admin)
    private TimeDAO timeDAO;

    private Liga liga; // liga à qual serão adicionados usuários
    private List<Usuario> usuariosDisponiveis = new ArrayList<>();

    // Método chamado externamente para inicializar dependências e carregar usuários
    public void setConnection(Connection conn, Usuario usuario, Liga liga) {
        this.conn = conn;
        this.usuario = usuario;
        this.liga = liga;

        this.ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        this.timeDAO = new TimeDAO(conn,
                new UsuarioDAO(conn, new LigaDAO(conn)),
                new JogadorDAO(conn, new ClubeDAO(conn)));

        carregarUsuarios();
    }

    // Retorna para a tela anterior
    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    // Inicializa comportamentos da interface
    @FXML
    private void initialize() {
        // Permite múltiplas seleções na lista
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Atualiza lista conforme texto digitado no campo de busca
        campoBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarUsuarios(newVal);
        });

        // Ação do botão de adicionar
        botaoAdicionar.setOnAction(e -> {
            try {
                adicionarUsuario();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    // Carrega todos os usuários que ainda não pertencem a uma liga
    private void carregarUsuarios() {
        try {
            List<Pessoa> todasPessoas = usuarioDAO.getAllUsuarios();
            usuariosDisponiveis.clear();

            for (Pessoa p : todasPessoas) {
                // Só adiciona usuários reais, sem liga e que não sejam o usuário atual
                if (p instanceof Usuario u && u.getLiga() == null && u.getId() != usuario.getId()) {
                    usuariosDisponiveis.add(u);
                }
            }

            listViewUsuarios.setItems(FXCollections.observableArrayList(usuariosDisponiveis));

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar usuários.");
        }
    }

    // Filtra lista de usuários com base no texto buscado
    private void filtrarUsuarios(String texto) {
        List<Usuario> filtrados = new ArrayList<>();

        for (Usuario u : usuariosDisponiveis) {
            if (u.getNome().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(u);
            }
        }

        listViewUsuarios.setItems(FXCollections.observableArrayList(filtrados));
    }

    // Adiciona os usuários selecionados à liga atual
    private void adicionarUsuario() throws SQLException {
        ObservableList<Usuario> selecionados = listViewUsuarios.getSelectionModel().getSelectedItems();

        if (selecionados.isEmpty()) {
            mostrarAlerta("Atenção", "Nenhum usuário selecionado.");
            return;
        }

        // Insere os usuários na liga
        usuarioDAO.insertUsuariosLiga(selecionados, liga);

        // Cria um time para cada usuário, se ainda não tiver
        for (Usuario u : selecionados) {
            if (!timeDAO.usuarioTemTime(u.getId())) {
                timeDAO.insertTime(u.getId(), "Time de" + u.getNome(), liga.getId());
            }
        }

        // Prepara nomes dos adicionados para exibir em alerta
        StringBuilder nomes = new StringBuilder();
        for (Usuario u : selecionados) {
            nomes.append(u.getNome()).append("\n");
        }

        mostrarAlerta("Sucesso", "Usuários adicionados:\n" + nomes);

        // Fecha tela atual e volta para a tela de administração da liga
        NavigationManager.pop();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaViewLigaAdm.fxml"));
            Parent root = loader.load();

            ControllerTelaViewLigaAdm controller = loader.getController();
            controller.setConnection(conn, this.liga, this.usuario);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Liga");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao voltar para a tela da liga.");
        }
    }

    // Exibe um alerta com título e mensagem
    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
