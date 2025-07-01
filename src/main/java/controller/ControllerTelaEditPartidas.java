package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Clube;
import model.Partida;
import model.Simulacao;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerTelaEditPartidas {

    @FXML private Button menuMontagem;
    @FXML private TableView<Partida> tableConfrontos;
    @FXML private TableColumn<Partida, String> colMandante;
    @FXML private TableColumn<Partida, String> colVisitante;
    @FXML private ComboBox<Clube> comboClube1;
    @FXML private ComboBox<Clube> comboClube2;
    @FXML private Button btnTrocar;
    @FXML private Button btnAleatorizar;

    private final ObservableList<Partida> partidas = FXCollections.observableArrayList();
    private final ObservableList<Clube> clubesDisponiveis = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar colunas da tabela
        colMandante.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClubeCasa().getNome()));
        colVisitante.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClubeFora().getNome()));

        atualizarTabelaECombos();

        btnTrocar.setOnAction(e -> trocarClubes());
        btnAleatorizar.setOnAction(e -> aleatorizarPartidas());
    }

    private void atualizarTabelaECombos() {
        partidas.setAll(Simulacao.getPartidas());
        tableConfrontos.setItems(partidas);

        // Pegar clubes únicos
        Set<Clube> clubesUnicos = new HashSet<>();
        for (Partida p : partidas) {
            clubesUnicos.add(p.getClubeCasa());
            clubesUnicos.add(p.getClubeFora());
        }

        // Ordenar por nome (alfabética, sem case-sensitive)
        List<Clube> clubesOrdenados = clubesUnicos.stream()
                .sorted(Comparator.comparing(Clube::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        clubesDisponiveis.setAll(clubesOrdenados);

        // Configurar ComboBox para exibir apenas os nomes
        setupComboBox(comboClube1);
        setupComboBox(comboClube2);

        comboClube1.setItems(clubesDisponiveis);
        comboClube2.setItems(clubesDisponiveis);
    }

    private void setupComboBox(ComboBox<Clube> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Clube item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Clube item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });
    }

    private void trocarClubes() {
        Clube clube1 = comboClube1.getValue();
        Clube clube2 = comboClube2.getValue();

        if (clube1 == null || clube2 == null) {
            showAlert("Seleção inválida", "Selecione dois clubes para trocar.");
            return;
        }

        if (clube1.equals(clube2)) {
            showAlert("Seleção inválida", "Selecione dois clubes diferentes.");
            return;
        }

        boolean sucesso = Simulacao.trocarClubes(clube1, clube2);

        if (sucesso) {
            showAlert("Sucesso", "Clubes trocados com sucesso!");
            atualizarTabelaECombos();
        } else {
            showAlert("Erro", "Não foi possível trocar os clubes.");
        }
    }

    private void aleatorizarPartidas() {
        List<Clube> todosClubes = new ArrayList<>();
        for (Partida p : Simulacao.getPartidas()) {
            todosClubes.add(p.getClubeCasa());
            todosClubes.add(p.getClubeFora());
        }

        Simulacao.clear();

        boolean gerado = Simulacao.gerarPartidasAleatorias(todosClubes);

        if (gerado) {
            showAlert("Sucesso", "Partidas geradas aleatoriamente!");
        } else {
            showAlert("Erro", "Número ímpar de clubes ou erro na geração.");
        }

        atualizarTabelaECombos();
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}
