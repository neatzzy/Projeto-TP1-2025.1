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
        NavigationManager.popAndApply((Stage) menuMontagem.getScene().getWindow());
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
