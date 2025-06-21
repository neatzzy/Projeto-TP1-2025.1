import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ControllerTelaLogin {

    @FXML
    private TextField nomeField;

    @FXML
    private PasswordField senhaField;

    // Método chamado ao clicar no botão de login (adicione o botão no FXML e vincule este método)
    @FXML
    private void handleLogin() {
        String nome = nomeField.getText();
        String senha = senhaField.getText();

        // Aqui você pode adicionar a lógica de autenticação
        System.out.println("Nome: " + nome + ", Senha: " + senha);
    }
}