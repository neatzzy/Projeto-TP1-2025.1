package controller.UsrPartidaControllers;

import controller.NavigationManager;
import controller.SceneInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.event.ActionEvent;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Partida;
import model.Simulacao;

public class ControllerTelaRedirectPartidas {
    @FXML
    private Button menuMontagem;
    @FXML
    private TableView<Partida> tableConfrontos;
    @FXML
    private TableColumn<Partida, String> colMandante;
    @FXML
    private TableColumn<Partida, String> colVisitante;
    @FXML
    private TableColumn<Partida, Void> colDetalhes;

    private ObservableList<Partida> partidas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMandante.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClubeCasa().getNome()));
        colVisitante.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClubeFora().getNome()));
        colDetalhes.setCellFactory(getDetalhesCellFactory());

        partidas.addAll(Simulacao.getPartidas()); // Supondo que Simulacao tenha um método getPartidas() que retorna uma coleção de partidas
        tableConfrontos.setItems(partidas);
    }

    private Callback<TableColumn<Partida, Void>, TableCell<Partida, Void>> getDetalhesCellFactory() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("Detalhes");
            {
                btn.setOnAction((ActionEvent event) -> {
                    Partida partida = getTableView().getItems().get(getIndex());
                    // Abre a tela de detalhes da partida
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrPartidaScreens/TelaDetalhesPartida.fxml"));
                    try {
                        Stage stage = (Stage) menuMontagem.getScene().getWindow();

                        SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
                        NavigationManager.push(sceneInfo);

                        Scene scene = new Scene(loader.load());
                        ControllerTelaDetalhesPartida controller = loader.getController();
                        controller.setPartida(partida);
                        stage.setScene(scene);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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