package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Simulacao;
import model.UserType;
import model.Usuario;
import java.io.IOException;
import java.sql.Connection;

public class ControllerTelaMenuUsuario {

    private Usuario usuario;
    private Connection conn;

    @FXML
    private Label labelTitulo;

    @FXML
    private Button ligaButton;

    @FXML
    private Button escalarButton;

    @FXML
    private Button partidasButton;

    @FXML
    private Button jogadoresButton;

    @FXML
    private Button editarPerfilButton;

    @FXML
    private Button sairButton;

    public void setUsuarioLogado(Usuario usuario) { this.usuario = usuario; }
    public void setConnection(Connection conn) { this.conn = conn; }

    @FXML
    public void liga(){
        try {
            if (this.usuario.getTipo() == UserType.USUARIO  && this.usuario.getLiga() == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaRedirectNoLiga.fxml"));
                Parent root = loader.load();

                SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
                NavigationManager.push(sceneInfo);

                ControllerTelaRedirectNoLiga controller = (ControllerTelaRedirectNoLiga) loader.getController();
                controller.setConnection(conn, this.usuario);

                Stage stage = (Stage) labelTitulo.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu Liga");
                stage.show();
            }
            else if (this.usuario.getTipo() == UserType.USUARIO) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaViewLiga.fxml"));
                Parent root = loader.load();

                SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
                NavigationManager.push(sceneInfo);

                ControllerTelaViewLiga controller = (ControllerTelaViewLiga) loader.getController();
                controller.setConnection(conn, this.usuario.getLiga(), this.usuario);

                Stage stage = (Stage) labelTitulo.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu Liga");
                stage.show();
            }
            else{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrLigaScreens/TelaViewLigaAdm.fxml"));
                Parent root = loader.load();

                SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
                NavigationManager.push(sceneInfo);

                ControllerTelaViewLigaAdm controller = loader.getController();
                controller.setConnection(conn, this.usuario.getLiga(), this.usuario);

                Stage stage = (Stage) labelTitulo.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu Liga");
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void escalar() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrEscalarScreens/TelaCampinho.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
            NavigationManager.push(sceneInfo);

            ControllerTelaCampinho controller = loader.getController();
            controller.setConnection(conn, this.usuario);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Escalar Jogadores");
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void partidas() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrPartidaScreens/TelaRedirectPartidas.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
            NavigationManager.push(sceneInfo);

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
    private void jogadores() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrJogadorScreens/TelaViewJogadores.fxml"));
            Parent root = loader.load();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), "Menu do Usuário");
            NavigationManager.push(sceneInfo);

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

    @FXML
    private void editarPerfil() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/UsrUsuarioScreens/TelaEditPerfilUsuario.fxml"));
            Parent root = loader.load();

            ControllerTelaEditPerfilUsuario controller = (ControllerTelaEditPerfilUsuario) loader.getController();
            controller.setConnection(conn);
            controller.setUsuario(this.usuario);

            Stage stage = (Stage) labelTitulo.getScene().getWindow();

            SceneInfo sceneInfo = new SceneInfo(labelTitulo.getScene(), stage.getTitle());
            NavigationManager.push(sceneInfo);

            stage.setScene(new Scene(root));
            stage.setTitle("Editar Perfil");
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
