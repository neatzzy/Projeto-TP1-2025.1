package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.JogadorDAO;
import dao.LigaDAO;
import dao.UsuarioDAO;

public class Admin extends Pessoa {

    private static UsuarioDAO usuarioDAO;

    public Admin(int id, String nome, String senha, Connection conn) {
        super(id, nome, senha);
        usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    public boolean simular() throws SQLException {
        return Simulacao.simular();
    }

    public void resetarSimulacao() throws SQLException {
        Simulacao.resetar();
    }

    public void deleteUsuario(Usuario usuario){
        int id = usuario.getId();
        usuarioDAO.deleteUsuarioById(id);
    }

    public void deleteUsuario(int id){
        usuarioDAO.deleteUsuarioById(id);
    }

    /* FALTA IMPLEMENTAR:

    deleteLigas getRelatorioLigas
    partidas(modificar, ver, refazer as partidas, remover, adicionar etc)
    addjogador/remove
    addClube/remove (incluso em partidas)
    */
}
