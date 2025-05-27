public class Zagueiro extends Jogador{
    public Zagueiro(int id, String nome, Posicao posicao, Clube clube, double preco, double overall) {
        super(id, nome, posicao, clube, preco, overall);
    }

    public double calcularPontuacao(Stats stats){
        double pontuacao = 0;

        pontuacao += stats.getDesarmes() * 1.5;
        pontuacao += stats.getGols() * 8.0;
        pontuacao += stats.getAssistencias() * 0.5;
        if(stats.isSg()) pontuacao += 5.0;
        pontuacao += stats.getFinalizacoes() * 0.8;
        pontuacao += stats.getGolsContra() * -5.0;
        if(stats.getCartaoVermelho()) pontuacao += -3.0;
        pontuacao += stats.getCartaoAmarelo() * -1.0;
        pontuacao += stats.getFaltasCometidas() * -0.5;

        return pontuacao;
    }
}
