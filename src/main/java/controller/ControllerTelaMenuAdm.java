package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ControllerTelaMenuAdm {
    private Admin adm;
    private Connection conn;

    @FXML
    private Label labelTitulo;

    @FXML
    private Button simulacaoButton;

    @FXML
    private Button ligasButton;

    @FXML
    private Button usuariosButton;

    @FXML
    private Button clubesButton;

    @FXML
    private Button jogadoresButton;

    @FXML
    private Button partidasButton;

    @FXML
    private Button sairButton;


    public void setUsuarioLogado(Admin adm) { this.adm = adm; }
    public void setConnection(Connection conn) { this.conn = conn; }

    public void abrirSimulacao(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmSimulacaoScreens/TelaSimulacao.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Simulação");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirLigas(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmLigaScreens/TelaViewLigas.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ligas");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirUsuarios(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmUserScreens/TelaViewUsuarios.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Usuários");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirClubes(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmClubesScreens/TelaRedirectClubesAdm.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clubes");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirJogadores(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmJogadorScreens/TelaRedirectJogadoresAdm.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Jogadores");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void abrirPartidas(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AdmPartidaScreens/TelaRedirectPartidasAdm.fxml"));
            Parent root = loader.load();

            NavigationManager.push(labelTitulo.getScene());

            //ControllerTelaInicio controller = (ControllerTelaInicio) loader.getController();
            //controller.setConnection(conn);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Partidas");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

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


