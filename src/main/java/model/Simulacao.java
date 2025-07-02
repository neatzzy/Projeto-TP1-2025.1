package model;

import dao.LigaDAO;
import dao.UsuarioDAO;
import database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
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

    public static boolean removePartida(Partida partida){
        return partidas.remove(partida);
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

    public static boolean trocarClubes(Clube clube1, Clube clube2) {
        if (clube1 == null || clube2 == null || clube1.equals(clube2)) return false;

        Partida partida1 = null;
        Partida partida2 = null;

        for (Partida p : partidas) {
            if (p.getClubeCasa().equals(clube1) || p.getClubeFora().equals(clube1)) {
                partida1 = p;
            }
            if (p.getClubeCasa().equals(clube2) || p.getClubeFora().equals(clube2)) {
                partida2 = p;
            }
        }

        if (partida1 == null || partida2 == null) return false;

        // Troca os clubes
        if (partida1.getClubeCasa().equals(clube1)) {
            partida1.setClubeCasa(clube2);
        } else {
            partida1.setClubeFora(clube2);
        }

        if (partida2.getClubeCasa().equals(clube2)) {
            partida2.setClubeCasa(clube1);
        } else {
            partida2.setClubeFora(clube1);
        }

        return true;
    }

    // TODO: implementar o metodo resetStats() que itera pelos jogadores e reseta seus stats
    private static void resetPartidasStats(Set<Partida> partidas){
        for(Partida partida : partidas){
            partida.resetStats();
        }
    }

    public static void clear(){
        for (Partida p : partidas) {
            p.getClubeCasa().setPartida(false);
            p.getClubeFora().setPartida(false);
        }
        partidas.clear();
    }

    // getters
    public static boolean getOcorreu() {
        return ocorreu;
    }

    public static Set<Partida> getPartidas() {
        return partidas;
    }

    public static List<Clube> getClubes() {
        Set<Clube> clubesSet = new HashSet<>();
        for (Partida partida : partidas) {
            clubesSet.add(partida.getClubeCasa());
            clubesSet.add(partida.getClubeFora());
        }
        return new ArrayList<>(clubesSet);
    }


}