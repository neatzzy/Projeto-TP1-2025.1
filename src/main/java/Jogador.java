import database.*;
import java.sql.*;

public class Jogador {
    private int id;
    private String nome;
    private Posicao posicao;
    private Clube clube;
    private double preco;
    private double overall;
    private Stats stats;
    private final DbFunctions db = new DbFunctions();

    public Jogador(int id, String nome, Posicao posicao, Clube clube, double preco, double overall) {
        this.id = id;
        this.nome = nome;
        this.posicao = posicao;
        this.clube = clube;
        this.preco = preco;
        this.overall = overall;
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
}

