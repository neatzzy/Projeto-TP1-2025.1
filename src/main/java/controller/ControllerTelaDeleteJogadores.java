package controller;

import dao.ClubeDAO;
import dao.JogadorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ControllerTelaDeleteJogadores {

    private Connection conn;
    private ClubeDAO clubeDAO;
    private JogadorDAO jogadorDAO;

    @FXML private Button menuMontagem;
    @FXML private TextField tfBuscaJogador;
    @FXML private TableView<Jogador> tableJogadores;
    @FXML private TableColumn<Jogador, String> colNome;
    @FXML private TableColumn<Jogador, String> colClube;
    @FXML private TableColumn<Jogador, Double> colOVR;
    @FXML private TableColumn<Jogador, Double> colPreco;
    @FXML private TableColumn<Jogador, Void> colAcoes;

    private ObservableList<Jogador> jogadoresList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
    }

    private void setupColumns() {
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colClube.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClube().getNome()));
        colOVR.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOverall()));
        colPreco.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPreco()));

        addDeleteButtonToTable();
    }

    private void addDeleteButtonToTable() {
        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Deletar");

            {
                deleteButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-background-radius: 8;");
                deleteButton.setOnAction((ActionEvent event) -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    deletarJogador(jogador);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    public void carregarJogadores() {
        jogadoresList.clear();
        Set<Partida> partidas = Simulacao.getPartidas();

        for (Partida partida : partidas) {
            jogadoresList.addAll(partida.getClubeCasa().getJogadores());
            jogadoresList.addAll(partida.getClubeFora().getJogadores());
        }

        tableJogadores.setItems(jogadoresList);
    }

    @FXML
    public void buscarJogadores() {
        String termo = tfBuscaJogador.getText().toLowerCase().trim();

        if (termo.isEmpty()) {
            tableJogadores.setItems(jogadoresList);
            return;
        }

        List<Jogador> filtrados = jogadoresList.stream()
                .filter(j -> j.getNome().toLowerCase().contains(termo)
                        || j.getClube().getNome().toLowerCase().contains(termo))
                .collect(Collectors.toList());

        tableJogadores.setItems(FXCollections.observableArrayList(filtrados));
    }

    private void deletarJogador(Jogador jogador) {
        try {
            Clube clube = jogador.getClube();
            clube.setClubeDAO(clubeDAO);
            clube.setJogadorDAO(jogadorDAO);

            if (Simulacao.getOcorreu()) {
                mostrarAlerta("Não é possível excluir jogadores após a simulação.");
                return;
            }

            int id = jogador.getId();

            if (clube.removeJogador(conn, id)) {
                jogadoresList.remove(jogador);
                tableJogadores.setItems(null);
                tableJogadores.setItems(jogadoresList);
            } else {
                mostrarAlerta("Não foi possível excluir o jogador.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao excluir jogador: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.clubeDAO = new ClubeDAO(conn);
        this.jogadorDAO = new JogadorDAO(conn, clubeDAO);
        carregarJogadores();
    }
}
