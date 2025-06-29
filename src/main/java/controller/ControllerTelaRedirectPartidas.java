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

public class ControllerTelaRedirectPartidas {
    @FXML
    private Button btnVoltar;
    @FXML
    private TableView<Confronto> tableConfrontos;
    @FXML
    private TableColumn<Confronto, String> colMandante;
    @FXML
    private TableColumn<Confronto, String> colVisitante;
    @FXML
    private TableColumn<Confronto, Void> colDetalhes;

    private ObservableList<Confronto> confrontos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMandante.setCellValueFactory(new PropertyValueFactory<>("mandante"));
        colVisitante.setCellValueFactory(new PropertyValueFactory<>("visitante"));
        colDetalhes.setCellFactory(getDetalhesCellFactory());
        // Exemplo de dados
        confrontos.addAll(
            new Confronto("Time A", "Time B"),
            new Confronto("Time C", "Time D")
        );
        tableConfrontos.setItems(confrontos);
    }

    private Callback<TableColumn<Confronto, Void>, TableCell<Confronto, Void>> getDetalhesCellFactory() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("Detalhes");
            {
                btn.setOnAction((ActionEvent event) -> {
                    Confronto confronto = getTableView().getItems().get(getIndex());
                    // Ação ao clicar em detalhes
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
        Scene previous = NavigationManager.pop();
        if (previous != null) {
            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(previous);
        }
    }

    public static class Confronto {
        private String mandante;
        private String visitante;
        public Confronto(String mandante, String visitante) {
            this.mandante = mandante;
            this.visitante = visitante;
        }
        public String getMandante() { return mandante; }
        public void setMandante(String mandante) { this.mandante = mandante; }
        public String getVisitante() { return visitante; }
        public void setVisitante(String visitante) { this.visitante = visitante; }
    }
}
