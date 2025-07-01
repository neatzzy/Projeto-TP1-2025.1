package controller;

import dao.*;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.HashSet;
import java.util.Set;

public class ControllerTelaMercado {

    @FXML private Button menuMontagem;
    @FXML private ComboBox<String> comboBoxFiltro;
    @FXML private TextField campoPesquisa;
    @FXML private Button botaoPesquisar;
    @FXML private TableView<Jogador> tableView;
    @FXML private TableColumn<Jogador, String> colTime;
    @FXML private TableColumn<Jogador, String> colPosicao;
    @FXML private TableColumn<Jogador, String> colJogador;
    @FXML private TableColumn<Jogador, String> colPreco;
    @FXML private TableColumn<Jogador, Void> colComprar;


    private ObservableList<Jogador> listaJogadores = FXCollections.observableArrayList();

    private Connection conn;
    private TimeDAO timeDAO;
    private Usuario usuario;
    private TimeUsuario timeusuario;

    public void setConnection(Connection conn, Usuario usuario, TimeDAO timedao) {
        this.conn = conn;
        this.usuario = usuario;
        this.timeusuario = usuario.getTimeUsuario();
        this.timeDAO = timedao;

        inicializarInterface();
    }

    @FXML
    public void voltar(){

        try {
            NavigationManager.pop();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrEscalarScreens/TelaCampinho.fxml"));

            Parent root = loader.load();
            ControllerTelaCampinho controller = (ControllerTelaCampinho) loader.getController();
            controller.setConnection(conn, usuario);
            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Campinho");

            stage.show();
        }catch (IOException e){
               throw new RuntimeException(e);
        }
    }

    @FXML
    private void inicializarInterface() {

        timeusuario.imprimirTime();

        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClube().getNome()));
        colPosicao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStringPosicao()));
        colJogador.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(String.format("C$%.2f", cell.getValue().getPreco())));

        comboBoxFiltro.getItems().addAll("Todos", "Goleiro", "Zagueiro", "Meia", "Atacante");
        comboBoxFiltro.setValue("Todos");

        colComprar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button();

            {
                btn.setOnAction(e -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());

                    try {
                        if (timeusuario.getJogadores().contains(jogador)) {

                            // Remover jogador
                            timeusuario.removeJogador(jogador);

                            usuario.setTimeUsuario(timeusuario);

                            mostrarAlerta("Remoção", "Jogador removido com sucesso!");
                        } else {
                            // Adicionar jogador
                            boolean inseriu = timeusuario.addJogador(jogador);
                            if (!inseriu) {
                                mostrarAlerta("Erro", "Esse jogador não pode ser adicionado!");
                                return;
                            }

                            usuario.setTimeUsuario(timeusuario);

                            mostrarAlerta("Sucesso", "Jogador comprado com sucesso!");
                        }

                        tableView.refresh();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarAlerta("Erro", "Erro ao processar jogador.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    if (timeusuario.getJogadores().contains(jogador)) {
                        btn.setText("Remover");
                        btn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-background-radius: 10;");
                    } else {
                        btn.setText("Comprar");
                        btn.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-background-radius: 10;");
                    }
                    setGraphic(btn);
                }
            }
        });

        carregarJogadores();
        comboBoxFiltro.setOnAction(e -> filtrar());
        botaoPesquisar.setOnAction(e -> filtrar());
    }

    private void carregarJogadores() {
        listaJogadores.clear();

        // Usar Set para evitar duplicatas
        Set<Jogador> jogadoresUnicos = new HashSet<>();

        for (Partida partida : Simulacao.getPartidas()) {
            jogadoresUnicos.addAll(partida.getAllJogadores());
        }

        listaJogadores.addAll(jogadoresUnicos);

        filtrar();
    }

    @FXML
    private void pesquisarJogador() {
        filtrar();
    }


    private void filtrar() {
        String filtro = comboBoxFiltro.getValue();
        String pesquisa = campoPesquisa.getText() != null ? campoPesquisa.getText().trim().toLowerCase() : "";

        ObservableList<Jogador> filtrados = listaJogadores.filtered(j -> {
            boolean posicaoOk = filtro.equals("Todos") || j.getStringPosicao().equalsIgnoreCase(filtro);
            boolean nomeOk = pesquisa.isEmpty() || j.getNome().toLowerCase().contains(pesquisa);
            return posicaoOk && nomeOk;
        });

        tableView.setItems(filtrados);
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }

}
