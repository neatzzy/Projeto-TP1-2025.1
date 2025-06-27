package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Jogador;

public class ControllerTelaViewJogador {
    @FXML
    private Label labelTitulo;
    @FXML
    private Label labelPosicao;
    @FXML
    private Label labelClube;
    @FXML
    private Label labelPreco;
    @FXML
    private Label labelPontuacao;
    @FXML
    private VBox statsBox;

    public void AbrirTelaViewJogador(Jogador jogador) {
        if (labelTitulo != null) {
            setDadosJogador(jogador.getNome(), jogador.getPosicao().toString(),
                            jogador.getClube().getNome(),
                            String.format("%.2f", jogador.getPreco()),
                            String.format("%.0f", jogador.getOverall()));

        }
        if (statsBox != null) {
            statsBox.getChildren().clear();
            jogador.getStats().forEach((entry) -> {
                String stat = entry.getKey();
                int valor = entry.getValue();
                if (valor != 0) {
                    Label statLabel = new Label(stat + ": " + valor);
                    statsBox.getChildren().add(statLabel);
                }
            });
        }
    }

    public void setDadosJogador(String nome, String posicao, String clube, String preco, String pontuacao) {
        labelTitulo.setText(nome);
        labelPosicao.setText(posicao);
        labelClube.setText(clube);
        labelPreco.setText(preco);
        labelPontuacao.setText(pontuacao);
    }

    public VBox getStatsBox() {
        return statsBox;
    }

    // Exemplo de método para abrir o menu, se necessário
    @FXML
    private void abrirMenu() {
        // Lógica para abrir o menu lateral ou voltar
    }


}