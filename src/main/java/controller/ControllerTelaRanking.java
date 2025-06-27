package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTelaRanking implements Initializable {


    @FXML
    private Label menuLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void abrirMenu(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/screens/AdmSimulacaoScreens/TelaSimulacao.fxml")); // so pra testar, é pra redirecionar pro menu/simulacao
            Stage stage = (Stage) menuLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
