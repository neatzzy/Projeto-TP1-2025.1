package controller;

import dao.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.Liga;
import model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaEntrarLiga {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<Liga> lvLigas;

    @FXML
    private PasswordField pfSenha;

    @FXML
    private Button btnEntrar;

    private Connection conn;
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;
    private Usuario usuario;
    private TimeDAO timeDAO;

    public void setConnection(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;
        this.ligaDAO = new LigaDAO(this.conn);
        this.usuarioDAO = new UsuarioDAO(this.conn, this.ligaDAO);
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));

        carregarLigas();
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) lvLigas.getScene().getWindow());
    }

    private void carregarLigas() {
        try {
            List<Liga> ligas = ligaDAO.getAllLigas();
            ObservableList<Liga> obs = FXCollections.observableArrayList(ligas);
            lvLigas.setItems(obs);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar as ligas.");
        }
    }

    @FXML
    private void abrirMenu() {
        System.out.println("Menu clicado");
    }

    @FXML
    private void initialize() {
        btnEntrar.setOnAction(e -> tentarEntrarNaLiga());
    }

    // verifica se os campos estao corretos e insere na liga (semelhante ao login)
    private void tentarEntrarNaLiga() {
        Liga ligaSelecionada = lvLigas.getSelectionModel().getSelectedItem();
        String senha = pfSenha.getText();

        if (ligaSelecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma liga.");
            return;
        }

        if (senha.isEmpty()) {
            mostrarAlerta("Aviso", "Digite a senha da liga.");
            return;
        }

        String senhaHash = ligaSelecionada.getSenha();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean senhaValida = encoder.matches(senha, senhaHash);

        if (!senhaValida) {
            mostrarAlerta("Erro", "Senha incorreta.");
            pfSenha.clear();
            return;
        }

        try {

            boolean sucesso = usuarioDAO.insertUsuarioLiga(usuario, ligaSelecionada);
            if (sucesso) {

                if(!timeDAO.usuarioTemTime(usuario.getId())) {
                    timeDAO.insertTime(usuario.getId(), "Time de" + usuario.getNome(), ligaSelecionada.getId());
                }

                mostrarAlerta("Sucesso", "Você entrou na liga!");
                NavigationManager.clear();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                    Parent root = loader.load();

                    controller.ControllerTelaMenuUsuario controller = loader.getController();
                    controller.setConnection(conn);
                    controller.setUsuarioLogado(this.usuario);

                    Stage stage = (Stage) btnEntrar.getScene().getWindow();

                    SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
                    NavigationManager.push(sceneInfo);

                    stage.setScene(new Scene(root));
                    stage.setTitle("Menu do Usuário");

                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
                }

            } else {
                mostrarAlerta("Erro", "Erro ao atualizar o usuário.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao inserir o usuário na liga.");
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
