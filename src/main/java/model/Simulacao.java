package model;

import dao.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class Simulacao{
    private static boolean ocorreu = false;
    private static Set<Partida> partidas = new HashSet<>();
    private static LigaDAO ligaDAO;
    private static TimeDAO timeDAO;

    // para ajudar na separação das etapas
    private static List<Liga> ligasGlobal;
    private static Map<Integer, Jogador> jogadoresSimuladosGlobal;
    private static Map<Integer, TimeUsuario> timesComIdsGlobal;


    public void InicializarConexoes(Connection conn){
        ligaDAO = new LigaDAO(conn);
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

    public static boolean simular(int etapa, Consumer<String> atualizarMensagem) throws SQLException {


        switch (etapa) {
            case 0:
                atualizarMensagem.accept("Verificando ligas e times...");
                ligasGlobal = verificarTimesValidos();
                return true;
            case 1:
                atualizarMensagem.accept("Simulando partidas...");
                simularPartidas();
                return true;
            case 2:
                atualizarMensagem.accept("Calculando pontuações...");
                timesComIdsGlobal = calcularPontuacoes();
                return true;
            case 3:
                atualizarMensagem.accept("Salvando resultados no banco...");
                salvarResultados();
                ocorreu = true;
                return true;
            default:
                return false;
        }
    }

    private static List<Liga> verificarTimesValidos() throws SQLException {
        List<Liga> ligas = ligaDAO.getAllLigas();
        for (Liga liga : ligas) {
            for (Usuario usuario : liga.getUsuarios()) {
                if (!usuario.getTimeUsuario().isValido()) {
                    throw new SQLException("Time inválido.");
                }
            }
        }

        return ligas;
    }

    private static void simularPartidas() {

        jogadoresSimuladosGlobal = new HashMap<>();

        for (Partida partida : partidas) {
            partida.simular();

            for (Jogador j : partida.getClubeCasa().getJogadores()) {
                jogadoresSimuladosGlobal.put(j.getId(), j);
            }

            for (Jogador j : partida.getClubeFora().getJogadores()) {
                jogadoresSimuladosGlobal.put(j.getId(), j);
            }
        }
    }

    private static Map<Integer, TimeUsuario> calcularPontuacoes() throws SQLException {
        Map<Integer, TimeUsuario> allTimes = new HashMap<>();

        for (Liga liga : ligasGlobal) {

            ocorreu = false;
            Map<Integer, TimeUsuario> timesComIds = timeDAO.getAllTimesComIdsPorLigaId(liga.getId());

            for (Map.Entry<Integer, TimeUsuario> entry : timesComIds.entrySet()) {

                ocorreu = false;

                int idTime = entry.getKey();
                TimeUsuario time = entry.getValue();

                List<Jogador> jogadoresAtualizados = new ArrayList<>(time.getJogadores());

                for (int i = 0; i < jogadoresAtualizados.size(); i++) {
                    Jogador original = jogadoresAtualizados.get(i);
                    Jogador simulado = jogadoresSimuladosGlobal.get(original.getId());
                    if (simulado != null) {
                        jogadoresAtualizados.set(i, simulado);
                    }
                }

                Jogador capitaoOriginal = time.getCapitao();
                if(capitaoOriginal != null) {
                    Jogador capitaoSimulado = jogadoresSimuladosGlobal.get(capitaoOriginal.getId());
                    time.removeCapitao();
                    time.setCapitao(capitaoSimulado);
                } else {
                    continue;
                }

                time.setJogadores(new HashSet<>(jogadoresAtualizados));

                ocorreu = true;
                time.calcularPontuacao();

                allTimes.put(idTime, time);
            }
        }

        return allTimes;
    }

        private static void salvarResultados() throws SQLException {
            for (Map.Entry<Integer, TimeUsuario> entry : timesComIdsGlobal.entrySet()) {
                int idTime = entry.getKey();
                TimeUsuario time = entry.getValue();
                timeDAO.inserirPontuacaoTime(idTime, time.getPontuacao());
            }
        }

    // so pode ser usado apos a simulacao para reseta-la
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
                timeDAO.inserirPontuacaoTime(usuario.getId(), 0.0); // n funciona entra no loop)
            }
        }


        // reseta as pontuações dos times
        for (Map.Entry<Integer, TimeUsuario> entry : timesComIdsGlobal.entrySet()) {
            int idTime = entry.getKey();
            TimeUsuario time = entry.getValue();
            timeDAO.inserirPontuacaoTime(idTime, 0.0);
        }


        ocorreu = false;
    }

    // troca dois clubes de lugar em uma partida
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

    // reseta os stats de todos os jogadores que estão em alguma partida
    private static void resetPartidasStats(Set<Partida> partidas){
        for(Partida partida : partidas){
            partida.resetStats();
        }
    }

    // esvazia a lista de partidas
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