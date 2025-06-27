package controller;

import dao.ClubeDAO;
import dao.JogadorDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Clube;
import model.Jogador;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaVisualizarClube {
    @FXML
    private Label lblNomeClube;
    @FXML
    private TableView<Jogador> tableJogadores;
    @FXML
    private TableColumn<Jogador, String> colNome;
    @FXML
    private TableColumn<Jogador, String> colPosicao;
    @FXML
    private TableColumn<Jogador, Double> colPreco;
    @FXML
    private TableColumn<Jogador, Void> colVerDetalhes;
    @FXML
    private Button btnVoltar;

    private Clube clube;

    public void abrirTelaVisualizarClube(Connection conn, Clube clube) {
        this.clube = clube;
        ClubeDAO clubeDAO = new ClubeDAO(conn);
        JogadorDAO jogadorDAO = new JogadorDAO(conn, clubeDAO);
        if (lblNomeClube != null) {
            lblNomeClube.setText(getNomeClube());
        }
        try{
            List<Jogador> jogadores = jogadorDAO.getJogadoresByClub(clube);
            carregarJogadores(jogadores);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarJogadores(List<Jogador> jogadores) {
        tableJogadores.getItems().clear();
        tableJogadores.getItems().addAll(jogadores);
    }

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colPosicao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStringPosicao()));
        colPreco.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPreco()));
        if (colVerDetalhes != null) {
            colVerDetalhes.setCellFactory(criarBotaoVerDetalhes());
        }
    }

    private Callback<TableColumn<Jogador, Void>, TableCell<Jogador, Void>> criarBotaoVerDetalhes() {
        return new Callback<>() {
            @Override
            public TableCell<Jogador, Void> call(final TableColumn<Jogador, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Ver Detalhes");
                    {
                        btn.setStyle("-fx-background-color: #009688; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
                        btn.setOnAction(event -> {
                            Jogador jogador = getTableView().getItems().get(getIndex());
                            abrirDetalhesJogador(jogador);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
    }

    private void abrirDetalhesJogador(Jogador jogador) {
        // Carrega a tela de detalhes do jogador
    }

    @FXML
    private void btnVoltar() {
        // Lógica para voltar à tela anterior

    }

    public String getNomeClube() {
        return clube != null ? clube.getNome() : "";
    }
}
