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
    private String posicao;

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

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
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

    public void forEach(java.util.function.Consumer<? super java.util.Map.Entry<String, Integer>> action) {
        java.util.Map<String, Integer> statsMap = new java.util.LinkedHashMap<>();
        statsMap.put("Desarmes - " + desarmes + " (+ " + (desarmes * 1.5) + " pts)", desarmes);
        statsMap.put("Gols - " + gols + " (+ " + (gols * 8) + " pts)", gols);
        statsMap.put("Assistências - " + assistencias + " (+ " + (assistencias * 5) + " pts)", assistencias);
        if (posicao != null && (posicao.equalsIgnoreCase("ZAGUEIRO") || posicao.equalsIgnoreCase("GOLEIRO"))) {
            statsMap.put("SG - " + (isSg() ? 1 : 0) + " (+5 pts)", sg ? 1 : 0);
        }
        statsMap.put("Finalizações - " + finalizacoes + " (+ " + (finalizacoes * 0.8) + " pts)", finalizacoes);
        statsMap.put("Defesas - " + defesas + " (+ " + (defesas * 1.5) + " pts)", defesas);
        statsMap.put("Defesa de Pênalti - " + defesaPenalti + " (+ " + (defesaPenalti * 7) + " pts)", defesaPenalti);
        statsMap.put("Gols Contra - " + golsContra + " (- " + (golsContra * 5) + " pts)", golsContra);
        statsMap.put("Cartão Vermelho - " + (cartaoVermelho ? 1 : 0) + " (-3 pts)", cartaoVermelho ? 1 : 0);
        statsMap.put("Gols Sofridos - " + golsSofridos + " (- " + golsSofridos + " pts)", golsSofridos);
        statsMap.put("Cartão Amarelo - " + cartaoAmarelo + " (- " + cartaoAmarelo + " pts)", cartaoAmarelo);
        statsMap.put("Faltas Cometidas - " + faltasCometidas + " (- " + (faltasCometidas * 0.5) + " pts)", faltasCometidas);

        for (java.util.Map.Entry<String, Integer> entry : statsMap.entrySet()) {
            action.accept(entry);
        }
    }
}
