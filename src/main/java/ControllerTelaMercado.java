import io.github.cdimascio.dotenv.Dotenv;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.Connection;
import java.util.List;

public class ControllerTelaMercado {

    @FXML
    private ComboBox<String> comboBoxFiltro;

    @FXML
    private TableView<Jogador> tableView;

    @FXML
    private TableColumn<Jogador, String> colTime;

    @FXML
    private TableColumn<Jogador, String> colPosicao;

    @FXML
    private TableColumn<Jogador, String> colJogador;

    @FXML
    private TableColumn<Jogador, String> colPreco;

    @FXML
    private TableColumn<Jogador, String> colComprar;

    private ObservableList<Jogador> listaJogadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboBoxFiltro.setItems(FXCollections.observableArrayList("Todos", "ATACANTE", "MEIA", "ZAGUEIRO", "GOLEIRO"));
        comboBoxFiltro.setValue("Todos");

        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db(db_name, user, pass);

        // Busca clubes e jogadores
        List<Clube> clubes = db.getAllCLubes(conn);
        db.getAllJogadores(conn, clubes);

        // Preenche a lista observável com todos os jogadores dos clubes
        for (Clube clube : clubes) {
            listaJogadores.addAll(clube.getJogadores());
        }

        tableView.setItems(listaJogadores);

        // Configura as colunas
        colTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClube().getNome()));
        colPosicao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStringPosicao()));
        colJogador.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colPreco.setCellValueFactory(data -> new SimpleStringProperty("C$" + String.format("%.2f", data.getValue().getPreco())));
        colComprar.setCellFactory(col -> new TableCell<Jogador, String>() {
            private final Button btn = new Button("Comprar");

            {
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;");
                btn.setOnAction(event -> {
                    Jogador jogador = getTableView().getItems().get(getIndex());
                    System.out.println("Comprou: " + jogador.getNome());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setStyle("-fx-alignment: CENTER; -fx-padding: 0;"); // Centraliza e remove padding
                }
            }
        });

        comboBoxFiltro.setOnAction(e -> filtrar());

        listaJogadores.sort((j1, j2) -> {
            int cmp = j1.getClube().getNome().compareToIgnoreCase(j2.getClube().getNome());
            if (cmp != 0) return cmp;
            cmp = compararPosicao(j1.getStringPosicao(), j2.getStringPosicao());
            if (cmp != 0) return cmp;
            cmp = Double.compare(j2.getPreco(), j1.getPreco()); // Maior preço primeiro
            if (cmp != 0) return cmp;
            return j1.getNome().compareToIgnoreCase(j2.getNome());
        });
    }

    private void filtrar() {
        String filtro = comboBoxFiltro.getValue();
        if (filtro.equals("Todos")) {
            tableView.setItems(listaJogadores);
        } else {
            ObservableList<Jogador> filtrados = listaJogadores.filtered(j -> j.getPosicao().equals(filtro));
            tableView.setItems(filtrados);
        }
    }

    private int compararPosicao(String pos1, String pos2) {
        List<String> ordem = List.of("ATACANTE", "MEIA", "ZAGUEIRO", "GOLEIRO");
        int i1 = ordem.indexOf(pos1);
        int i2 = ordem.indexOf(pos2);
        return Integer.compare(i1, i2);
    }
}