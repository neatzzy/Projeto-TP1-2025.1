package controller;

import dao.ClubeDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaClubesUsuario{
    private List<Clube> clubes;
    private ClubeDAO clubeDAO;

    @FXML
    private FlowPane paneClubes;
    @FXML
    private Button btnVoltar;

    public void AbrirTelaClubes(Connection conn) {
        this.clubes = null;
        this.clubeDAO = new ClubeDAO(conn);
        try {
            clubes = clubeDAO.getAllClubes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        criarBotoesClubes();
    }

    private Button criarBotaoClube(String nomeClube) {
        Button btn = new Button(nomeClube);
        btn.setStyle("-fx-font-size: 18px; -fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 3, 0, 0, 2);");
        btn.setOnAction(event -> abrirClube(nomeClube));
        return btn;
    }

    private void abrirClube(String nomeClube) {
        // Lógica para abrir detalhes do clube selecionado
        int clubeId;
        try{
            clubeId = clubeDAO.getClubeIdByName(nomeClube);
            Clube clube = clubeDAO.getClubeById(clubeId);

            // Abrir tela do clube

            System.out.println("Abrindo detalhes do clube: " + nomeClube + " (ID: " + clubeId + ")");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao obter ID do clube: " + nomeClube);
        }
    }

    @FXML
    private void voltar() {
        // Lógica para voltar à tela anterior
    }

    public void criarBotoesClubes() {
        if (clubes == null || paneClubes == null) return;
        paneClubes.getChildren().clear();
        int count = 0;
        HBox linha = new HBox(16);
        linha.setAlignment(javafx.geometry.Pos.CENTER);
        for (Clube clube : clubes) {
            Button btn = criarBotaoClube(clube.getNome());
            linha.getChildren().add(btn);
            count++;
            if (count % 4 == 0) {
                paneClubes.getChildren().add(linha);
                linha = new HBox(16);
                linha.setAlignment(javafx.geometry.Pos.CENTER);
            }
        }
        if (!linha.getChildren().isEmpty()) {
            paneClubes.getChildren().add(linha);
        }
    }
}
