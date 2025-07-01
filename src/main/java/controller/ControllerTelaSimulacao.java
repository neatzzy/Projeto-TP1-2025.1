package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Partida;
import model.Simulacao;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ControllerTelaSimulacao implements Initializable {

    @FXML private ProgressBar progressBar;
    @FXML private Label labelMensagem;
    @FXML private Button menuMontagem;
    @FXML private Button btnSimular;
    @FXML private Button btnResetarSimulacao;

    private final String[] mensagens = {
            "Iniciando simulação...",
            "Simulando partidas...",
            "Calculando estatísticas...",
            "Contabilizando gols...",
            "Gerando resultados...",
            "Simulação finalizada!"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean ocorreu = Simulacao.getOcorreu();
        if (ocorreu) {
            mostrarSimulacaoCompleta();
        } else {
            resetarEstadoInicial();
        }
    }

    private void mostrarSimulacaoCompleta() {
        progressBar.setProgress(1.0);
        labelMensagem.setText(mensagens[mensagens.length - 1]);
        btnResetarSimulacao.setDisable(false);
        btnSimular.setDisable(true);  // ➜ ADIÇÃO: desabilita o botão se já ocorreu
    }

    private void resetarEstadoInicial() {
        progressBar.setProgress(0.0);
        labelMensagem.setText("");
        btnResetarSimulacao.setDisable(true);
        btnSimular.setDisable(false);  // ➜ garante que volte a habilitar ao resetar
    }

    @FXML
    private void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    private void simular() {
        btnSimular.setDisable(true);
        btnResetarSimulacao.setDisable(true);

        new Thread(() -> {
            try {
                Simulacao.simular();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            executarAnimacaoProgresso();

        }).start();
    }

    private void executarAnimacaoProgresso() {
        Platform.runLater(() -> progressBar.setProgress(0));

        for (int i = 1; i <= 100; i++) {
            final double progress = i / 100.0;

            Platform.runLater(() -> {
                progressBar.setProgress(progress);

                if (progress < 0.2) {
                    labelMensagem.setText(mensagens[0]);
                } else if (progress < 0.4) {
                    labelMensagem.setText(mensagens[1]);
                } else if (progress < 0.6) {
                    labelMensagem.setText(mensagens[2]);
                } else if (progress < 0.8) {
                    labelMensagem.setText(mensagens[3]);
                } else if (progress < 1.0) {
                    labelMensagem.setText(mensagens[4]);
                } else {
                    labelMensagem.setText(mensagens[5]);
                    btnResetarSimulacao.setDisable(false);
                    btnSimular.setDisable(true); // ➜ garante desativar ao fim da animação
                }
            });

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void resetarSimulacao() {
        try {
            Simulacao.resetar();
            Set<Partida> partidas = Simulacao.getPartidas();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resetarEstadoInicial();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação");
            alert.setHeaderText(null);
            alert.setContentText("Simulação resetada com sucesso!");
            alert.showAndWait();
        });
    }
}
