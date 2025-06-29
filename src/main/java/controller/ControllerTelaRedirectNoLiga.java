package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerTelaRedirectNoLiga {
    @FXML
    private Button criarLigaButton;

    @FXML
    public void voltar(){
        Scene previous = NavigationManager.pop();
        if (previous != null) {
            Stage stage = (Stage) criarLigaButton.getScene().getWindow();
            stage.setScene(previous);
        }
    }
}
