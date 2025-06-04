
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class ControlerTelaSimulacao {

    @FXML
    private Label labelOlaMundo;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("Clicou");
        labelOlaMundo.setText("Hello porra");
    }
}
