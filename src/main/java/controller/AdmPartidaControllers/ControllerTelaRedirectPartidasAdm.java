package controller.AdmPartidaControllers;

import controller.NavigationManager;
import controller.SceneInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.Simulacao;

import java.io.IOException;

public class ControllerTelaRedirectPartidasAdm {
    @FXML
    private Button menuMontagem;

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void irParaVerPartidas(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrPartidaScreens/TelaRedirectPartidas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Partidas");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void irParaEditarPartidas() {
        if (Simulacao.getOcorreu()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Operação não permitida");
            alert.setHeaderText(null);
            alert.setContentText("Não é possível editar partidas após a simulação ter ocorrido.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmPartidaScreens/TelaEditPartidas.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) menuMontagem.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(menuMontagem.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Editar Partidas");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
