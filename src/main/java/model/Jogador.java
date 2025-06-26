package model;

public class Jogador {
    private int id;
    private String nome;
    private Posicao posicao;
    private Clube clube;
    private double preco;
    private double overall;
    private double pontuacao = 0;
    private Stats stats;
    //private final DbFunctions db = new DbFunctions(); sem uso?

    public Jogador(int id, String nome, Posicao posicao, Clube clube, double preco, double overall) {
        this.id = id;
        this.nome = nome;
        this.posicao = posicao;
        this.clube = clube;
        this.preco = preco;
        this.overall = overall;
        this.stats = new Stats();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public Clube getClube() {
        return clube;
    }

    public void setClube(Clube clube) {
        this.clube = clube;
    }

    public double getPreco() {
        return preco;
    }

    public double getOverall() {
        return overall;
    }

    public String getStringPosicao(){
        return switch (posicao) {
            case ATACANTE -> "ATACANTE";
            case MEIA -> "MEIA";
            case ZAGUEIRO -> "ZAGUEIRO";
            case GOLEIRO -> "GOLEIRO";
        };
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public double calcularPontuacao(){
        double pontuacao = 0;
        pontuacao += stats. getDesarmes() * 1.5;
        pontuacao += stats.getGols() * 8.0;
        pontuacao += stats.getAssistencias() * 5.0;
        pontuacao += stats.getFinalizacoes() * 0.8;
        if(stats.getCartaoVermelho()) pontuacao += -3.0;
        pontuacao += stats.getCartaoAmarelo() * -1.0;
        pontuacao += stats.getGolsContra() * -5.0;
        pontuacao += stats.getFaltasCometidas() * -0.5;

        if(this.posicao == Posicao.ZAGUEIRO || this.posicao == posicao.GOLEIRO){
            if(stats.isSg()) pontuacao += 5.0;
            if(this.posicao == Posicao.GOLEIRO){
                pontuacao += stats.getDefesas() * 1.5;
                pontuacao += stats.getDefesaPenalti() * 7.0;
                pontuacao += stats.getGolsSofridos() * -1.0;
            }
        }

        this.pontuacao = pontuacao;
        return pontuacao;
    }

    public double getPontuacao() {
        return this.pontuacao;
    }


}

