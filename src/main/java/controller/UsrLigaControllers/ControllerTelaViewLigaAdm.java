package controller.UsrLigaControllers;

import controller.NavigationManager;
import controller.SceneInfo;
import controller.UsrMenuController.ControllerTelaMenuUsuario;
import dao.JogadorDAO;
import dao.LigaDAO;
import dao.TimeDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTelaViewLigaAdm {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private Button deletarButton;

    @FXML
    private Button adicionarButton;

    private Connection conn;
    private Liga liga;
    private Usuario usuario; // admin logado
    private UsuarioDAO usuarioDAO;
    private LigaDAO ligaDAO;
    private TimeDAO timeDAO;

    public void setConnection(Connection conn, Liga liga, Usuario usuario) {
        this.conn = conn;
        this.liga = liga;
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
        this.ligaDAO = new LigaDAO(conn);
        this.timeDAO = new TimeDAO(conn, usuarioDAO, new JogadorDAO(conn, new dao.ClubeDAO(conn)));

        carregarDados();
    }

    @FXML
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    private Map<Integer, Double> pontuacoesPorUsuario = new HashMap<>();

    private void carregarDados() {
        try {
            labelTitulo.setText(liga.getNome());

            List<Pessoa> usuariosDaLiga = usuarioDAO.getAllUsuariosByLigaId(liga.getId());
            List<Usuario> usuariosDaLiga2 = new ArrayList<>(usuariosDaLiga.stream()
                    .map(p -> (Usuario) p)
                    .toList());

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosDaLiga2);

            // se simulação ocorreu, carregar pontuações do banco
            if (Simulacao.getOcorreu()) {
                pontuacoesPorUsuario.clear();

                for (Usuario u : usuariosDaLiga2) {
                    try {
                        double pontuacao = timeDAO.getPontuacaoTime(u.getId());
                        pontuacoesPorUsuario.put(u.getId(), pontuacao);
                    } catch (SQLException e) {
                        pontuacoesPorUsuario.put(u.getId(), 0.0);
                    }
                }

                usuariosDaLiga2.sort((u1, u2) -> {
                    double p1 = pontuacoesPorUsuario.getOrDefault(u1.getId(), 0.0);
                    double p2 = pontuacoesPorUsuario.getOrDefault(u2.getId(), 0.0);
                    return Double.compare(p2, p1);
                });

                obs.setAll(usuariosDaLiga2);
            }

            listViewUsuarios.setItems(obs);

            listViewUsuarios.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Usuario usuario, boolean empty) {
                    super.updateItem(usuario, empty);
                    if (empty || usuario == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    String texto = usuario.getNome();

                    if (Simulacao.getOcorreu()) {
                        int posicao = getIndex() + 1;
                        double pont = pontuacoesPorUsuario.getOrDefault(usuario.getId(), 0.0);
                        setText(String.format("%dº %s - Pontuação: %.2f", posicao, texto, pont));

                        if (getIndex() == 0)
                            setStyle("-fx-background-color: #C8E6C9;");
                        else
                            setStyle("");
                    } else {
                        setText(texto);
                        setStyle("");
                    }
                }
            });

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro ao carregar usuários da liga.");
            e.printStackTrace();
        }
        listViewUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (!Simulacao.getOcorreu()) {
                    mostrarAlerta("Simulação não realizada", "A simulação ainda não foi realizada. Você não pode visualizar os times.");
                    return;
                }

                Usuario selecionado = listViewUsuarios.getSelectionModel().getSelectedItem();
                if (selecionado != null) {
                    abrirTelaVisualizarCampinho(selecionado);
                }
            }
        });
    }

    @FXML
    private void initialize() {

        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        deletarButton.setOnAction(e -> deletarLiga()); // Ainda é opcional
        adicionarButton.setOnAction(e -> abrirAdicionarUsuarios());
        listViewUsuarios.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                removerUsuarioSelecionado();
            }
        });
    }

    @FXML
    private void removerUsuarioSelecionado() {
        Usuario selecionado = listViewUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um usuário.");
            return;
        }

        if (selecionado.getId() == usuario.getId()) {
            mostrarAlerta("Erro", "Você não pode remover a si mesmo.");
            return;
        }

        try {
            boolean sucesso = usuarioDAO.removerUsuarioDaLiga(selecionado, this.liga);

            if (sucesso) {

                // se não está em uma liga, remove seu time
                timeDAO.deleteTimeById(selecionado.getId());

                mostrarAlerta("Sucesso", "Usuário removido da liga.");
                carregarDados();
            } else {
                mostrarAlerta("Erro", "Erro ao remover o usuário.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao tentar remover o usuário.");
        }
    }

    @FXML
    private void deletarLiga() {
        if(Simulacao.getOcorreu()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Ação bloqueada");
            alerta.setHeaderText(null);
            alerta.setContentText("Você não pode deletar a liga pois a simulação já foi realizada.");
            alerta.showAndWait();
            return;
        }
        try {

            ligaDAO.deleteLiga(this.liga.getId());
            usuarioDAO.transformarAdminLigaEmUsuario(this.usuario.getId());
            this.usuario.setTipo(UserType.USUARIO);
            this.usuario.sairLiga(liga);

            mostrarAlerta("Sucesso", "Liga removida.");

            NavigationManager.clear();

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                Parent root = loader.load();

                ControllerTelaMenuUsuario controller = loader.getController();
                controller.setConnection(conn);
                controller.setUsuarioLogado(this.usuario);

                Stage stage = (Stage) menuMontagem.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu do Usuário");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao remover liga.");
        }
    }

    @FXML
    private void abrirAdicionarUsuarios() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaAddUsuarioLiga.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Liga");
            NavigationManager.push(sceneInfo);

            ControllerTelaAddUsuarioLiga controller = loader.getController();
            controller.setConnection(conn, this.usuario, this.liga);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Adição Usuário Liga");
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao redirecionar para o menu.");
        }
    }

    private void abrirTelaVisualizarCampinho(Usuario outroUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaVisualizarCampinho.fxml"));
            Parent root = loader.load();

            ControllerTelaVisualizarCampinho controller = loader.getController();
            controller.setConnection(conn, outroUsuario);

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Visualizar Time de " + outroUsuario.getNome());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir visualização do time.");
            e.printStackTrace();
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


