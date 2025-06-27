package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Admin;
import model.Usuario;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import model.*;

public class ControllerTelaMenuUsuario {

    private Usuario usuario;
    private Connection conn;

    @FXML private Label labelTitulo;

    public void setUsuarioLogado(Usuario usuario) { this.usuario = usuario; }
    public void setConnection(Connection conn) { this.conn = conn; }

    @FXML
    public void voltar(){
        try {

            NavigationManager.clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/InicialScreens/TelaInicio.fxml"));
            Parent root = loader.load();

            ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tela de Início");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
