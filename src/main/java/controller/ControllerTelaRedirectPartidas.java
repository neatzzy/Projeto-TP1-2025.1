package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.event.ActionEvent;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Partida;
import model.Clube;

public class ControllerTelaRedirectPartidas {
    @FXML
    private Button btnVoltar;
    @FXML
    private TableView<Partida> tableConfrontos;
    @FXML
    private TableColumn<Partida, String> colMandante;
    @FXML
    private TableColumn<Partida, String> colVisitante;
    @FXML
    private TableColumn<Partida, Void> colDetalhes;
    @FXML
    private Button menuMontagem;

    private ObservableList<Partida> partidas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMandante.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClubeCasa().getNome()));
        colVisitante.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClubeFora().getNome()));
        colDetalhes.setCellFactory(getDetalhesCellFactory());
        // Exemplo de dados (mock de clubes)
        Clube clubeA = new Clube("Time A");
        Clube clubeB = new Clube("Time B");
        Clube clubeC = new Clube("Time C");
        Clube clubeD = new Clube("Time D");
        partidas.addAll(
                new Partida(clubeA, clubeB),
                new Partida(clubeC, clubeD)
        );
        tableConfrontos.setItems(partidas);
    }

    private Callback<TableColumn<Partida, Void>, TableCell<Partida, Void>> getDetalhesCellFactory() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("Detalhes");
            {
                btn.setOnAction((ActionEvent event) -> {
                    Partida partida = getTableView().getItems().get(getIndex());
                    // Exemplo de ação: mostrar nomes dos clubes
                    System.out.println("Casa: " + partida.getClubeCasa().getNome() + ", Fora: " + partida.getClubeFora().getNome());
                });
                btn.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-background-color: #fff; -fx-text-fill: #004D40;");
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

    @FXML
    private void voltar(ActionEvent event) {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}