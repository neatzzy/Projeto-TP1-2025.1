package controller;

import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Liga;
import model.Pessoa;
import model.UserType;
import model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ControllerTelaViewLigas {

    @FXML private Button menuMontagem;
    @FXML private TextField tfBuscaLiga;
    @FXML private Button btnBuscar;
    @FXML private VBox ligasBox;

    private Connection conn;
    private LigaDAO ligaDAO;

    @FXML
    public void initialize() {
        btnBuscar.setOnAction(e -> buscarLigas());
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.ligaDAO = new LigaDAO(conn);

        carregarLigas();
    }

    private void carregarLigas() {
        ligasBox.getChildren().clear();

        try {
            List<Liga> ligas = ligaDAO.getAllLigas();

            if (ligas.isEmpty()) {
                Label vazio = new Label("Nenhuma liga cadastrada.");
                vazio.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                ligasBox.getChildren().add(vazio);
                return;
            }

            for (Liga liga : ligas) {
                ligasBox.getChildren().add(criarLinhaLiga(liga));
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar as ligas.");
        }
    }

    private HBox criarLinhaLiga(Liga liga) {
        HBox linha = new HBox(20);
        linha.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 10;");
        linha.setPrefHeight(50.0);

        Label nomeLabel = new Label(liga.getNome());
        nomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nomeLabel.setPrefWidth(200.0);

        Label descricaoLabel = new Label(liga.toString() != null ? liga.toString() : "");
        descricaoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        descricaoLabel.setPrefWidth(200.0);

        Button btnDeletar = new Button("Deletar");
        btnDeletar.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        btnDeletar.setOnAction(e -> deletarLiga(liga));

        linha.getChildren().addAll(nomeLabel, descricaoLabel, btnDeletar);
        return linha;
    }

    private void deletarLiga(Liga liga) {
        try {
            // Buscar todos os usuários da liga
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn, ligaDAO);
            List<Pessoa> pessoasDaLiga = usuarioDAO.getAllUsuariosByLigaId(liga.getId());

            // Encontrar o admin da liga
            Usuario adminLiga = null;
            for (Pessoa p : pessoasDaLiga) {
                if (p instanceof Usuario u && u.getTipo() == UserType.ADMLIGA) {
                    adminLiga = u;
                    break;
                }
            }

            // Transformar adminLiga em user (caso exista)
            if (adminLiga != null) {
                boolean sucesso = usuarioDAO.transformarAdminLigaEmUsuario(adminLiga.getId());
                if (!sucesso) {
                    mostrarAlerta("Erro", "Não foi possível alterar o tipo do administrador da liga.");
                    return;
                }
            }

            // Deletar a liga
            ligaDAO.deleteLiga(liga.getId());

            mostrarAlerta("Sucesso", "Liga deletada com sucesso!");
            carregarLigas();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Ocorreu um erro ao tentar deletar a liga.");
        }
    }

    @FXML
    private void buscarLigas() {
        String termo = tfBuscaLiga.getText().trim().toLowerCase();

        ligasBox.getChildren().clear();

        try {
            List<Liga> ligas = ligaDAO.getAllLigas();
            ligas.removeIf(l -> !l.getNome().toLowerCase().contains(termo));

            if (ligas.isEmpty()) {
                Label vazio = new Label("Nenhuma liga encontrada para o filtro.");
                vazio.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                ligasBox.getChildren().add(vazio);
                return;
            }

            for (Liga liga : ligas) {
                ligasBox.getChildren().add(criarLinhaLiga(liga));
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar as ligas.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}
