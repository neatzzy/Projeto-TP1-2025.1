package controller;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTelaViewLiga {

    @FXML
    private Button menuMontagem;

    @FXML
    private Label labelTitulo;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private Button sairButton;

    private Connection conn;
    private Liga liga;
    private Usuario usuario;
    private UsuarioDAO usuarioDAO;
    private TimeDAO timeDAO;

    private Map<Integer, Double> pontuacoesPorUsuario = new HashMap<>();

    public void setConnection(Connection conn, Liga liga, Usuario usuario) {
        this.conn = conn;
        this.liga = liga;
        this.usuario = usuario;
        this.usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));
        carregarDados();
    }

    private void carregarDados() {
        try {
            labelTitulo.setText(liga.getNome());

            List<Pessoa> usuariosDaLiga = usuarioDAO.getAllUsuariosByLigaId(liga.getId());
            List<Usuario> usuariosConvertidos = new ArrayList<>(
                    usuariosDaLiga.stream()
                            .map(u -> (Usuario) u)
                            .toList()
            );

            int idAdminLiga = usuariosDaLiga.stream()
                    .filter(p -> p instanceof Usuario u && u.getTipo() == UserType.ADMLIGA)
                    .map(p -> (Usuario) p)
                    .findFirst()
                    .map(Pessoa::getId)
                    .orElse(-1);

            ObservableList<Usuario> obs = FXCollections.observableArrayList(usuariosConvertidos);
            listViewUsuarios.setItems(obs);

            if (Simulacao.getOcorreu()) {
                pontuacoesPorUsuario.clear();

                for (Usuario u : usuariosConvertidos) {
                    try {
                        double pontuacao = timeDAO.getPontuacaoTime(u.getId());
                        pontuacoesPorUsuario.put(u.getId(), pontuacao);
                    } catch (SQLException e) {
                        pontuacoesPorUsuario.put(u.getId(), 0.0);
                    }
                }

                usuariosConvertidos.sort((u1, u2) -> {
                    double p1 = pontuacoesPorUsuario.getOrDefault(u1.getId(), 0.0);
                    double p2 = pontuacoesPorUsuario.getOrDefault(u2.getId(), 0.0);
                    return Double.compare(p2, p1);
                });

                obs.setAll(usuariosConvertidos);
            }

            listViewUsuarios.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Usuario usuario, boolean empty) {
                    super.updateItem(usuario, empty);
                    if (empty || usuario == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        String nomeUsuario = usuario.getNome();

                        if (Simulacao.getOcorreu()) {
                            int posicao = getIndex() + 1;
                            double pontuacao = pontuacoesPorUsuario.getOrDefault(usuario.getId(), 0.0);

                            Text posTexto = new Text(String.format("%dº ", posicao));
                            Text nomeTexto = new Text(nomeUsuario);
                            Text pontTexto = new Text(String.format(" - Pontuação: %.2f", pontuacao));

                            if (usuario.getId() == idAdminLiga) {
                                nomeTexto.setStyle("-fx-fill: red; -fx-font-weight: bold;");
                            }

                            if (getIndex() == 0) {
                                setStyle("-fx-background-color: #C8E6C9;");
                            } else {
                                setStyle("");
                            }

                            TextFlow flow = new TextFlow(posTexto, nomeTexto, pontTexto);
                            setGraphic(flow);
                            setText(null);
                        } else {
                            Text nomeTexto = new Text(nomeUsuario);
                            if (usuario.getId() == idAdminLiga) {
                                nomeTexto.setStyle("-fx-fill: red; -fx-font-weight: bold;");
                            }
                            TextFlow flow = new TextFlow(nomeTexto);
                            setGraphic(flow);
                            setText(null);
                            setStyle("");
                        }
                    }
                }
            });

        } catch (SQLException e) {
            mostrarAlerta("Erro", "Falha ao carregar usuários da liga.");
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
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    private void sairLiga() {
        if(Simulacao.getOcorreu()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Ação bloqueada");
            alerta.setHeaderText(null);
            alerta.setContentText("Você não pode sair da liga pois a simulação já foi realizada.");
            alerta.showAndWait();
            return;
        }
        try {

            boolean sucesso = usuarioDAO.removerUsuarioDaLiga(usuario, usuario.getLiga());

            if (sucesso) {

                // se não está em uma liga, remove seu time
                timeDAO.deleteTimeById(usuario.getId());

                mostrarAlerta("Sucesso", "Você saiu da liga.");

                // Redireciona para o menu do usuário
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrMenuScreens/TelaMenuUsuario.fxml"));
                Parent root = loader.load();

                controller.ControllerTelaMenuUsuario controller = loader.getController();
                controller.setConnection(conn);
                controller.setUsuarioLogado(usuario);

                Stage stage = (Stage) sairButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu do Usuário");

                NavigationManager.clear();
            } else {
                mostrarAlerta("Erro", "Erro ao sair da liga.");
            }

        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao abrir o menu.");
            e.printStackTrace();
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Erro no banco de dados ao sair da liga.");
            e.printStackTrace();
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

