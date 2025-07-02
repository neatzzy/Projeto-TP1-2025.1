package controller.UsrLigaControllers;

import controller.NavigationManager;
import controller.SceneInfo;
import controller.UsrMenuController.ControllerTelaMenuUsuario;
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
import model.UserType;
import model.Usuario;

import java.io.IOException;
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
    private TimeDAO timeDAO;

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    public void setConnection(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;
        this.ligaDAO = new LigaDAO(conn);
        this.usuarioDAO = new UsuarioDAO(conn, ligaDAO);
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));
        carregarUsuarios();

    }

    private void carregarUsuarios() {
        try {

            List<Pessoa> usuariosSemLiga = usuarioDAO.getAllUsuariosSemLiga();

            List<Usuario> usuariosSemLiga2 = usuariosSemLiga.stream()
                    .filter(u -> u.getId() != this.usuario.getId())
                    .map(u -> (Usuario) u)
                    .toList();

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosSemLiga2);
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

            for(Usuario u : listaSelecionados){
                if(!timeDAO.usuarioTemTime(u.getId())) {
                    timeDAO.insertTime(u.getId(), "Time de" + u.getNome(), idNovaLiga);
                }
            }

            if(!timeDAO.usuarioTemTime(usuario.getId())) {
                timeDAO.insertTime(usuario.getId(), "Time de" + usuario.getNome(), idNovaLiga);
            }

            this.usuario.setTipo(UserType.ADMLIGA);

            mostrarAlerta("Sucesso", "Liga criada e usuários adicionados!");
            tfNomeLiga.clear();
            pfSenhaLiga.clear();
            lvUsuarios.getSelectionModel().clearSelection();

            NavigationManager.clear();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                Parent root = loader.load();

                ControllerTelaMenuUsuario controller = loader.getController();
                controller.setConnection(conn);
                controller.setUsuarioLogado(this.usuario);

                Stage stage = (Stage) btnCriarLiga.getScene().getWindow();

                SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
                NavigationManager.push(sceneInfo);

                stage.setScene(new Scene(root));
                stage.setTitle("Menu do Usuário");

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
            }


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
