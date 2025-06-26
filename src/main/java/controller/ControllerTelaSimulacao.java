package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTelaSimulacao implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label label1, label2, label3, label4, label5;

    @FXML
    private Label menuLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label1.setVisible(false);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        label5.setVisible(false);
        carregarComProgresso();
    }

    @FXML
    private void abrirMenu(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/screens/TelaRanking.fxml")); // so pra testar, é pra redirecionar pro menu/simulacao
            Stage stage = (Stage) menuLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarComProgresso() {
        progressBar.setProgress(0);

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                final double progress = i / 100.0;

                Platform.runLater(() -> {
                    progressBar.setProgress(progress);

                    // Mostra labels conforme o progresso alcança múltiplos de 20%
                    if (progress >= 0.2 && !label1.isVisible()) {
                        label1.setVisible(true);
                    }
                    if (progress >= 0.4 && !label2.isVisible()) {
                        label2.setVisible(true);
                    }
                    if (progress >= 0.6 && !label3.isVisible()) {
                        label3.setVisible(true);
                    }
                    if (progress >= 0.8 && !label4.isVisible()) {
                        label4.setVisible(true);
                    }
                    if (progress >= 1.0 && !label5.isVisible()) {
                        label4.setVisible(false);
                        label5.setVisible(true);
                    }
                });

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

