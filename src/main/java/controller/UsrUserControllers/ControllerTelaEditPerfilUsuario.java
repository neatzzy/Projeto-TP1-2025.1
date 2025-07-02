package controller.UsrUserControllers;

import controller.NavigationManager;
import controller.UsrMenuController.ControllerTelaMenuUsuario;
import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaEditPerfilUsuario {

    private Usuario usuario;
    private Connection conn;

    @FXML private Button menuMontagem;
    @FXML private Button btnSalvarPerfil;
    @FXML private TextField tfNome;
    @FXML private PasswordField pfSenhaAntiga;
    @FXML private PasswordField pfSenha;
    @FXML private PasswordField pfConfirmarSenha;
    @FXML private Label lblMensagem;

    @FXML
    public void initialize() {
        lblMensagem.setText("");
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void salvarPerfil() {
        String nome = tfNome.getText();
        String senhaAntiga = pfSenhaAntiga.getText();
        String senha = pfSenha.getText();
        String confirmarSenha = pfConfirmarSenha.getText();

        if (nome == null || nome.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Erro de validação", "Nome não pode estar vazio.");
            return;
        }

        if (senhaAntiga == null || senhaAntiga.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Erro de validação", "Preencha a senha antiga.");
            return;
        }

        if (senha == null || senha.isBlank() || confirmarSenha == null || confirmarSenha.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Erro de validação", "Preencha a nova senha e sua confirmação.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            showAlert(Alert.AlertType.ERROR, "Erro de validação", "As senhas não conferem.");
            return;
        }

        String senhaHash = usuario.getSenha();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean senhaValida = encoder.matches(senhaAntiga, senhaHash);

        if (!senhaValida) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Senha antiga incorreta.");
            clear();
            return;
        }

        // Atualiza o usuário
        usuario.setNome(nome);
        usuario.setSenha(senha);

        try {
            LigaDAO ligaDAO = new LigaDAO(conn);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn, ligaDAO);
            usuarioDAO.updateUsuario(usuario);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Perfil atualizado com sucesso!");
            limparCampos();

            // Vai para o Menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
            Parent root = loader.load();

            ControllerTelaMenuUsuario controllerMenuUsuario = loader.getController();
            controllerMenuUsuario.setUsuarioLogado(usuario);
            controllerMenuUsuario.setConnection(conn);

            Stage stage = (Stage) btnSalvarPerfil.getScene().getWindow();
            NavigationManager.clear();

            stage.setScene(new Scene(root));
            stage.setTitle("Menu do Usuário");
            stage.show();

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar as alterações.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void limparCampos() {
        tfNome.clear();
        pfSenhaAntiga.clear();
        pfSenha.clear();
        pfConfirmarSenha.clear();
        lblMensagem.setText("");
    }

    private void clear() {
        pfSenhaAntiga.clear();
        pfSenha.clear();
        pfConfirmarSenha.clear();
        lblMensagem.setText("");
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
