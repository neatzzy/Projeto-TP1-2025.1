package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Partida;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Clube;
import model.Jogador;

public class ControllerTelaDetalhesPartida {
    private Partida partida;

    @FXML private Button menuMontagem;
    @FXML private Label lblTimeA;
    @FXML private Label lblTimeB;
    @FXML private Label lblPlacar;
    @FXML private Label lblPontuacaoA;
    @FXML private Label lblPontuacaoB;
    @FXML private TabPane tabTimes;
    @FXML private Tab tabTimeA;
    @FXML private Tab tabTimeB;
    @FXML private TableView<Jogador> tableTimeA;
    @FXML private TableView<Jogador> tableTimeB;
    @FXML private TableColumn<Jogador, String> colPosicaoA;
    @FXML private TableColumn<Jogador, String> colNomeA;
    @FXML private TableColumn<Jogador, Double> colPontuacaoA;
    @FXML private TableColumn<Jogador, String> colPosicaoB;
    @FXML private TableColumn<Jogador, String> colNomeB;
    @FXML private TableColumn<Jogador, Double> colPontuacaoB;
    @FXML private TableColumn<Jogador, Void> colDetalhesA;
    @FXML private TableColumn<Jogador, Void> colDetalhesB;

    public void setPartida(Partida partida) {
        this.partida = partida;
        preencherTela();
    }

    @FXML
    public void initialize() {
        // Configura colunas do time A
        colPosicaoA.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStringPosicao()));
        colNomeA.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colPontuacaoA.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPontuacao()));
        colPontuacaoA.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        // Configura colunas do time B
        colPosicaoB.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStringPosicao()));
        colNomeB.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colPontuacaoB.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPontuacao()));
        colPontuacaoB.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        // Botão detalhes A
        colDetalhesA.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Ver detalhes");
            {
                btn.setOnAction(event -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    abrirTelaViewJogador(jogador);
                });
                btn.setStyle("-fx-font-size: 13px; -fx-background-radius: 8; -fx-background-color: #fff; -fx-text-fill: #004D40;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Botão detalhes B
        colDetalhesB.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Ver detalhes");
            {
                btn.setOnAction(event -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    abrirTelaViewJogador(jogador);
                });
                btn.setStyle("-fx-font-size: 13px; -fx-background-radius: 8; -fx-background-color: #fff; -fx-text-fill: #004D40;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void preencherTela() {
        if (partida == null) return;
        Clube clubeA = partida.getClubeCasa();
        Clube clubeB = partida.getClubeFora();
        lblTimeA.setText(clubeA.getNome());
        lblTimeB.setText(clubeB.getNome());
        // Placar
        lblPlacar.setText(partida.getGolsClubeCasa() + "  ×  " + partida.getGolsClubeFora());
        // Pontuação com 2 casas decimais
        lblPontuacaoA.setText(String.format("%.2f", calcularPontuacao(clubeA)));
        lblPontuacaoB.setText(String.format("%.2f", calcularPontuacao(clubeB)));

        // Jogadores
        ObservableList<Jogador> jogadoresA = FXCollections.observableArrayList(clubeA.getJogadores());
        ObservableList<Jogador> jogadoresB = FXCollections.observableArrayList(clubeB.getJogadores());
        tableTimeA.setItems(jogadoresA);
        tableTimeB.setItems(jogadoresB);
        tabTimeA.setText(clubeA.getNome());
        tabTimeB.setText(clubeB.getNome());
    }

    private double calcularPontuacao(Clube clube) {
        return clube.getJogadores().stream().mapToDouble(Jogador::getPontuacao).sum();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private void abrirTelaViewJogador(Jogador jogador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrJogadorScreens/TelaViewJogador.fxml"));
            AnchorPane root = loader.load();
            ControllerTelaViewJogador controller = loader.getController();
            controller.setJogador(jogador);
            controller.getTableStats(jogador);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes do Jogador: " + jogador.getNome());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
