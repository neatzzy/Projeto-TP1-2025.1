package controller.AdmLigaController;

import controller.NavigationManager;
import dao.LigaDAO;
import dao.UsuarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Liga;
import model.Pessoa;
import model.UserType;
import model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTelaViewLigas {

    @FXML private Button menuMontagem;
    @FXML private TextField tfBuscaLiga;
    @FXML private Button btnBuscar;
    @FXML private TableView<Liga> tableLigas;
    @FXML private TableColumn<Liga, String> colNome;
    @FXML private TableColumn<Liga, String> colId;
    @FXML private TableColumn<Liga, Void> colAcoes;

    private Connection conn;
    private LigaDAO ligaDAO;

    private ObservableList<Liga> ligasObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        btnBuscar.setOnAction(e -> buscarLigas());

        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnDeletar = new Button("Deletar");

            {
                btnDeletar.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
                btnDeletar.setOnAction(e -> {
                    Liga liga = getTableView().getItems().get(getIndex());
                    deletarLiga(liga);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(btnDeletar));
                }
            }
        });
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
        this.ligaDAO = new LigaDAO(conn);
        carregarLigas();
    }

    private void carregarLigas() {
        try {
            List<Liga> ligas = ligaDAO.getAllLigas();
            ligasObservable.setAll(ligas);
            tableLigas.setItems(ligasObservable);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Não foi possível carregar as ligas.");
        }
    }

    @FXML
    private void buscarLigas() {
        String termo = tfBuscaLiga.getText().trim().toLowerCase();

        if (termo.isEmpty()) {
            tableLigas.setItems(ligasObservable);
            return;
        }

        List<Liga> filtradas = ligasObservable.stream()
                .filter(l -> l.getNome().toLowerCase().contains(termo))
                .collect(Collectors.toList());

        tableLigas.setItems(FXCollections.observableArrayList(filtradas));
    }

    private void deletarLiga(Liga liga) {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn, ligaDAO);
            List<Pessoa> pessoasDaLiga = usuarioDAO.getAllUsuariosByLigaId(liga.getId());

            Usuario adminLiga = null;
            for (Pessoa p : pessoasDaLiga) {
                if (p instanceof Usuario u && u.getTipo() == UserType.ADMLIGA) {
                    adminLiga = u;
                    break;
                }
            }

            if (adminLiga != null) {
                boolean sucesso = usuarioDAO.transformarAdminLigaEmUsuario(adminLiga.getId());
                if (!sucesso) {
                    mostrarAlerta("Erro", "Não foi possível alterar o tipo do administrador da liga.");
                    return;
                }
            }

            ligaDAO.deleteLiga(liga.getId());

            mostrarAlerta("Sucesso", "Liga deletada com sucesso!");
            carregarLigas();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Ocorreu um erro ao tentar deletar a liga.");
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
    public void voltar() {
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }
}
