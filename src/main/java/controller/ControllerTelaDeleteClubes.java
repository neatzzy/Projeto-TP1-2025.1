package controller;

import dao.ClubeDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Clube;
import model.Partida;
import model.Simulacao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerTelaDeleteClubes {

    private Connection conn;
    private ClubeDAO clubeDAO;

    @FXML private Button menuMontagem;
    @FXML private TableView<Clube> tableClubes;
    @FXML private TableColumn<Clube, String> colNome;
    @FXML private TableColumn<Clube, Number> colId;
    @FXML private Button btnDeletar;

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.clubeDAO = new ClubeDAO(conn);
        carregarClubes();
    }

    @FXML
    public void initialize() {
        // Configurar as colunas
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));

        // Permitir seleção múltipla
        tableClubes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void carregarClubes() {
        try {
            List<Clube> clubes = clubeDAO.getAllClubes();
            tableClubes.getItems().setAll(clubes);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao carregar clubes do banco de dados.");
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void deletarClubes() {
        List<Clube> selecionados = new ArrayList<>(tableClubes.getSelectionModel().getSelectedItems());

        if (selecionados.size() != 2) {
            showAlert("Selecione exatamente dois clubes para deletar!");
            return;
        }

        // 1. Remover do banco de dados
        for (Clube clube : selecionados) {
            clubeDAO.deleteClubeById(clube.getId());
        }

        // 2. Atualizar partidas
        List<Clube> adversarios = new ArrayList<>();
        List<Partida> partidas = new ArrayList<>();

        for (Partida p : Simulacao.getPartidas()){
            if(p.getClubeCasa().getId() == selecionados.get(0).getId()){
                adversarios.add(p.getClubeFora());
                partidas.add(p);
                break;
            }
            else if (p.getClubeFora().getId() == selecionados.get(0).getId()){
                adversarios.add(p.getClubeCasa());
                partidas.add(p);
                break;
            }
        }
        for (Partida p : Simulacao.getPartidas()){
            if(p.getClubeCasa().getId() == selecionados.get(1).getId()){
                adversarios.add(p.getClubeFora());
                partidas.add(p);
                break;
            }
            else if (p.getClubeFora().getId() == selecionados.get(1).getId()){
                adversarios.add(p.getClubeCasa());
                partidas.add(p);
                break;
            }
        }
        if (partidas.get(0).getClubeCasa().getId() == partidas.get(1).getClubeCasa().getId() && partidas.get(0).getClubeFora().getId() == partidas.get(1).getClubeFora().getId()){
            partidas.remove(1);
            Simulacao.removePartida(partidas.get(0));
        }
        else{
            Simulacao.removePartida(partidas.get(0));
            Simulacao.removePartida(partidas.get(1));
            adversarios.get(0).setPartida(false);
            adversarios.get(1).setPartida(false);
            Simulacao.addPartida(adversarios.get(0), adversarios.get(1));
        }

        showAlert("Clubes deletados com sucesso!");
        carregarClubes();
    }



    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
