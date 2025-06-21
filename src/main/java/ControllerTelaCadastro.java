import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerTelaCadastro {

    @FXML
    private TextField nomeField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private PasswordField confirmarSenhaField;

    @FXML
    private void handleCadastro() {
        String nome = nomeField.getText();
        String senha = senhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();

        if (nome.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            mostrarAlerta("Preencha todos os campos!");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            mostrarAlerta("As senhas não coincidem!");
            return;
        }

        // Lógica de cadastro (exemplo: salvar usuário)
        System.out.println("Usuário cadastrado: " + nome);
        mostrarAlerta("Cadastro realizado com sucesso!");
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}