package controller;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Clube;
import model.Jogador;
import database.Database;
import dao.ClubeDAO;
import dao.JogadorDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaMercado {

    private Connection conn;
    private Usuario usuario;

    @FXML
    private ComboBox<String> comboBoxFiltro;

    @FXML
    private TextField campoPesquisa;

    @FXML
    private Button botaoPesquisar;

    @FXML
    private Button botaoVoltar;

    @FXML
    private TableView<Jogador> tableView;

    @FXML
    private TableColumn<Jogador, String> colTime;

    @FXML
    private TableColumn<Jogador, String> colPosicao;

    @FXML
    private TableColumn<Jogador, String> colJogador;

    @FXML
    private TableColumn<Jogador, String> colPreco;

    @FXML
    private TableColumn<Jogador, String> colComprar;

    private ObservableList<Jogador> listaJogadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            ClubeDAO clubeDAO = new ClubeDAO(conn);
            JogadorDAO jogadorDAO = new JogadorDAO(conn, clubeDAO);

            List<Clube> clubes = clubeDAO.getAllClubes();
            jogadorDAO.getAllJogadores(clubes);

            for (Clube clube : clubes) {
                listaJogadores.addAll(clube.getJogadores());
            }

            tableView.setItems(listaJogadores);

            colTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClube().getNome()));
            colPosicao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStringPosicao()));
            colJogador.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
            colPreco.setCellValueFactory(data -> new SimpleStringProperty("C$" + String.format("%.2f", data.getValue().getPreco())));

            colComprar.setCellFactory(col -> {
                Button btn = new Button("Comprar");
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;");
                TableCell<Jogador, String> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
                        }
                    }
                };
                btn.setOnAction(event -> {
                    Jogador jogador = cell.getTableView().getItems().get(cell.getIndex());
                    System.out.println("Comprou: " + jogador.getNome());
                });
                return cell;
            });

            comboBoxFiltro.setOnAction(e -> filtrar());
            botaoPesquisar.setOnAction(e -> pesquisarJogador());

        } catch (SQLException e) {
            e.printStackTrace();
            // aqui você pode exibir um alerta, logar no sistema etc.
        }
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    private void filtrar() {
        String filtro = comboBoxFiltro.getValue();
        String pesquisa = campoPesquisa.getText() != null ? campoPesquisa.getText().trim().toLowerCase() : "";

        ObservableList<Jogador> filtrados = listaJogadores.filtered(j -> {
            boolean posicaoOk = filtro.equals("Todos") || j.getStringPosicao().equals(filtro);
            boolean nomeOk = pesquisa.isEmpty() || j.getNome().toLowerCase().contains(pesquisa);
            return posicaoOk && nomeOk;
        });
        tableView.setItems(filtrados);
    }


    @FXML
    public void abrirTelaMercado() {
        // Implementa a lógica para abrir a tela de mercado
    }

    @FXML
    private void pesquisarJogador() {
        filtrar();
    }

    @FXML
    private void voltar() {
        // Implementa a lógica para voltar à tela anterior
    }
}