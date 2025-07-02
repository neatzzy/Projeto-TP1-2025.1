package controller;

import dao.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ControllerTelaVisualizarCampinho {

    @FXML private Button menuMontagem;
    @FXML private Label labelNomeClube;

    @FXML private Label labelGoleiro;
    @FXML private Label labelZagueiro1;
    @FXML private Label labelZagueiro2;
    @FXML private Label labelZagueiro3;
    @FXML private Label labelZagueiro4;
    @FXML private Label labelMeio1;
    @FXML private Label labelMeio2;
    @FXML private Label labelMeio3;
    @FXML private Label labelAtaque1;
    @FXML private Label labelAtaque2;
    @FXML private Label labelAtaque3;
    @FXML private Label labelSaldo;

    @FXML private Label labelCapitao;
    @FXML private Label labelPontuacaoTotal;


    // Botões das posições (bolinhas)
    @FXML private Button goleiroButton;
    @FXML private Button zagueiroButton1;
    @FXML private Button zagueiroButton2;
    @FXML private Button zagueiroButton3;
    @FXML private Button zagueiroButton4;
    @FXML private Button meioButton1;
    @FXML private Button meioButton2;
    @FXML private Button meioButton3;
    @FXML private Button atacanteButton1;
    @FXML private Button atacanteButton2;
    @FXML private Button atacanteButton3;

    private Connection conn;
    private Usuario usuario;
    private TimeUsuario timeusuario;
    private TimeDAO timeDAO;

    public void setConnection(Connection conn, Usuario usuario) {

        this.conn = conn;
        this.usuario = usuario;
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));

        try {
            timeusuario = timeDAO.getTimeById(this.usuario.getId());

            double preco = timeusuario.calcularPreco();
            timeusuario.setPreco(preco);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        carregarJogadores();

    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private void carregarJogadores() {

        timeusuario.imprimirTime();

        // Limpa o estado visual antes de recarregar
        labelNomeClube.setText(timeusuario.getNome());
        labelCapitao.setText("Capitão: " + timeusuario.getCapitao().getNome());

        List<Jogador> novaLista = new ArrayList<>();

        for (Jogador jogador : timeusuario.getJogadores()) {
            Jogador atualizado = jogador; // default

            for (Partida p : Simulacao.getPartidas()) {
                Clube clube = null;
                if (jogador.getClube().getId() == p.getClubeCasa().getId()) {
                    clube = p.getClubeCasa();
                } else if (jogador.getClube().getId() == p.getClubeFora().getId()) {
                    clube = p.getClubeFora();
                }

                if (clube != null) {
                    for (Jogador j : clube.getJogadores()) {
                        if (jogador.getId() == j.getId()) {
                            atualizado = j;
                            break;
                        }
                    }
                    break;
                }
            }
            novaLista.add(atualizado);
        }

        timeusuario.setJogadores(new HashSet<>(novaLista));

        timeusuario.calcularPontuacao();
        double pontuacao = 0;
        for (Jogador jogadori : novaLista) {
            pontuacao += jogadori.getPontuacao();
            if (jogadori.getId() == timeusuario.getCapitao().getId()){
                pontuacao += jogadori.getPontuacao();
            }
        }

        labelPontuacaoTotal.setText("Total: " + String.format("%.2f", pontuacao));

        labelGoleiro.setText("GOL: ?");
        labelZagueiro1.setText("ZAG: ?");
        labelZagueiro2.setText("ZAG: ?");
        labelZagueiro3.setText("ZAG: ?");
        labelZagueiro4.setText("ZAG: ?");
        labelMeio1.setText("MEI: ?");
        labelMeio2.setText("MEI: ?");
        labelMeio3.setText("MEI: ?");
        labelAtaque1.setText("ATA: ?");
        labelAtaque2.setText("ATA: ?");
        labelAtaque3.setText("ATA: ?");

        goleiroButton.setStyle(defaultStyle());
        zagueiroButton1.setStyle(defaultStyle());
        zagueiroButton2.setStyle(defaultStyle());
        zagueiroButton3.setStyle(defaultStyle());
        zagueiroButton4.setStyle(defaultStyle());
        meioButton1.setStyle(defaultStyle());
        meioButton2.setStyle(defaultStyle());
        meioButton3.setStyle(defaultStyle());
        atacanteButton1.setStyle(defaultStyle());
        atacanteButton2.setStyle(defaultStyle());
        atacanteButton3.setStyle(defaultStyle());

        int goleiro = 0, zagueiro = 0, meio = 0, ataque = 0;

        for (Jogador jogador : timeusuario.getJogadores()) {
            String texto = jogador.getNome() + " (Pts: " + String.format("%.2f", jogador.getPontuacao()) + ")";
            switch (jogador.getPosicao()) {
                case GOLEIRO -> {
                    labelGoleiro.setText("GOL: " + texto);
                    goleiro++;
                    goleiroButton.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                }
                case ZAGUEIRO -> {
                    if (++zagueiro == 1) {
                        labelZagueiro1.setText("ZAG: " + texto);
                        zagueiroButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (zagueiro == 2) {
                        labelZagueiro2.setText("ZAG: " + texto);
                        zagueiroButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (zagueiro == 3) {
                        labelZagueiro3.setText("ZAG: " + texto);
                        zagueiroButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (zagueiro == 4) {
                        labelZagueiro4.setText("ZAG: " + texto);
                        zagueiroButton4.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                }
                case MEIA -> {
                    if (++meio == 1) {
                        labelMeio1.setText("MEI: " + texto);
                        meioButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (meio == 2) {
                        labelMeio2.setText("MEI: " + texto);
                        meioButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (meio == 3) {
                        labelMeio3.setText("MEI: " + texto);
                        meioButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                }
                case ATACANTE -> {
                    if (++ataque == 1) {
                        labelAtaque1.setText("ATA: " + texto);
                        atacanteButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (ataque == 2) {
                        labelAtaque2.setText("ATA: " + texto);
                        atacanteButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    } else if (ataque == 3) {
                        labelAtaque3.setText("ATA: " + texto);
                        atacanteButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                }
            }
        }


        double saldo = (150.0 - timeusuario.getPreco());
        labelSaldo.setText("Saldo restante: " + String.format("%.2f", saldo) + "$");

    }

    private String defaultStyle() {
        return "-fx-background-color: transparent; -fx-background-radius: 50%; -fx-border-color: white;" +
                " -fx-border-radius: 50%; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, black, 4, 0, 0, 1);";
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
