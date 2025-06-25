package model;

import dao.ClubeDAO;
import dao.JogadorDAO;

import java.sql.Connection;
import java.sql.*;
import java.util.Set; // uso para declarar variavel da classe Set
import java.util.HashSet; // uso para instanciar o objeto

public class Clube {

    private int id;
    private String nome;
    private double overAtaque;      // media aritmetica do overall dos jogadores de ataque
    private double overDefesa;      // media aritmetica do overall dos jogadores de defesa
    private Set<Jogador> jogadores;
    private int jogA, jogD;         // nro de jogadores de ataque/defesa
    private boolean partida;
    private final ClubeDAO clubeDAO;
    private final JogadorDAO jogadorDAO;


    public Clube(ClubeDAO clubeDAO, JogadorDAO jogadorDAO, String nome) throws SQLException{
        this.nome = nome;
        this.clubeDAO = clubeDAO;
        this.jogadorDAO = jogadorDAO;
        overAtaque = 0;                
        overDefesa = 0;                
        jogadores = new HashSet<>();
        jogA = jogD = 0;
        this.partida = false;
        
        // insere o clube no banco de dados 
        this.id = clubeDAO.insertClube(nome, overAtaque, overDefesa);
    }

    //construtor para retornar clube que já está no banco de dados
    public Clube(int id, String nome, double overAtaque, double overDefesa) throws SQLException{
        this.id = id;
        this.nome = nome;
        this.overAtaque = overAtaque;
        this.overDefesa = overDefesa;
        this.clubeDAO = null; // não precisa para retornar dados do banco de dados(nesse caso só quero o objeto Clube com os dados contidos no banco de dados)
        this.jogadorDAO = null; // não precisa para retornar dados do banco de dados(nesse caso só quero o objeto Clube com os dados contidos no banco de dados)
        jogadores = new HashSet<>(); // vai ser att em addJogador
        jogA = jogD = 0; // vai ser att em addJogador
        this.partida = false;
    }

    // adiciona jogador ao clube e retorna false se ele ja estiver no clube
    // como jogador nao existe sem clube, addJogador tambem cria um jogador e nao o recebe como parametro (pode ter as mesmas caracteristicas que outros, mas o id eh diferente)
    public void addJogador(String nomeJogador, Posicao posicao, double preco, double overall) throws SQLException{
        int idJogador = jogadorDAO.insertJogador(nomeJogador, posicao.name(), preco, overall, this.id);
        Jogador jogador = new Jogador(idJogador, nomeJogador, posicao, this, preco, overall);
        jogadores.add(jogador);
        if (this.isAtaque(posicao)){
            jogA++;
            overAtaque = this.recalcOverAtaqueAdd(overall);
        }
        else if (this.isDefesa(posicao)){
            jogD++;
            overDefesa = this.recalcOverDefesaAdd(overall);
        }
        clubeDAO.atualizarClubeById(id, overAtaque, overDefesa);
    }

    // para adicionar jogadores de um objeto clube retornado diretamente do banco de dados(ou seja, os jogadores já estão lá, não precisa inserir no banco)
    public void addJogador(Jogador jogador) throws SQLException{
        jogadores.add(jogador);
        if (this.isAtaque(jogador.getPosicao())){
            jogA++;
        }
        else if (this.isDefesa(jogador.getPosicao())){
            jogD++;
        }
    }

    // remove jogador do clube com parametro objeto e retorna falso se o jogador nao esta no clube
    // soh remove se a simulacao nao aconteceu e apaga o objeto jogador do programa e apaga jogador do BD
    public boolean removeJogador(Connection conn, Jogador jogador)throws SQLException{
        if(Simulacao.getOcorreu()) return false;
        if (!jogadores.remove(jogador)) return false;
        if (this.isAtaque(jogador.getPosicao())){
            jogA--;
            overAtaque = this.recalcOverAtaqueSub(jogador.getOverall()); // revisar essa logica depois
        }
        else if (this.isDefesa(jogador.getPosicao())){
            jogD--;
            overDefesa = this.recalcOverDefesaSub(jogador.getOverall());
        }
        jogador.setClube(null);
        jogadorDAO.deleteJogadorById(jogador.getId());
        clubeDAO.atualizarClubeById(id, overAtaque, overDefesa);
        return true;
    }
    // remove jogador do clube com parametro ID e retorna falso se o jogador nao esta no clube
    // soh remove se a simulacao nao aconteceu e apaga jogador do BD
    public boolean removeJogadorById(Connection conn, int idJogador)throws SQLException{
        if(Simulacao.getOcorreu()) return false;
        Jogador jogador = jogadorDAO.getPlayerById(idJogador); // tenha certeza de que essa funcao esteja corretamente implementada (se o jogador com o id enviado nao existir, ela deve retornar um objeto jogador vazio ou erro)
        if (!jogadores.remove(jogador)) return false;
        if (this.isAtaque(jogador.getPosicao())){
            jogA--;
            overAtaque = this.recalcOverAtaqueSub(jogador.getOverall()); // revisar essa logica depois
        }
        else if (this.isDefesa(jogador.getPosicao())){
            jogD--;
            overDefesa = this.recalcOverDefesaSub(jogador.getOverall());
        }
        jogadorDAO.deleteJogadorById(idJogador);
        clubeDAO.atualizarClubeById(id, overAtaque, overDefesa);
        return true;
    }
    
    private double recalcOverAtaqueAdd(double overall){
        return overAtaque + (overall - overAtaque)/jogA;
    }
    private double recalcOverDefesaAdd(double overall){
        return overDefesa + (overall - overDefesa)/jogD;
    }
    private double recalcOverAtaqueSub(double overall){
        if (jogA == 0) return 0;
        return (overAtaque * (jogA+1) - overall)/jogA;
    }
    private double recalcOverDefesaSub(double overall){
        if (jogD == 0) return 0;
        return (overDefesa * (jogD+1) - overall)/jogD;
    }
    private boolean isAtaque(Posicao posicao) {
        return posicao == Posicao.ATACANTE || posicao == Posicao.MEIA;
    }

    private boolean isDefesa(Posicao posicao) {
        return posicao == Posicao.ZAGUEIRO || posicao == Posicao.GOLEIRO;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getOverAtaque() {
        return overAtaque;
    }

    public double getOverDefesa() {
        return overDefesa;
    }

    public Set<Jogador> getJogadores() {
        return jogadores;
    }

    public boolean getPartida() {
        return partida;
    }

    public void setPartida(boolean partida) {
        this.partida = partida;
    }
}
