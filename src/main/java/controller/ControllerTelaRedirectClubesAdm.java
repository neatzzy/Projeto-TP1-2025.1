package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ControllerTelaRedirectClubesAdm {
    @FXML
    private Button menuMontagem;

    @FXML
    public void voltar(){
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
    }

    @FXML
    public void irParaCriarClubes(){

    }

    @FXML
    public void irParaDeletarClubes(){

    }
}
