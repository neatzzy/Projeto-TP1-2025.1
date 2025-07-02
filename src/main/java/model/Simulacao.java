package model;

import dao.*;
import database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;
// a ideia eh puxar os dados do db e colocar os clubes em uma lista, para gerar as partidas ou adiciona-las manualmente e depois simula-las
// juntamente com a simulacao da liga

public class Simulacao {
    private static boolean ocorreu = false;
    private static Set<Partida> partidas = new HashSet<>();
    private static LigaDAO ligaDAO;
    private static UsuarioDAO usuarioDAO;
    private static TimeDAO timeDAO;

    public void InicializarConexoes(Connection conn){
        ligaDAO = new LigaDAO(conn);
        usuarioDAO = new UsuarioDAO(conn, new LigaDAO(conn));
        timeDAO = new TimeDAO(conn, new UsuarioDAO(conn, new LigaDAO(conn)), new JogadorDAO(conn, new ClubeDAO(conn)));
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

        System.out.println("Simular INICIOU");

        List<Liga> ligas = ligaDAO.getAllLigas();

        Map<Integer, Jogador> jogadoresSimulados = new HashMap<>();


        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()){
                if(!usuario.getTimeUsuario().isValido()) return false;
            }
        }

        for (Partida partida : partidas){
            partida.simular(); // já calcula a pontuação dos jogadores junto

            // Adiciona jogadores do time da casa
            for (Jogador j : partida.getClubeCasa().getJogadores()) {
                jogadoresSimulados.put(j.getId(), j);
            }

            // Adiciona jogadores do time visitante
            for (Jogador j : partida.getClubeFora().getJogadores()) {
                jogadoresSimulados.put(j.getId(), j);
            }
        }

        // usuário é nulo ent ne entra
        /*
        for (Liga liga : ligas) {
            System.out.println("LIGA: " + liga);
            for (Usuario usuario : liga.getUsuarios()){
                System.out.println("USUARIO: " + usuario);
                usuario.getTimeUsuario().calcularPontuacao();
                System.out.println("CALCULANDO PONTUACAO: " + usuario.getTimeUsuario().getPontuacao());
                timeDAO.inserirPontuacaoTime(usuario.getId(), usuario.getTimeUsuario().getPontuacao());
            }
        }
         */

        // Agora, para cada usuário, substitua os jogadores do time pelos simulados e calcula pontuação
        for (Liga liga : ligas) {
            System.out.println("LIGA: " + liga);

            ocorreu = false;

            // Pega todos os times da liga direto pelo DAO
            Map<Integer, TimeUsuario> timesComIds = timeDAO.getAllTimesComIdsPorLigaId(liga.getId());

            for (Map.Entry<Integer, TimeUsuario> entry : timesComIds.entrySet()) {

                ocorreu = false;

                int idTime = entry.getKey();
                TimeUsuario time = entry.getValue();

                System.out.println("TIME ID: " + idTime);

                time.imprimirTime();

                // Substituir jogadores pelos simulados
                List<Jogador> jogadoresOriginais = new ArrayList<>(time.getJogadores());

                for (int i = 0; i < jogadoresOriginais.size(); i++) {
                    Jogador original = jogadoresOriginais.get(i);
                    Jogador simulado = jogadoresSimulados.get(original.getId());
                    if (simulado != null) {
                        jogadoresOriginais.set(i, simulado);
                    }
                }

                time.setJogadores(new HashSet<>(jogadoresOriginais));

                System.out.println("ocorreu sim1: " + ocorreu);

                ocorreu = true;

                // Calcula pontuação atualizada
                time.calcularPontuacao();
                System.out.println("CALCULANDO PONTUACAO (Time ID " + idTime + "): " + time.getPontuacao());

                // Atualiza no banco usando o id do time
                timeDAO.inserirPontuacaoTime(idTime, time.getPontuacao());
            }
        }

        ocorreu = true;

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

        // usuário é nulo ent n entra

        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()){
                usuario.getTimeUsuario().calcularPontuacao(); // vai zerar a pontuacao pois os stats estao zerados
                timeDAO.inserirPontuacaoTime(usuario.getId(), 0.0); // n funciona9n entra no loop)
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

}