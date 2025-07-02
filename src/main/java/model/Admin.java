package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

import dao.LigaDAO;
import dao.UsuarioDAO;

public class Admin extends Pessoa {

    // DAO para operações com usuários
    private static UsuarioDAO usuarioDAO;

    public Admin(int id, String nome, String senha, Connection conn) {
        super(id, nome, senha);
        // Inicializa o DAO de usuários com a conexão recebida
        usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    // Executa a simulação completa em 4 etapas
    public boolean simular() throws SQLException {

        // Consumidor para mostrar mensagens no console
        Consumer<String> atualizarMensagem = mensagem -> System.out.println(mensagem);

        // Loop por todas as etapas da simulação
        for (int etapa = 0; etapa <= 3; etapa++) {
            boolean sucesso = Simulacao.simular(etapa, atualizarMensagem);
            if (!sucesso) {
                return false; // falha em alguma etapa
            }
        }
        return true; // todas as etapas concluídas com sucesso
    }

    // Reseta os dados da simulação para o estado inicial
    public void resetarSimulacao() throws SQLException {
        Simulacao.resetar();
    }

    // Indica que este usuário é um administrador
    @Override
    public boolean isAdmin() { return true; }
}
