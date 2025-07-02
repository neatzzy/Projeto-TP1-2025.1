package controller;

import dao.ClubeDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Clube;
import model.Simulacao;

import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaCriarClubes {

    private ClubeDAO clubeDAO;

    @FXML
    private Button menuMontagem;

    @FXML
    private Button botaoCriar;

    @FXML
    private TextField campoNomeClube1;

    @FXML
    private TextField campoNomeClube2;

    public void setConnection(Connection conn) {
        this.clubeDAO = new ClubeDAO(conn);
    }

    @FXML
    public void initialize() {
        botaoCriar.setOnAction(e -> criarClubesEPartida());
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private void criarClubesEPartida() {
        String nome1 = campoNomeClube1.getText().trim();
        String nome2 = campoNomeClube2.getText().trim();

        if (nome1.isEmpty() || nome2.isEmpty()) {
            mostrarAlerta("Erro", "Ambos os nomes devem ser preenchidos!");
            return;
        }

        try {
            // Insere no banco e recupera os IDs
            int id1 = clubeDAO.insertClube(nome1, 0.0, 0.0);
            int id2 = clubeDAO.insertClube(nome2, 0.0, 0.0);

            // Constrói objetos Clube a partir dos dados salvos
            Clube clube1 = clubeDAO.getClubeById(id1);
            Clube clube2 = clubeDAO.getClubeById(id2);

            if (clube1 == null || clube2 == null) {
                mostrarAlerta("Erro", "Erro ao recuperar clubes criados.");
                return;
            }

            // Adiciona a partida à Simulacao
            boolean sucesso = Simulacao.addPartida(clube1, clube2);

            if (!sucesso) {
                mostrarAlerta("Aviso", "Não foi possível criar partida (clubes já podem estar em partidas existentes).");
                return;
            }

            mostrarAlerta("Sucesso", "Clubes criados e partida adicionada com sucesso!");

            // Limpa os campos para o usuário
            campoNomeClube1.clear();
            campoNomeClube2.clear();

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta("Erro", "Erro ao acessar o banco de dados: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
