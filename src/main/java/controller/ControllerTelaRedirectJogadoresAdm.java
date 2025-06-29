package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ControllerTelaRedirectJogadoresAdm {
    @FXML
    private Button menuMontagem;

    @FXML
    public void voltar(){
        Scene previous = NavigationManager.pop();
        if (previous != null) {
            Stage stage = (Stage) menuMontagem.getScene().getWindow();
            stage.setScene(previous);
        }
    }

    @FXML
    public void irParaVerJogadores(){

    }

    @FXML
    public void irParaCriarJogadores(){

    }

    @FXML
    public void irParaDeletarJogadores(){

    }
}
