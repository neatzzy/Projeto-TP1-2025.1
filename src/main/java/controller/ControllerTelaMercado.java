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
    private TimeUsuario timeUsuario;
    private Usuario usuario;

    public void setConnection(Connection conn, TimeUsuario timeUsuario, Usuario usuario) {
        this.conn = conn;
        this.timeUsuario = timeUsuario;
        this.usuario = usuario;
        this.timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));
    }

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void initialize() {

        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getClube().getNome()));
        colPosicao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStringPosicao()));
        colJogador.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colPreco.setCellValueFactory(cell -> new SimpleStringProperty(String.format("C$%.2f", cell.getValue().getPreco())));

        comboBoxFiltro.getItems().addAll("Todos", "Goleiro", "Zagueiro", "Meia", "Atacante");
        comboBoxFiltro.setValue("Todos");

        colComprar.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Comprar");
            {
                btn.setOnAction(e -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());

                    try {
                        if (timeUsuario.getJogadores().contains(jogador)) {
                            mostrarAlerta("Erro", "Este jogador já está no seu time.");
                            return;
                        }

                        // Contar jogadores por posição
                        long countZagueiros = timeUsuario.getJogadores().stream()
                                .filter(j -> j.getPosicao() == Posicao.ZAGUEIRO)
                                .count();

                        long countMeias = timeUsuario.getJogadores().stream()
                                .filter(j -> j.getPosicao() == Posicao.MEIA)
                                .count();

                        long countAtacantes = timeUsuario.getJogadores().stream()
                                .filter(j -> j.getPosicao() == Posicao.ATACANTE)
                                .count();

                        long countGoleiro = timeUsuario.getJogadores().stream()
                                .filter(j -> j.getPosicao() == Posicao.GOLEIRO)
                                .count();

                        // Verificar limites
                        switch (jogador.getPosicao()) {
                            case ZAGUEIRO:
                                if (countZagueiros >= 4) {
                                    mostrarAlerta("Erro", "Você só pode ter no máximo 4 zagueiros no time.");
                                    return;
                                }
                                break;
                            case MEIA:
                                if (countMeias >= 4) {
                                    mostrarAlerta("Erro", "Você só pode ter no máximo 4 meio-campistas no time.");
                                    return;
                                }
                                break;
                            case ATACANTE:
                                if (countAtacantes >= 3) {
                                    mostrarAlerta("Erro", "Você só pode ter no máximo 3 atacantes no time.");
                                    return;
                                }
                                break;
                            case GOLEIRO:
                                if (countGoleiro >= 1) {
                                    mostrarAlerta("Erro", "Você só pode ter no máximo 1 goleiro no time.");
                                    return;
                                }
                                break;
                            default:
                                break;
                        }


                        double saldoAtual = 100.0 - timeUsuario.getPreco();
                        if (jogador.getPreco() > saldoAtual) {
                            mostrarAlerta("Erro", "Saldo insuficiente para comprar este jogador.");
                            return;
                        }


                        boolean inseriu = timeDAO.insertJogadorTime(jogador.getId(), usuario.getId());
                        //NAO ERA PRA PRECISAR!
                        if (!inseriu) {
                            mostrarAlerta("Erro", "Este jogador já está no seu time.");
                            return;
                        }

                        timeUsuario.addJogador(jogador);
                        timeUsuario.setPreco(timeUsuario.getPreco() + jogador.getPreco());
                        mostrarAlerta("Sucesso", "Jogador comprado com sucesso!");

                        NavigationManager.pop();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrEscalarScreens/TelaCampinho.fxml"));
                        Parent root = loader.load();

                        ControllerTelaCampinho controller = (ControllerTelaCampinho) loader.getController();
                        controller.setConnection(conn, usuario);

                        Stage stage = (Stage) menuMontagem.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Campinho");
                        stage.show();


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarAlerta("Erro", "Erro ao comprar jogador.");
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Carregar todos jogadores da simulação
        carregarJogadores();

        // Configurar evento filtro e pesquisa
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
