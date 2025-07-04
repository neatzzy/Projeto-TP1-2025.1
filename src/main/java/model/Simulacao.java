package model;

import dao.LigaDAO;
import dao.UsuarioDAO;
import database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.Set; // uso para declarar variavel da classe Set
import java.util.HashSet; // uso para instanciar o objeto
// a ideia eh puxar os dados do db e colocar os clubes em uma lista, para gerar as partidas ou adiciona-las manualmente e depois simula-las
// juntamente com a simulacao da liga

public class Simulacao {
    private static boolean ocorreu = false;
    private static Set<Partida> partidas = new HashSet<>();
    private static LigaDAO ligaDAO;
    private static UsuarioDAO usuarioDAO;

    public void InicializarConexoes(Connection conn){
        ligaDAO = new LigaDAO(conn);
        usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
    }

    // recebe uma lista de clubes e os sorteia em partidas, retorna falso se a lista nao for par
    public static boolean gerarPartidasAleatorias(List<Clube> clubes){
        if (clubes.size()%2 != 0) return false;

        // Embaralha a lista para garantir aleatoriedade
        Collections.shuffle(clubes, new Random());

        for (int i = 0; i < clubes.size(); i+=2) {
            Clube clubeCasa = clubes.get(i);
            Clube clubeFora = clubes.get(i+1);

            if(!Simulacao.addPartida(clubeCasa, clubeFora)) return false;
        }
        return true;
    }
    // adiciona partida entre 2 clubes na simulacao, retorna falso se os clubes ja estiverem em uma partida
    // TODO: dar override nos metodos equals/hash de Partida e talvez Clube, criar um construtor na classe Partida
    public static boolean addPartida(Clube clubeCasa, Clube clubeFora){
        if (clubeCasa.getPartida() || clubeFora.getPartida()) return false;
        Partida partida = new Partida(clubeCasa, clubeFora);
        partidas.add(partida);
        clubeCasa.setPartida(true);
        clubeFora.setPartida(true);
        return true;
    }
    // OBS: cuidar para que todos os clubes do campeonato estejam em alguma partida antes de simular
    public static boolean simular() throws SQLException {

        List<Liga> ligas = ligaDAO.getAllLigas();

        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()){
                if(!usuario.getTimeUsuario().isValido()) return false;
            }
        }

        for (Partida partida : partidas){
            partida.simular(); // já calcula a pontuação dos jogadores junto
        }

        ocorreu = true;

        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()){
                usuario.getTimeUsuario().calcularPontuacao();
            }
        }

        return true;
    }
    // so pode ser usado apos a simulacao
    public static void resetar() throws SQLException {

        List<Liga> ligas = ligaDAO.getAllLigas();

        Simulacao.resetPartidasStats(partidas);

        for (Partida partida : partidas){
            for (Jogador jogador : partida.getClubeCasa().getJogadores()){
                jogador.calcularPontuacao();
            }
            for (Jogador jogador : partida.getClubeFora().getJogadores()){
                jogador.calcularPontuacao();
            }
        }
        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()){
                usuario.getTimeUsuario().calcularPontuacao(); // vai zerar a pontuacao pois os stats estao zerados
            }
        }

        ocorreu = false;
    }
    // TODO: implementar o metodo resetStats() que itera pelos jogadores e reseta seus stats
    private static void resetPartidasStats(Set<Partida> partidas){
        for(Partida partida : partidas){
            partida.resetStats();
        }
    }

    // getters
    public static boolean getOcorreu() {
        return ocorreu;
    }

    public static Set<Partida> getPartidas() {
        return partidas;
    }

}