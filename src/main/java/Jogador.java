import database.*;

public class Jogador {
    private static int nroJogadores = 0;

    private final int id;
    private String nome;
    private Posicao posicao;
    private Clube clube;
    private double preco;
    private double overall;
    private Stats stats;

    public Jogador(String nome, Posicao posicao, Clube clube, double preco) {
        this.id = nroJogadores++;
        this.nome = nome;
        this.posicao = posicao;
        this.clube = clube;
        this.preco = preco;
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

    public double getPreco() {
        return preco;
    }

    public double getOverall() {
        return overall;
    }


    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}

public class Goleiro extends Jogador{
    public Goleiro(String nome, Posicao posicao, Clube clube, double preco) {
        super(nome, posicao, clube, preco);
    }

    public double calcularPontuacao(){
        return 0;
    }
}

public class Zagueiro extends Jogador{
    public Zagueiro(String nome, Posicao posicao, Clube clube, double preco) {
        super(nome, posicao, clube, preco);
    }

    public double calcularPontuacao(){
        return 0;
    }
}

public class Meia extends Jogador{
    public Meia(String nome, Posicao posicao, Clube clube, double preco) {
        super(nome, posicao, clube, preco);
    }

    public double calcularPontuacao(){
        return 0;
    }
}

public class Atacante extends Jogador{
    public Atacante(String nome, Posicao posicao, Clube clube, double preco) {
        super(nome, posicao, clube, preco);
    }

    public double calcularPontuacao(){
        return 0;
    }
}