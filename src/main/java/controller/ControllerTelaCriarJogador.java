package controller;

import dao.ClubeDAO;
import dao.JogadorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTelaCriarJogador {

    private ClubeDAO clubeDAO;
    private JogadorDAO jogadorDAO;

    @FXML
    private Button menuMontagem;

    @FXML
    private Button btnSalvar;

    @FXML
    private TextField tfNome;

    @FXML
    private ComboBox<Clube> cbClube;

    @FXML
    private ComboBox<String> cbPosicao;

    @FXML
    private TextField tfPreco;

    @FXML
    private TextField tfOverall;

    @FXML
    public void initialize() {
        carregarClubes();
        carregarPosicoes();
    }

    public void setConnection(Connection conn) {
        clubeDAO = new ClubeDAO(conn);
        jogadorDAO = new JogadorDAO(conn, clubeDAO);
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private void carregarClubes() {
        List<Clube> clubesDisponiveis = Simulacao.getClubes();  // Ajuste conforme sua classe Simulacao
        cbClube.setItems(FXCollections.observableArrayList(clubesDisponiveis));
    }

    private void carregarPosicoes() {
        ObservableList<String> posicoes = FXCollections.observableArrayList("GOL", "ZAG", "MEI", "ATA");
        cbPosicao.setItems(posicoes);
    }

    @FXML
    public void salvarJogador() {
        if (Simulacao.getOcorreu()) {
            showAlert("Erro", "Não é possível adicionar jogadores após a simulação ter ocorrido.");
            return;
        }

        String nome = tfNome.getText();
        String posicaoStr = cbPosicao.getValue();
        Clube clubeSelecionado = cbClube.getValue();
        clubeSelecionado.setJogadorDAO(jogadorDAO);
        clubeSelecionado.setClubeDAO(clubeDAO);
        String precoStr = tfPreco.getText();
        String overallStr = tfOverall.getText();

        if (nome == null || nome.trim().isEmpty() ||
                posicaoStr == null ||
                clubeSelecionado == null ||
                precoStr == null || precoStr.trim().isEmpty() ||
                overallStr == null || overallStr.trim().isEmpty()) {
            showAlert("Erro", "Todos os campos devem ser preenchidos.");
            return;
        }

        double preco;
        double overall;
        try {
            preco = Double.parseDouble(precoStr);
            overall = Double.parseDouble(overallStr);
        } catch (NumberFormatException e) {
            showAlert("Erro", "Preço e OVR devem ser números válidos.");
            return;
        }

        // Mapear texto para Enum Posicao
        Posicao posicao;
        switch (posicaoStr) {
            case "GOL": posicao = Posicao.GOLEIRO; break;
            case "ZAG": posicao = Posicao.ZAGUEIRO; break;
            case "MEI": posicao = Posicao.MEIA; break;
            case "ATA": posicao = Posicao.ATACANTE; break;
            default:
                showAlert("Erro", "Posição inválida.");
                return;
        }

        try {
            clubeSelecionado.addJogador(nome, posicao, preco, overall);
            showAlert("Sucesso", "Jogador adicionado com sucesso!");
            limparCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao adicionar jogador: " + e.getMessage());
        }
    }

    private void limparCampos() {
        tfNome.clear();
        cbPosicao.setValue(null);
        cbClube.setValue(null);
        tfPreco.clear();
        tfOverall.clear();
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
