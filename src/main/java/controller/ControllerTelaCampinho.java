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
    @FXML private Button botaoSalvar;

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

        if(Simulacao.getOcorreu()){
            mostrarAlerta("Erro", "A simulação já foi feita!.");
            voltar();
            return;
        }

        if (usuario.getLiga() == null) {
            mostrarAlerta("Erro", "Você precisa entrar em uma liga antes de montar seu time.");
            voltar();
            return;
        }

        try {

            timeusuario = usuario.getTimeUsuario();

            if (timeusuario == null) {
                timeusuario = timeDAO.getTimeById(usuario.getId());
                usuario.setTimeUsuario(timeusuario);
            }

            double preco = timeusuario.calcularPreco();
            timeusuario.setPreco(preco);
            usuario.setTimeUsuario(timeusuario);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        carregarJogadores();

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
            controller.setConnection(conn, usuario, timeDAO);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mercado");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir o mercado.");
            e.printStackTrace();
        }
    }

    private void carregarJogadores() {

        timeusuario.imprimirTime();

        // Limpa o estado visual antes de recarregar
        labelNomeClube.setText(timeusuario.getNome());

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

        selectCapitao.getItems().clear();
        selectCapitao.setValue(null);

        int goleiro = 0, zagueiro = 0, meio = 0, ataque = 0;

        for (Jogador jogador : timeusuario.getJogadores()) {
            switch (jogador.getPosicao()) {
                case GOLEIRO -> {
                    labelGoleiro.setText("GOL: " + jogador.getNome());
                    selectCapitao.getItems().add(jogador.getNome());
                    goleiro++;
                    goleiroButton.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                }
                case ZAGUEIRO -> {
                    if (++zagueiro == 1) {
                        labelZagueiro1.setText("ZAG: " + jogador.getNome());
                        zagueiroButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (zagueiro == 2) {
                        labelZagueiro2.setText("ZAG: " + jogador.getNome());
                        zagueiroButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (zagueiro == 3) {
                        labelZagueiro3.setText("ZAG: " + jogador.getNome());
                        zagueiroButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (zagueiro == 4) {
                        labelZagueiro4.setText("ZAG: " + jogador.getNome());
                        zagueiroButton4.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    selectCapitao.getItems().add(jogador.getNome());
                }
                case MEIA -> {
                    if (++meio == 1) {
                        labelMeio1.setText("MEI: " + jogador.getNome());
                        meioButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (meio == 2) {
                        labelMeio2.setText("MEI: " + jogador.getNome());
                        meioButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (meio == 3) {
                        labelMeio3.setText("MEI: " + jogador.getNome());
                        meioButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    selectCapitao.getItems().add(jogador.getNome());
                }
                case ATACANTE -> {
                    if (++ataque == 1) {
                        labelAtaque1.setText("ATA: " + jogador.getNome());
                        atacanteButton1.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (ataque == 2) {
                        labelAtaque2.setText("ATA: " + jogador.getNome());
                        atacanteButton2.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    else if (ataque == 3) {
                        labelAtaque3.setText("ATA: " + jogador.getNome());
                        atacanteButton3.setStyle("-fx-background-color: #43A047; -fx-background-radius: 50%; -fx-border-color: white; -fx-border-radius: 50%; -fx-border-width: 1.5;");
                    }
                    selectCapitao.getItems().add(jogador.getNome());
                }
            }
        }

        Jogador capitaoAtual = timeusuario.getCapitao();

        if (capitaoAtual != null) {
            boolean capitaoAindaNoTime = timeusuario.getJogadores().contains(capitaoAtual);
            if (capitaoAindaNoTime) {
                selectCapitao.setValue(capitaoAtual.getNome());
            } else {
                timeusuario.removeCapitao(); // remove o capitão do time
                selectCapitao.setValue(null); // limpa o campo visualmente
            }
        }

        double saldo = (150.0 - usuario.getTimeUsuario().getPreco());
        labelSaldo.setText("Saldo restante: " + String.format("%.2f", saldo) + "$");

        selectCapitao.setOnAction(e -> {
            String nomeSelecionado = selectCapitao.getValue();
            if (nomeSelecionado == null) return;
            Jogador capitao = usuario.getTimeUsuario().getJogadores().stream()
                    .filter(j -> j.getNome().equals(nomeSelecionado))
                    .findFirst().orElse(null);

            if (capitao == null) {
                mostrarAlerta("Erro", "Jogador não encontrado.");
                return;
            }

            usuario.getTimeUsuario().setCapitao(capitao);
            mostrarAlerta("Sucesso", capitao.getNome() + " foi definido como capitão!");
            });

    }

    @FXML
    private void salvarTime() {
        try {

            Jogador capitao = timeusuario.getCapitao();
            boolean timeValido = timeusuario.isValido();

            if(timeValido && capitao != null){
                timeDAO.setCapitao(usuario.getId(), capitao.getId());
                timeDAO.alterarTime(usuario.getId(), timeusuario.getJogadores());
                mostrarAlerta("Sucesso", "Time salvo com sucesso!");
            } else if (capitao != null){
                mostrarAlerta("Erro", "Time não é válido! A escalação deve ser feita do time completo!");
            } else {
                mostrarAlerta("Erro", "Lembre de selecionar um capitão!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao salvar time.");
        }
    }

    @FXML
    private void venderTimeCompleto() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação");
        confirmacao.setHeaderText("Certeza que deseja vender todo o time?");
        confirmacao.setContentText("Essa ação não poderá ser desfeita");

        ButtonType botaoSim = new ButtonType("Sim");
        ButtonType botaoNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmacao.getButtonTypes().setAll(botaoSim, botaoNao);

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == botaoSim) {
                try {
                    timeusuario.removerTodosJogadores();
                    timeDAO.removeCapitao(usuario.getId());
                    timeDAO.alterarTime(usuario.getId(), timeusuario.getJogadores());
                    usuario.setTimeUsuario(timeusuario);

                    carregarJogadores();
                    mostrarAlerta("Sucesso", "Todos os jogadores foram vendidos!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao vender o time completo.");
                }
            }
        });
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
