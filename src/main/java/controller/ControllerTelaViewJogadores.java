package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Jogador;
import model.Partida;
import model.Simulacao;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTelaViewJogadores {

    @FXML private Button menuMontagem;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TextField campoPesquisa;
    @FXML private TableView<Jogador> tableView;
    @FXML private TableColumn<Jogador, String> colNome;
    @FXML private TableColumn<Jogador, String> colPosicao;
    @FXML private TableColumn<Jogador, String> colClube;
    @FXML private TableColumn<Jogador, Double> colPontuacao;
    @FXML private TableColumn<Jogador, Void> colDetalhes;

    private ObservableList<Jogador> listaJogadores = FXCollections.observableArrayList();
    private ObservableList<Jogador> jogadoresFiltrados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboBoxFiltro.getItems().addAll("Todos", "ATACANTE", "MEIA", "ZAGUEIRO", "GOLEIRO");
        comboBoxFiltro.setValue("Todos");

        // Configuração de colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPosicao.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStringPosicao()));
        colClube.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClube().getNome()));

        // Pontuação como Double para ordenação correta
        colPontuacao.setCellValueFactory(new PropertyValueFactory<>("pontuacao"));
        colPontuacao.setCellFactory(column -> new TableCell<>() {
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

        // Adiciona os botões de detalhes
        addButtonDetalhes();

        // Pega todos os jogadores de todas as partidas
        listaJogadores.clear();
        for (Partida partida : Simulacao.getPartidas()) {
            listaJogadores.addAll(partida.getAllJogadores());
        }

        // Inicializa a tabela com todos os jogadores
        jogadoresFiltrados.setAll(listaJogadores);
        tableView.setItems(jogadoresFiltrados);
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void pesquisarJogador() {
        String filtroPosicao = comboBoxFiltro.getValue();
        String termo = campoPesquisa.getText().toLowerCase();

        List<Jogador> filtrados = listaJogadores.stream()
                .filter(j -> "Todos".equals(filtroPosicao) || j.getStringPosicao().equalsIgnoreCase(filtroPosicao))
                .filter(j -> j.getNome().toLowerCase().contains(termo))
                .collect(Collectors.toList());

        jogadoresFiltrados.setAll(filtrados);
    }

    private void addButtonDetalhes() {
        colDetalhes.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Detalhes");

            {
                btn.setOnAction(event -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    abrirDetalhesJogador(jogador);
                });
                btn.setStyle("-fx-background-color: rgba(255,255,255,0.12); " +
                        "-fx-background-radius: 10; " +
                        "-fx-text-fill: green; -fx-font-size: 14px; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void abrirDetalhesJogador(Jogador jogador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrJogadorScreens/TelaViewJogador.fxml"));
            Parent root = loader.load();

            ControllerTelaViewJogador controller = loader.getController();
            controller.setJogador(jogador);
            controller.getTableStats(jogador);

            Stage stage = (Stage) tableView.getScene().getWindow();
            NavigationManager.push(new SceneInfo(stage.getScene(), stage.getTitle()));

            stage.setScene(new Scene(root));
            stage.setTitle("Detalhes: " + jogador.getNome());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
