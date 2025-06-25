package model;

public class Stats {
    private int desarmes;
    private int gols;
    private int assistencias;
    private boolean sg;
    private int finalizacoes;
    private int defesas;
    private int defesaPenalti;
    private int golsContra;
    private boolean cartaoVermelho;
    private int golsSofridos;
    private int cartaoAmarelo;
    private int faltasCometidas;

    public int getDesarmes() {
        return desarmes;
    }

    public void setDesarmes(int desarmes) {
        this.desarmes = desarmes;
    }

    public int getGols() {
        return gols;
    }

    public void setGols(int gols) {
        this.gols = gols;
    }

    public int getAssistencias() {
        return assistencias;
    }

    public void setAssistencias(int assistencias) {
        this.assistencias = assistencias;
    }

    public boolean isSg() {
        return sg;
    }

    public void setSg(boolean sg) {
        this.sg = sg;
    }

    public int getFinalizacoes() {
        return finalizacoes;
    }

    public void setFinalizacoes(int finalizacoes) {
        this.finalizacoes = finalizacoes;
    }

    public int getDefesas() {
        return defesas;
    }

    public void setDefesas(int defesas) {
        this.defesas = defesas;
    }

    public int getDefesaPenalti() {
        return defesaPenalti;
    }

    public void setDefesaPenalti(int defesaPenalti) {
        this.defesaPenalti = defesaPenalti;
    }

    public int getGolsContra() {
        return golsContra;
    }

    public void setGolsContra(int golsContra) {
        this.golsContra = golsContra;
    }

    public boolean getCartaoVermelho() {
        return cartaoVermelho;
    }

    public void setCartaoVermelho(boolean cartaoVermelho) {
        this.cartaoVermelho = cartaoVermelho;
    }

    public int getGolsSofridos() {
        return golsSofridos;
    }

    public void setGolsSofridos(int golsSofridos) {
        this.golsSofridos = golsSofridos;
    }

    public int getCartaoAmarelo() {
        return cartaoAmarelo;
    }

    public void setCartaoAmarelo(int cartaoAmarelo) {
        this.cartaoAmarelo = cartaoAmarelo;
    }

    public int getFaltasCometidas() {
        return faltasCometidas;
    }

    public void setFaltasCometidas(int faltasCometidas) {
        this.faltasCometidas = faltasCometidas;
    }

    public void resetStats(){
        this.desarmes = 0;
        this.gols = 0;
        this.assistencias = 0;
        this.sg = false;
        this.finalizacoes = 0;
        this.defesas = 0;
        this.defesaPenalti = 0;
        this.golsContra = 0;
        this.cartaoVermelho = false;
        this.golsSofridos = 0;
        this.cartaoAmarelo = 0;
        this.faltasCometidas = 0;
    }
}
