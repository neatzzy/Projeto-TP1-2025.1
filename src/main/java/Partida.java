
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class Partida {

    private Clube clubeCasa;
    private Clube clubeFora;

    // controle de gols da partida
    private int golsClubeCasa = 0;
    private int golsClubeFora = 0;


    // para fazer controle das assistências
    private int assistClubeCasa = 0;
    private int assistClubeFora = 0;
    private int golsPenaltiCasa = 0;
    private int golsPenaltiFora = 0;

    // tambem e pra ajudar no controle (nota: jogador tem que ter override do equals e do hashcode)
    Set<Jogador> jogadoresGolCasa = new HashSet<>();
    Set<Jogador> jogadoresGolFora = new HashSet<>();
    Set<Jogador> jogadoresAssistenciaCasa = new HashSet<>();
    Set<Jogador> jogadoresAssistenciaFora = new HashSet<>();

    // controle de bonus
    private boolean cartaoVermelhoClubeCasa = false;
    private boolean cartaoVermelhoClubeFora = false;

    // seed(útil para ver se der um erro na simulação, o experimento será igual)
    private final Random random = new Random(42);

    // calcula a poisson
    public int poisson(double lambda, Random random) {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        return k - 1;
    }

    // simual a partida
    public void simularPartida(){

        Set<Jogador> jogadoresCasa = clubeCasa.getJogadores();
        Set<Jogador> jogadoresFora = clubeFora.getJogadores();

        // lista com todos os jogadores da partida
        List<Jogador> todosJogadores = new ArrayList<>();
        todosJogadores.addAll(jogadoresCasa);
        todosJogadores.addAll(jogadoresFora);

        // simula os status 1 vez para cada jogador
        for(Jogador jogador: todosJogadores){

            // bonus time casa
            boolean timeCasa;
            timeCasa = jogador.getClube().equals(clubeCasa);

            // para cada posição, passa médias referentes à tal
            switch (jogador.getPosicao()) {
                case ATACANTE -> simularStatusJogador(jogador, timeCasa , 0.5, 0.5, 0.2, 2.5, 0, 0.005, 0.8, 0.2, 0.02 );
                case MEIA -> simularStatusJogador(jogador, timeCasa, 2.0, 0.1, 0.3, 1.5, 0, 0.01, 1.5, 0.25, 0.03);
                case ZAGUEIRO -> simularStatusJogador(jogador, timeCasa,2.5, 0.05, 0.05, 0.3, 0, 0.03, 1.2, 0.3, 0.05);
                case GOLEIRO -> simularStatusJogador(jogador, timeCasa, 1.0, 0.01, 0.01, 0.1, 3.0, 0.01, 0.3, 0.1, 0.01);
            }

        }

        // lidar com lógica de penalti, defesapenalti

        Jogador goleiroCasa = new Jogador();
        Jogador goleiroFora = new Jogador();
        Jogador atacanteMaiorOverallCasa = new Jogador();
        Jogador atacanteMaiorOverallFora = new Jogador();

        // loop para achar goleiro e atacante de maior overall(time casa)
        // sugestão: funções para retornar goleiros, atacantes, jogador de maior overall, ...
        for(Jogador jogador: todosJogadores){

            boolean timeCasa;
            timeCasa = jogador.getClube().equals(clubeCasa);

            if(jogador.getPosicao() == Posicao.GOLEIRO){
                if(timeCasa) {
                    goleiroCasa = jogador;
                } else {
                    goleiroFora = jogador;
                }
            }

            if((jogador.getPosicao() == Posicao.ATACANTE)){
                if((timeCasa) && (jogador.getOverall() > atacanteMaiorOverallCasa.getOverall())) {
                    atacanteMaiorOverallCasa = jogador;
                } else if ((!timeCasa) && (jogador.getOverall() > atacanteMaiorOverallFora.getOverall())){
                    atacanteMaiorOverallFora = jogador;
                }
            }

        }

        goleiroCasa.getStatus().setGolsSofridos(this.golsClubeFora);
        goleiroFora.getStatus().setGolsSofridos(this.golsClubeCasa);

        // simulação de penalti(lida com defesas de penalti e com gols sofridos)
        simularPenalti(goleiroCasa, goleiroFora, atacanteMaiorOverallCasa, atacanteMaiorOverallFora, 0.3);

        // lidar com gols e assistencias(gol de penalti não gera assistência)
        if((this.golsClubeCasa - this.golsPenaltiCasa) != this.assistClubeCasa){
            corrigirGolsAssistencias(true, goleiroCasa, goleiroFora);
        }

        // lidar com gols e assistencias(gol de penalti não gera assistência)
        if ((this.golsClubeFora - this.golsPenaltiCasa) != this.assistClubeFora){
            corrigirGolsAssistencias(false, goleiroCasa, goleiroFora);
        }

        // agora que já vi todas as lógicas de tratamento de gol, setar sg

        boolean sgCasa = true;
        boolean sgFora = true;

        if(this.golsClubeFora > 0){
            sgCasa = false;
        }

        if(this.golsClubeCasa > 0){
            sgFora = false;
        }

        for(Jogador jogador: todosJogadores){

            boolean timeCasa;
            timeCasa = jogador.getClube().equals(clubeCasa);

            if(timeCasa){
                jogador.getStatus().setSg(sgCasa);
            } else {
                jogador.getStatus().setSg(sgFora);
            }

        }


    }

    // calcula os status baseando-se na média de cada estatística da posição do jogador e o seu overall
    public void simularStatusJogador(Jogador jogador, boolean timeCasa, double mediaDesarme, double mediaGol, double mediaAssistencia, double mediaFinalizacao, double mediaDefesa, double mediaGolsContra, double mediaFaltasCometidas, double mediaCartaoAmarelo, double mediaCartaoVermelho){

        double fatorPositivoOverall = 1 + 0.3 * (jogador.getOverall() / 100) + calcularBonusClube(timeCasa);
        double fatorNegativoOverall = 2 - 0.9 * (jogador.getOverall() / 100) - calcularBonusClube(timeCasa);

        // estatísticas independentes
        jogador.getStatus().setDesarmes(poisson(mediaDesarme * fatorPositivoOverall, this.random));
        jogador.getStatus().setFinalizacoes(poisson(mediaFinalizacao * fatorPositivoOverall, this.random));
        jogador.getStatus().setDefesas(poisson(mediaDefesa * fatorPositivoOverall, this.random));
        jogador.getStatus().setFaltasCometidas(poisson(mediaFaltasCometidas * fatorNegativoOverall, this.random));

        // estatísticas que variam resultado da partida
        int gols = poisson(mediaGol * fatorPositivoOverall, this.random);
        jogador.getStatus().setGols(gols);
        if(timeCasa){
            this.golsClubeCasa += gols;
            this.jogadoresGolCasa.add(jogador);
        } else {
            this.golsClubeFora += gols;
            this.jogadoresGolFora.add(jogador);
        }

        int assistencias = poisson(mediaAssistencia * fatorPositivoOverall, this.random);
        jogador.getStatus().setAssistencias(assistencias);
        if(timeCasa){
            this.assistClubeCasa += assistencias;
            this.jogadoresAssistenciaCasa.add(jogador);
        } else {
            this.assistClubeFora += assistencias;
            this.jogadoresAssistenciaFora.add(jogador);
        }

        int gols_contra = poisson(mediaGolsContra * fatorNegativoOverall, this.random);
        jogador.getStatus().setGolsContra(gols_contra);
        if(timeCasa){
            this.golsClubeFora += gols_contra;
        } else {
            this.golsClubeCasa += gols_contra;
        }

        // estatísticas limitadas!
        int total_cartoes_amarelos = poisson(mediaCartaoAmarelo * fatorNegativoOverall, this.random);
        int total_cartoes_vermelhos = poisson(mediaCartaoVermelho * fatorNegativoOverall, this.random);

        if(total_cartoes_amarelos >= 2){
            jogador.getStatus().setCartaoAmarelo(2);
            jogador.getStatus().setCartaovermelho(true);
            if(timeCasa){
                this.cartaoVermelhoClubeCasa = true;
            } else {
                this.cartaoVermelhoClubeFora = true;
            }
        } else {
            jogador.getStatus().setCartaoAmarelo(total_cartoes_amarelos);
        }

        if(total_cartoes_vermelhos >= 1){
            jogador.getStatus().setCartaovermelho(true);
            if(timeCasa){
                this.cartaoVermelhoClubeCasa = true;
            } else {
                this.cartaoVermelhoClubeFora = true;
            }
        } else {
            jogador.getStatus().setCartaovermelho(false);
        }

        // estatísticas dependentes: gols sofridos, defesa pênalti e sg

    }

    // cálcula se rolou penalti, se o goleiro defendeu, etc
    public void simularPenalti(Jogador goleiroCasa, Jogador goleiroFora, Jogador atacanteMaiorOverallCasa, Jogador atacanteMaiorOverallFora, double mediaPenaltis){

        int penaltis = poisson(mediaPenaltis, this.random);

        // chance de cada equipe receber um penalti
        double penaltiCasa = 0.5 + calcularBonusClube(true);
        double penaltiFora = 0.5 + calcularBonusClube(false);

        // media geral da defesa de penalti de um goleiro
        double mediaDefesaPenaltiCasa = 0.1 * (1 + 0.3 * (goleiroCasa.getOverall() / 100) + calcularBonusClube(timeCasa));
        double mediaDefesaPenaltiFora = 0.1 * (1 + 0.3 * (goleiroFora.getOverall() / 100) + calcularBonusClube(timeCasa));
        int defesasPenaltiCasa = poisson(mediaDefesaPenaltiCasa, this.random);
        int defesasPenaltiFora = poisson(mediaDefesaPenaltiFora, this.random);

        // total de defesas de penalti
        int totalDefesasPenaltiGoleiroCasa = 0;
        int totalDefesasPenaltiGoleiroFora = 0;

        while(penaltis >= 1){

            double somaProb = penaltiCasa + penaltiFora;
            double probCasa = penaltiCasa / somaProb;
            double probFora = penaltiFora / somaProb;

            double sorteio = random.nextDouble();
            boolean penaltiParaCasa = sorteio < probCasa;

            if (penaltiParaCasa) {
                if(defesasPenaltiFora > 0){
                    defesasPenaltiFora--;
                    totalDefesasPenaltiGoleiroFora++;
                } else {
                    atacanteMaiorOverallCasa.adicionarGol(atacanteMaiorOverallCasa.getStatus().getGols() + 1);
                    this.golsPenaltiCasa++;
                    this.golsClubeCasa++;
                    this.jogadoresGolCasa.add(jogador);
                    goleiroFora.getStatus().setGolsSofridos(goleiroFora.getStatus().getGolsSofridos() + 1);
                }
            } else {
                if(defesasPenaltiCasa > 0){
                    defesasPenaltiCasa--;
                    totalDefesasPenaltiGoleiroCasa++;
                } else {
                    // porfavor
                    atacanteMaiorOverallFora.getStatus().setGols(atacanteMaiorOverallFora.getStatus().getGols() + 1);
                    this.golsPenaltiFora++;
                    this.golsClubeFora++;
                    this.jogadoresGolFora.add(jogador);
                    goleiroCasa.getStatus().addGolsSofridos(goleiroCasa.getStatus().getGolsSofridos() + 1);
                }
            }
            penaltis--;
        }

        goleiroCasa.getStatus().setDefesasPenalti(totalDefesasPenaltiGoleiroCasa);
        goleiroFora.getStatus().setDefesasPenalti(totalDefesasPenaltiGoleiroFora);

    }

    private double calcularBonusClube(boolean timeCasa) {

        double bonusTimeCasa = timeCasa ? 0.05 : 0;

        double facilidadeConfronto = 0;
        if (timeCasa) {
            facilidadeConfronto = (clubeCasa.getOverAtaque() - clubeFora.getOverDefesa()) / 100.0 + (clubeCasa.getOverDefesa() - clubeFora.getOverAtaque()) / 100.0;
        } else {
            facilidadeConfronto = (clubeFora.getOverAtaque() - clubeCasa.getOverDefesa()) / 100.0 + (clubeFora.getOverDefesa() - clubeCasa.getOverAtaque()) / 100.0;
        }

        double bonusCartao = 0;
        if (timeCasa && cartaoVermelhoClubeFora) {
            bonusCartao += 0.1;
        } else if (!timeCasa && cartaoVermelhoClubeCasa) {
            bonusCartao += 0.1;
        }

        return bonusTimeCasa + facilidadeConfronto + bonusCartao;
    }

    // caso de ruim na conta, adicionar um gol/assitencia para algum jogador aleatório que já fez um gol ou assistência(fator hot streak)
    public void corrigirGolsAssistencias(boolean timeCasa, Jogador goleiroCasa, Jogador goleiroFora){

        List<Jogador> jogadoresGol = timeCasa ? this.jogadoresGolCasa : this.jogadoresGolFora;
        List<Jogador> jogadoresAssistencia = timeCasa ? this.jogadoresAssistenciaCasa : this.jogadoresAssistenciaFora;

        int golsSemPenalti = timeCasa ? (this.golsClubeCasa - this.golsPenaltiCasa) : (this.golsClubeFora - this.golsPenaltiFora);
        int assistencias = timeCasa ? this.assistClubeCasa : this.assistClubeFora;

        if((golsSemPenalti > assistencias) && (jogadoresAssistencia.empty())){ // se não teve nenhum jogador que fez assitência, não vai ter gol
            for(Jogador jogador: jogadoresGol){
                jogador.getStatus().setGols(0);
            }

            if(timeCasa){
                this.golsClubeCasa = this.golsPenaltiCasa; // se teve gol foi só de penalti(sem assitência)
                goleiroFora.getStatus().setGolsSofridos(this.golsPenaltiCasa);
            } else {
                this.golsClubeFora = this.golsPenaltiFora; // se teve gol foi só de penalti(sem assitência)
                goleiroFora.getStatus().setGolsSofridos(this.golsPenaltiFora);
            }
        }

        if((golsSemPenalti < assistencias) && (jogadoresGol.empty())){ // se não teve gol, não vai ter assistência
            for(Jogador jogador: jogadoresAssistencia){
                jogador.getStatus().setAssistencias(0);
            }

            if(timeCasa){
                this.assistClubeCasa = 0;
            } else {
                this.assistClubeFora = 0;
            }
        }

        if(golsSemPenalti < assistencias){ // adicionar gols!

            while(golsSemPenalti < assistencias) {
                Jogador jogadorSorteado = jogadoresGol.get(random.nextInt(jogadoresGol.size()));
                jogadorSorteado.getStatus().setGols(jogadorSorteado.getGols() + 1);
                if (timeCasa) {
                    this.golsClubeCasa++;
                    goleiroFora.getStatus().setGolsSofridos(goleiroFora.getStatus().getGolsSofridos() + 1);
                } else {
                    this.golsClubeFora++;
                    goleirocasa.getStatus().setGolsSofridos(goleiroCasa.getStatus().getGolsSofridos() + 1);
                }
                golsSemPenalti++;
            }

        } else if(golsSemPenalti > assistencias){ // adicionar assitências!

            while(golsSemPenalti > assistencias) {
                Jogador jogadorSorteado = jogadoresAssistencia.get(random.nextInt(jogadoresAssistencia.size()));
                jogadorSorteado.getStatus().setAssistencias(jogadorSorteado.getAssistencias() + 1);
                if (timeCasa) {
                    this.assistClubeCasa++;
                } else {
                    this.assistClubeFora++;
                }
                assistencias++;
            }
        }

    }

    // contrutores e setters

    public Clube getClubeCasa() {
        return clubeCasa;
    }

    public void setClubeCasa(Clube clubeCasa) {
        this.clubeCasa = clubeCasa;
    }

    public Clube getClubeFora() {
        return clubeFora;
    }

    public void setClubeFora(Clube clubeFora) {
        this.clubeFora = clubeFora;
    }

    public int getGolsClubeCasa() {
        return golsClubeCasa;
    }

    public void setGolsClubeCasa(int golsClubeCasa) {
        this.golsClubeCasa = golsClubeCasa;
    }

    public int getGolsClubeFora() {
        return golsClubeFora;
    }

    public void setGolsClubeFora(int golsClubeFora) {
        this.golsClubeFora = golsClubeFora;
    }

    public int getAssistClubeCasa() {
        return assistClubeCasa;
    }

    public void setAssistClubeCasa(int assistClubeCasa) {
        this.assistClubeCasa = assistClubeCasa;
    }

    public int getAssistClubeFora() {
        return assistClubeFora;
    }

    public void setAssistClubeFora(int assistClubeFora) {
        this.assistClubeFora = assistClubeFora;
    }

    public int getGolsPenaltiCasa() {
        return golsPenaltiCasa;
    }

    public void setGolsPenaltiCasa(int golsPenaltiCasa) {
        this.golsPenaltiCasa = golsPenaltiCasa;
    }

    public int getGolsPenaltiFora() {
        return golsPenaltiFora;
    }

    public void setGolsPenaltiFora(int golsPenaltiFora) {
        this.golsPenaltiFora = golsPenaltiFora;
    }

    public Set<Jogador> getJogadoresGolCasa() {
        return jogadoresGolCasa;
    }

    public void setJogadoresGolCasa(Set<Jogador> jogadoresGolCasa) {
        this.jogadoresGolCasa = jogadoresGolCasa;
    }

    public Set<Jogador> getJogadoresGolFora() {
        return jogadoresGolFora;
    }

    public void setJogadoresGolFora(Set<Jogador> jogadoresGolFora) {
        this.jogadoresGolFora = jogadoresGolFora;
    }


    public Set<Jogador> getJogadoresAssistenciaCasa() {
        return jogadoresAssistenciaCasa;
    }

    public void setJogadoresAssistenciaCasa(Set<Jogador> jogadoresAssistenciaCasa) {
        this.jogadoresAssistenciaCasa = jogadoresAssistenciaCasa;
    }

    public Set<Jogador> getJogadoresAssistenciaFora() {
        return jogadoresAssistenciaFora;
    }

    public void setJogadoresAssistenciaFora(Set<Jogador> jogadoresAssistenciaFora) {
        this.jogadoresAssistenciaFora = jogadoresAssistenciaFora;
    }

    public boolean isCartaoVermelhoClubeCasa() {
        return cartaoVermelhoClubeCasa;
    }

    public void setCartaoVermelhoClubeCasa(boolean cartaoVermelhoClubeCasa) {
        this.cartaoVermelhoClubeCasa = cartaoVermelhoClubeCasa;
    }

    public boolean isCartaoVermelhoClubeFora() {
        return cartaoVermelhoClubeFora;
    }

    public void setCartaoVermelhoClubeFora(boolean cartaoVermelhoClubeFora) {
        this.cartaoVermelhoClubeFora = cartaoVermelhoClubeFora;
    }

}
