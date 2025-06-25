import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public interface Simulavel {
    void simular();
}

class Partida implements Simulavel{

    private Clube clubeCasa;
    private Clube clubeFora;

    public Partida(Clube clubeCasa, Clube clubeFora) {
        this.clubeCasa = clubeCasa;
        this.clubeFora = clubeFora;
    }

    // controle de gols da partida
    private int golsClubeCasa = 0;
    private int golsClubeFora = 0;


    // para fazer controle das assistências
    private int assistClubeCasa = 0;
    private int assistClubeFora = 0;
    private int golsPenaltiCasa = 0;
    private int golsPenaltiFora = 0;

    // tambem e pra ajudar no controle (nota: jogador tem que ter override do equals e do hashcode)
    List<Jogador> jogadoresGolCasa = new ArrayList<>();
    List<Jogador> jogadoresGolFora = new ArrayList<>();
    List<Jogador> jogadoresAssistenciaCasa = new ArrayList<>();
    List<Jogador> jogadoresAssistenciaFora = new ArrayList<>();

    // controle de bonus
    private boolean cartaoVermelhoClubeCasa = false;
    private boolean cartaoVermelhoClubeFora = false;

    // seed(útil para ver se der um erro na simulação, o experimento será igual)
    private final Random random = new Random();

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

    // simula a partida
    public void simular(){

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
                case ATACANTE -> simularStatusJogador(jogador, timeCasa , 0.5, 0.15, 0.2, 1.5, 0, 0.001, 1.5, 0.15, 0.01);
                case MEIA -> simularStatusJogador(jogador, timeCasa, 1.5, 0.07, 0.35, 1.3, 0, 0.005, 1.5, 0.15, 0.01);
                case ZAGUEIRO -> simularStatusJogador(jogador, timeCasa, 2.0, 0.002, 0.01, 0.2, 0, 0.005, 2, 0.2, 0.015);
                case GOLEIRO -> simularStatusJogador(jogador, timeCasa, 0.1, 0.0001, 0.003, 0.001, 1.8, 0.007, 0.3, 0.15, 0.01);
            }

        }

        // lidar com lógica de penalti, defesapenalti

        Jogador goleiroCasa = new Jogador(0, null, null, null, 0, 0);
        Jogador goleiroFora = new Jogador(0, null, null, null, 0, 0);
        Jogador atacanteMaiorOverallCasa = new Jogador(0, null, null, null, 0, 0);
        Jogador atacanteMaiorOverallFora = new Jogador(0, null, null, null, 0, 0);

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

        goleiroCasa.getStats().setGolsSofridos(this.golsClubeFora);
        goleiroFora.getStats().setGolsSofridos(this.golsClubeCasa);

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
                jogador.getStats().setSg(sgCasa);
            } else {
                jogador.getStats().setSg(sgFora);
            }

        }


    }

    // calcula os status baseando-se na média de cada estatística da posição do jogador e o seu overall
    public void simularStatusJogador(Jogador jogador, boolean timeCasa, double mediaDesarme, double mediaGol, double mediaAssistencia, double mediaFinalizacao, double mediaDefesa, double mediaGolsContra, double mediaFaltasCometidas, double mediaCartaoAmarelo, double mediaCartaoVermelho){

        double fatorPositivoOverall = 1 + 0.3 * (jogador.getOverall() / 100) + calcularBonusClube(timeCasa);
        double fatorNegativoOverall = 2 - 0.9 * (jogador.getOverall() / 100) - calcularBonusClube(timeCasa);

        // se o over medio do adversario eh igual que o do time, nao influencia em nada. se o over eh maior que o do time, gera um numero < 1 que reduz o lambda da poisson (ocorrencia media do evento). complementa a logica de dificuldade do confronto
        double fatorAtaqueAdversario;
        double fatorDefesaAdversario;
        double solidezDefensiva;
        double destaqueOfensivo = 1;

        if (timeCasa){
            fatorAtaqueAdversario = 1 - (clubeFora.getOverAtaque() - clubeCasa.getOverDefesa())/40; // "o quao melhor eh o ataque deles em relacao a nossa defesa?" se > 1, o ataque deles nao tanka a nossa defesa
            fatorDefesaAdversario = 1 - (clubeFora.getOverDefesa() - clubeCasa.getOverAtaque())/40;

            if (fatorAtaqueAdversario < 0.05) fatorAtaqueAdversario = 0.05;
            if (fatorDefesaAdversario < 0.05) fatorDefesaAdversario = 0.05;

            solidezDefensiva = 1 - 0.8 * (clubeFora.getOverDefesa())/100;

            if(jogador.getPosicao() == Posicao.ATACANTE || jogador.getPosicao() == Posicao.MEIA) destaqueOfensivo = Math.pow(1 + (jogador.getOverall() - clubeCasa.getOverAtaque())/100, 5);
            if(destaqueOfensivo < 1) destaqueOfensivo = 1;
        }
        else{
            fatorAtaqueAdversario = 1 - (clubeCasa.getOverAtaque() - clubeFora.getOverDefesa())/40;
            fatorDefesaAdversario = 1 - (clubeCasa.getOverDefesa() - clubeFora.getOverAtaque())/40;

            if (fatorAtaqueAdversario < 0.05) fatorAtaqueAdversario = 0.05;
            if (fatorDefesaAdversario < 0.05) fatorDefesaAdversario = 0.05;

            solidezDefensiva = 1 - 0.8 * (clubeFora.getOverDefesa())/100;

            if(jogador.getPosicao() == Posicao.ATACANTE || jogador.getPosicao() == Posicao.MEIA) destaqueOfensivo = Math.pow(1 + (jogador.getOverall() - clubeFora.getOverAtaque())/100, 5);
            if(destaqueOfensivo < 1) destaqueOfensivo = 1;
        }
        // estatísticas independentes
        jogador.getStats().setDesarmes(poisson((mediaDesarme * fatorPositivoOverall)/Math.sqrt(Math.sqrt(fatorAtaqueAdversario)), this.random)); // teste: quanto melhor o ataque, mais posse e mais chances de desarme
        jogador.getStats().setFinalizacoes(poisson(mediaFinalizacao * fatorPositivoOverall * fatorDefesaAdversario * destaqueOfensivo, this.random));
        jogador.getStats().setDefesas(poisson((mediaDefesa * fatorPositivoOverall)/fatorAtaqueAdversario, this.random));
        jogador.getStats().setFaltasCometidas(poisson(mediaFaltasCometidas * fatorNegativoOverall, this.random));

        // estatísticas que variam resultado da partida
        int gols = poisson(mediaGol * fatorPositivoOverall * fatorDefesaAdversario * solidezDefensiva * destaqueOfensivo, this.random);
        jogador.getStats().setGols(gols);
        if(timeCasa){
            this.golsClubeCasa += gols;
            this.jogadoresGolCasa.add(jogador);
        } else {
            this.golsClubeFora += gols;
            this.jogadoresGolFora.add(jogador);
        }

        int assistencias = poisson(mediaAssistencia * fatorPositivoOverall * fatorDefesaAdversario * solidezDefensiva * destaqueOfensivo, this.random);
        jogador.getStats().setAssistencias(assistencias);
        if(timeCasa){
            this.assistClubeCasa += assistencias;
            this.jogadoresAssistenciaCasa.add(jogador);
        } else {
            this.assistClubeFora += assistencias;
            this.jogadoresAssistenciaFora.add(jogador);
        }

        int gols_contra = poisson(mediaGolsContra * fatorNegativoOverall, this.random);
        jogador.getStats().setGolsContra(gols_contra);
        if(timeCasa){
            this.golsClubeFora += gols_contra;
        } else {
            this.golsClubeCasa += gols_contra;
        }

        // estatísticas limitadas!
        int total_cartoes_amarelos = poisson(mediaCartaoAmarelo * fatorNegativoOverall * 0.6, this.random);
        int total_cartoes_vermelhos = poisson(mediaCartaoVermelho * fatorNegativoOverall * 0.3, this.random);

        if(total_cartoes_amarelos >= 2){
            jogador.getStats().setCartaoAmarelo(2);
            jogador.getStats().setCartaoVermelho(true);
            if(timeCasa){
                this.cartaoVermelhoClubeCasa = true;
            } else {
                this.cartaoVermelhoClubeFora = true;
            }
        } else {
            jogador.getStats().setCartaoAmarelo(total_cartoes_amarelos);
        }

        if(total_cartoes_vermelhos >= 1){
            jogador.getStats().setCartaoVermelho(true);
            if(timeCasa){
                this.cartaoVermelhoClubeCasa = true;
            } else {
                this.cartaoVermelhoClubeFora = true;
            }
        } else {
            jogador.getStats().setCartaoVermelho(false);
        }

        System.out.println("amarelos: " + total_cartoes_amarelos);

        // estatísticas dependentes: gols sofridos, defesa pênalti e sg

    }

    // cálcula se rolou penalti, se o goleiro defendeu, etc
    public void simularPenalti( Jogador goleiroCasa, Jogador goleiroFora, Jogador atacanteMaiorOverallCasa, Jogador atacanteMaiorOverallFora, double mediaPenaltis){

        int penaltis = poisson(mediaPenaltis, this.random);

        // chance de cada equipe receber um penalti
        double penaltiCasa = 0.5 + calcularBonusClube(true);
        double penaltiFora = 0.5 + calcularBonusClube(false);

        // media geral da defesa de penalti de um goleiro
        double mediaDefesaPenaltiCasa = 0.1 * (1 + 0.3 * (goleiroCasa.getOverall() / 100) + calcularBonusClube(true));
        double mediaDefesaPenaltiFora = 0.1 * (1 + 0.3 * (goleiroFora.getOverall() / 100) + calcularBonusClube(false));
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
                    atacanteMaiorOverallCasa.getStats().setGols(atacanteMaiorOverallCasa.getStats().getGols() + 1);
                    this.golsPenaltiCasa++;
                    this.golsClubeCasa++;
                    this.jogadoresGolCasa.add(atacanteMaiorOverallCasa);
                    goleiroFora.getStats().setGolsSofridos(goleiroFora.getStats().getGolsSofridos() + 1);
                }
            } else {
                if(defesasPenaltiCasa > 0){
                    defesasPenaltiCasa--;
                    totalDefesasPenaltiGoleiroCasa++;
                } else {
                    // porfavor
                    atacanteMaiorOverallFora.getStats().setGols(atacanteMaiorOverallFora.getStats().getGols() + 1);
                    this.golsPenaltiFora++;
                    this.golsClubeFora++;
                    this.jogadoresGolFora.add(atacanteMaiorOverallFora);
                    goleiroCasa.getStats().setGolsSofridos(goleiroCasa.getStats().getGolsSofridos() + 1);
                }
            }
            penaltis--;
        }

        goleiroCasa.getStats().setDefesaPenalti(totalDefesasPenaltiGoleiroCasa);
        goleiroFora.getStats().setDefesaPenalti(totalDefesasPenaltiGoleiroFora);

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

        if((golsSemPenalti > assistencias) && (jogadoresAssistencia.isEmpty())){ // se não teve nenhum jogador que fez assitência, não vai ter gol
            for(Jogador jogador: jogadoresGol){
                jogador.getStats().setGols(0);
            }

            if(timeCasa){
                this.golsClubeCasa = this.golsPenaltiCasa; // se teve gol foi só de penalti(sem assitência)
                goleiroFora.getStats().setGolsSofridos(this.golsPenaltiCasa);
            } else {
                this.golsClubeFora = this.golsPenaltiFora; // se teve gol foi só de penalti(sem assitência)
                goleiroFora.getStats().setGolsSofridos(this.golsPenaltiFora);
            }
        }

        if((golsSemPenalti < assistencias) && (jogadoresGol.isEmpty())){ // se não teve gol, não vai ter assistência
            for(Jogador jogador: jogadoresAssistencia){
                jogador.getStats().setAssistencias(0);
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

                if (jogadorSorteado.getPosicao() != Posicao.GOLEIRO){
                    jogadorSorteado.getStats().setGols(jogadorSorteado.getStats().getGols() + 1);
                    if (timeCasa) {
                        this.golsClubeCasa++;
                        goleiroFora.getStats().setGolsSofridos(goleiroFora.getStats().getGolsSofridos() + 1);
                    } else {
                        this.golsClubeFora++;
                        goleiroCasa.getStats().setGolsSofridos(goleiroCasa.getStats().getGolsSofridos() + 1);
                    }
                    golsSemPenalti++;
                }
            }

        } else if(golsSemPenalti > assistencias){ // adicionar assitências!

            while(golsSemPenalti > assistencias) {
                Jogador jogadorSorteado = jogadoresAssistencia.get(random.nextInt(jogadoresAssistencia.size()));
                jogadorSorteado.getStats().setAssistencias(jogadorSorteado.getStats().getAssistencias() + 1);
                if (timeCasa) {
                    this.assistClubeCasa++;
                } else {
                    this.assistClubeFora++;
                }
                assistencias++;
            }
        }

    }

    public void resetStats(){
        for(Jogador jogador : clubeCasa.getJogadores()){
            jogador.getStats().resetStats();
        }
        for(Jogador jogador : clubeFora.getJogadores()){
            jogador.getStats().resetStats();
        }
    }

    public void mostrarResumoPartida() {
        System.out.println("=== RESUMO DA PARTIDA ===");
        System.out.println("Clube da Casa: " + clubeCasa.getNome());
        System.out.println("Clube de Fora: " + clubeFora.getNome());
        System.out.println("Placar: " + clubeCasa.getNome() + " " + golsClubeCasa + " x " + golsClubeFora + " " + clubeFora.getNome());
        System.out.println();

        System.out.println("Gols da Casa:");
        for (Jogador j : jogadoresGolCasa) {
            System.out.println("- " + j.getNome() + " (" + j.getStats().getGols() + " gols)" + j.getPontuacao());
        }
        System.out.println("Gols de Pênalti da Casa: " + golsPenaltiCasa);

        System.out.println("Assistências da Casa:");
        for (Jogador j : jogadoresAssistenciaCasa) {
            System.out.println("- " + j.getNome() + " (" + j.getStats().getAssistencias() + " assistências)");
        }

        System.out.println();
        System.out.println("Gols da Fora:");
        for (Jogador j : jogadoresGolFora) {
            System.out.println("- " + j.getNome() + " (" + j.getStats().getGols() + " gols)" + j.getPontuacao());
            System.out.println("- " + j.getNome() + " (" + j.getStats().getFinalizacoes() + " fin)");
            System.out.println("- " + j.getNome() + " (" + j.getStats().getDesarmes() + " des)");
            if (j.getPosicao() == Posicao.GOLEIRO) System.out.println("- " + j.getNome() + " (" + j.getStats().getDefesas() + " def)");
        }
        System.out.println("Gols de Pênalti da Fora: " + golsPenaltiFora);

        System.out.println("Assistências da Fora:");
        for (Jogador j : jogadoresAssistenciaFora) {
            System.out.println("- " + j.getNome() + " (" + j.getStats().getAssistencias() + " assistências)");
        }

        System.out.println();
        System.out.println("Cartões Vermelhos:");
        if (cartaoVermelhoClubeCasa) {
            System.out.println("- " + clubeCasa.getNome() + " teve pelo menos 1 cartão vermelho.");
        }
        if (cartaoVermelhoClubeFora) {
            System.out.println("- " + clubeFora.getNome() + " teve pelo menos 1 cartão vermelho.");
        }

        System.out.println("\n=========================\n");
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

    public List<Jogador> getJogadoresGolCasa() {
        return jogadoresGolCasa;
    }

    public void setJogadoresGolCasa(List<Jogador> jogadoresGolCasa) {
        this.jogadoresGolCasa = jogadoresGolCasa;
    }

    public List<Jogador> getJogadoresGolFora() {
        return jogadoresGolFora;
    }

    public void setJogadoresGolFora(List<Jogador> jogadoresGolFora) {
        this.jogadoresGolFora = jogadoresGolFora;
    }


    public List<Jogador> getJogadoresAssistenciaCasa() {
        return jogadoresAssistenciaCasa;
    }

    public void setJogadoresAssistenciaCasa(List<Jogador> jogadoresAssistenciaCasa) {
        this.jogadoresAssistenciaCasa = jogadoresAssistenciaCasa;
    }

    public List<Jogador> getJogadoresAssistenciaFora() {
        return jogadoresAssistenciaFora;
    }

    public void setJogadoresAssistenciaFora(List<Jogador> jogadoresAssistenciaFora) {
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
