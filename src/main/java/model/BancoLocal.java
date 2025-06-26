/*//Talvez seja importante para o fluxo do programa, mas pode ser apagado no futuro
package model;

import dao.ClubeDAO;
import dao.JogadorDAO;
import dao.LigaDAO;

import java.sql.*;
import java.util.List;

public class BancoLocal {

    // Listas globais carregadas no início do programa (clubes, que contém jogadores, e ligas, que contém usuários importantes)
    private static List<Clube> clubes;
    private static List<Liga> ligas;

    // DAOs
    private static ClubeDAO clubeDAO;
    private static JogadorDAO jogadorDAO;
    private static LigaDAO ligaDAO;

    // Inicializa os DAOs e carrega os dados
    public static void carregarDados(Connection conn) {
        clubeDAO = new ClubeDAO(conn);
        jogadorDAO = new JogadorDAO(conn);
        ligaDAO = new LigaDAO(conn);

        clubes = clubeDAO.getAllClubes();
        for(Clube clube : clubes) {
            for(Jogador j : jogadorDAO.getJogadoresByClub(clube)) {
                clube.getJogadores().add(j);
            }
        }
        Simulacao.gerarPartidasAleatorias(clubes);
        ligas = ligaDAO.getAllLigas();
    }

    // Getters
    public static List<Clube> getClubes() {
        return clubes;
    }

    public static List<Liga> getLigas() {
        return ligas;
    }

    // Métodos auxiliares de busca por ID (opcional)
    public static Clube getClubePorId(int id) {
        return clubes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static Liga getLigaPorId(int id) {
        return ligas.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
    }
}*/
