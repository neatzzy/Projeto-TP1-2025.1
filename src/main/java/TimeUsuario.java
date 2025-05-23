import java.util.Set; // uso para declarar variavel da classe Set
import java.util.HashSet; // uso para instanciar o objeto
        
// avaliar se a classe deve ser publica ou protected\package
public class TimeUsuario {
    private Usuario usuario;     
    private Set<Jogador> jogadores; // jogadores que estao no time do usuario
    private double pontuacao;    
    private double preco;           // preco total do time
    private Jogador capitao;        // jogador, pertencente ao time, que pontua dobrado
    private boolean valido;        // diz se a escalacao eh valida para simulacao (time cheio e capitao definido)
    private int contG, contZ, contM, contA; // contam a qtd de jogadores de cada posicao no time (1 4 3 3)
    private static final int MAX_G = 1, MAX_Z = 4, MAX_M = 3, MAX_A = 3, MAX_TEAMSIZE = 11;
    
    public TimeUsuario(Usuario usuario){
        this.usuario = usuario;
        jogadores = new HashSet<>();
        pontuacao = 0;
        preco = 0;
        capitao = null;
        valido = false;
        contG = contZ = contM = contA = 0;
    }
    // calcula a pontuacao ou retorna falso se a escalacao eh invalida ou se a simulacao nao ocorreu
    public boolean calcularPontuacao(){
        if (!Simulacao.getOcorreu() || valido == false) return false; 
        pontuacao = 0;
        for (Jogador jogadori : jogadores){
            pontuacao += jogadori.getPontuacao();
        }
        pontuacao += capitao.getPontuacao(); // adiciona a pontuacao do capitao duplicadamente
        return true;
    }
    // adiciona jogador no time se houver vaga 
    public boolean addJogador(Jogador jogador){
        if (Simulacao.getOcorreu() || this.isFull() || (preco + jogador.getPreco()) > usuario.getCartoletas()) return false;
        Posicao posicao = jogador.getPosicao();
        if(posicao == Posicao.GOLEIRO){
            if (contG == MAX_G) return false;
            contG++;
        }
        if(posicao == Posicao.ZAGUEIRO){
            if (contZ == MAX_Z) return false;
            contZ++;
        }
        if(posicao == Posicao.MEIA){
            if (contM == MAX_M) return false;
            contM++;
        }
        if(posicao == Posicao.ATACANTE){
            if (contA == MAX_A) return false;
            contA++;
        }
        jogadores.add(jogador);
        preco+=jogador.getPreco();
        if (this.temCapitao() && this.isFull()) valido = true;
        return true;
    }
    // remove jogador do time se houver jogador
    public boolean removeJogador(Jogador jogador){
        if (Simulacao.getOcorreu() || this.isEmpty() || !jogadores.contains(jogador)) return false;
        jogadores.remove(jogador);
        preco-=jogador.getPreco();
        valido = false;
        Posicao posicao = jogador.getPosicao();
        if(posicao == Posicao.GOLEIRO) contG--;
        if(posicao == Posicao.ZAGUEIRO) contZ--;
        if(posicao == Posicao.MEIA) contM--;
        if(posicao == Posicao.ATACANTE) contA--;
        return true;
    }
    // inicializa capitao com jogador somente se jogador estiver no time e nao for o capitao atual
    public boolean setCapitao(Jogador jogador){
        if (Simulacao.getOcorreu() || (capitao != null && capitao.equals(jogador))) return false;
        if (jogadores.contains(jogador)){
            capitao = jogador;
            if (this.isFull()) valido = true;
            return true;
        }
        return false;
    }
    // remove o capitao do time
    public boolean removeCapitao(){
        if (Simulacao.getOcorreu()) return false;
        if (this.temCapitao()){
            capitao = null;
            valido = false;
            return true;
        }
        return false;
    }
    // verifica se o time tem um capitao definido
    private boolean temCapitao(){
        return capitao != null;
    }
    // verifica se o time esta cheio (11 jogadores)
    private boolean isFull(){
        return jogadores.size() == MAX_TEAMSIZE;
    }
    // verifica se o time esta vazio (0 jogadores)
    private boolean isEmpty(){
        return jogadores.isEmpty();
    }
    // remove todos os jogadores do time
    public boolean removerTodosJogadores(){
        if (Simulacao.getOcorreu() == true || this.isEmpty()) return false;
        jogadores.clear();
        contG = contZ = contM = contA = 0;
        capitao = null;
        valido = false;
        preco = 0;
        return true;
    }
    // OBS: ADICIONAR FUTURAMENTE UM toString E UM MODO DE VISUALIZAR O TIME
    // getters
    public Usuario getUsuario() {
        return usuario;
    }

    public Set<Jogador> getJogadores() {
        return jogadores;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public Jogador getCapitao() {
        return capitao;
    }

    public boolean isValido() {
        return valido;
    }

    public double getPreco() {
        return preco;
    }
}
