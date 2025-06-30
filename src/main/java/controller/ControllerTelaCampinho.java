
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
import java.util.List;

public class ControllerTelaCampinho {

    @FXML private Button menuMontagem;
    @FXML private Label labelNomeClube;
    @FXML private Button mercadoButton;

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
    @FXML private ComboBox<String> selectCapitao;
    @FXML private Label labelSaldo;

    private Connection conn;
    private Usuario usuario;
    private TimeDAO timeDAO;

    public void setConnection(Connection conn, Usuario usuario) {
        this.conn = conn;
        this.usuario = usuario;
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));

        if(Simulacao.getOcorreu()){
            mostrarAlerta("Erro", "A simulação já foi feita!.");
            voltar();
            return;
        }

        try {

            if (usuario.getLiga() == null) {
                mostrarAlerta("Erro", "Você precisa entrar em uma liga antes de montar seu time.");
                voltar();
                return;
            }

            if (timeDAO.getTimeById(usuario.getId()) == null) {
                timeDAO.insertTime(usuario.getId(), "Time de " + usuario.getNome(), usuario.getLiga().getId());
            }

            TimeUsuario timeUsuario = timeDAO.getTimeById(usuario.getId());
            usuario.setTimeUsuario(timeUsuario); // atualiza o objeto em memória

            labelNomeClube.setText(timeUsuario.getNome());

            carregarJogadores();

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar informações do time.");
            e.printStackTrace();
        }

    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void abrirMercado() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrEscalarScreens/TelaMercado.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), "Menu do Usuário");
            NavigationManager.push(sceneInfo);

            ControllerTelaMercado controller = (ControllerTelaMercado) loader.getController();
            controller.setConnection(conn, timeDAO.getTimeById(usuario.getId()), usuario);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mercado");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir o mercado.");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void carregarJogadores() {
        try {

            TimeUsuario timeusuario = timeDAO.getTimeById(usuario.getId());

            int goleiro = 0, zagueiro = 0, meio = 0, ataque = 0;

            for (Jogador jogador : timeusuario.getJogadores()) {
                switch (jogador.getPosicao()) {
                    case GOLEIRO -> {
                        labelGoleiro.setText("GOL: " + jogador.getNome());
                        selectCapitao.getItems().add(jogador.getNome());
                        goleiro++;
                    }
                    case ZAGUEIRO -> {
                        if (++zagueiro == 1) labelZagueiro1.setText("ZAG: " + jogador.getNome());
                        else if (zagueiro == 2) labelZagueiro2.setText("ZAG: " + jogador.getNome());
                        else if (zagueiro == 3) labelZagueiro3.setText("ZAG: " + jogador.getNome());
                        else if (zagueiro == 4) labelZagueiro4.setText("ZAG: " + jogador.getNome());
                        selectCapitao.getItems().add(jogador.getNome());
                    }
                    case MEIA -> {
                        if (++meio == 1) labelMeio1.setText("MEI: " + jogador.getNome());
                        else if (meio == 2) labelMeio2.setText("MEI: " + jogador.getNome());
                        else if (meio == 3) labelMeio3.setText("MEI: " + jogador.getNome());
                        selectCapitao.getItems().add(jogador.getNome());
                    }
                    case ATACANTE -> {
                        if (++ataque == 1) labelAtaque1.setText("ATA: " + jogador.getNome());
                        else if (ataque == 2) labelAtaque2.setText("ATA: " + jogador.getNome());
                        else if (ataque == 3) labelAtaque3.setText("ATA: " + jogador.getNome());
                        selectCapitao.getItems().add(jogador.getNome());
                    }
                }
            }

            double saldo = (100.0 - usuario.getTimeUsuario().getPreco());
            labelSaldo.setText("Saldo restante: " + String.format("%.2f", saldo) + "$");

            selectCapitao.setOnAction(e -> {
                String nomeSelecionado = selectCapitao.getValue();
                if (nomeSelecionado == null) return;

                try {

                    Jogador capitao = usuario.getTimeUsuario().getJogadores().stream()
                            .filter(j -> j.getNome().equals(nomeSelecionado))
                            .findFirst().orElse(null);

                    if (capitao == null) {
                        mostrarAlerta("Erro", "Jogador não encontrado.");
                        return;
                    }

                    timeDAO.setCapitao(usuario.getId(), capitao.getId());
                    usuario.getTimeUsuario().setCapitao(capitao);
                    mostrarAlerta("Sucesso", capitao.getNome() + " foi definido como capitão!");


                } catch (SQLException ex) {
                    ex.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao definir capitão.");
                }
            });

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar jogadores.");
            e.printStackTrace();
        }
    }

    private void salvarTime(){
        // Funcionalidade para salvar o time do usuário
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}
