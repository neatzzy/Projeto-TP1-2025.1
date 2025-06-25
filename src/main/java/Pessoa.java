import java.util.List;

public abstract class Pessoa {

    protected int id;
    protected String nome;
    protected String senha;

    public Pessoa(int id, String nome, String senha) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

class Admin extends Pessoa {

    public Admin(int id, String nome, String senha) {
        super(id, nome, senha);
    }

    public boolean simular(List<Liga> ligas){
        return Simulacao.simular(ligas);
    }

    public void resetarSimulacao(List<Liga> ligas){
        Simulacao.resetar(ligas);
    }

    public void deleteUsuario(Usuario usuario, List<Usuario> usuarios){
        usuario.getTimeUsuario().setUsuario(null);
        usuarios.remove(usuario);
    }

    deleteLigas
    getRelatorioLigas
    partidas(modificar, ver, refazer as partidas, remover, adicionar etc)
    deleteUsuario
    addjogador/remove
    addClube/remove (incluso em partidas)

}

class Usuario extends Pessoa {
    private double cartoletas = 150.0;
    private UserType tipo;
    private TimeUsuario timeUsuario;
    private Liga liga;

    public Usuario(int id, String nome, String senha) {
        super(id, nome, senha);
        this.timeUsuario = new TimeUsuario(this);
        tipo = UserType.USUARIO;
    }

    public boolean editarNomeLiga(String novoNome){
        if (tipo != UserType.ADMLIGA) return false;

        liga.setNome(novoNome);
        return true;
    }

    public boolean addUsuarioLiga(Usuario usuario) {

        if (usuario.getLiga() != null || this.tipo != UserType.ADMLIGA) return false;

        usuario.entrarLiga(liga);
        return true;
    }

    public boolean removeUsuarioLiga(Usuario usuario) {

        if (usuario.getLiga() == null || this.tipo != UserType.ADMLIGA) return false;

        usuario.sairLiga(liga);
        return true;
    }

    public boolean deleteLiga() {

        if (liga == null || this.tipo != UserType.ADMLIGA) return false;

        liga.removeAll();
        tipo = UserType.USUARIO;
        // TODO: depois remover a liga da lista global de ligas
        return true;
    }

    public boolean criarLiga(String nome) {

        if (liga != null) return false;

        liga = new Liga(nome);
        liga.addUsuario(this);
        tipo = UserType.ADMLIGA;
        return true;
    }

    public boolean entrarLiga(Liga liga) {

        if (this.liga != null) return false;

        this.liga = liga;
        liga.addUsuario(this);
        return true;
    }

    public boolean sairLiga(Liga liga) {

        if (this.liga == null) return false;

        this.liga = null;
        liga.removeUsuario(this);
        if (tipo == UserType.ADMLIGA) tipo = UserType.USUARIO;
        return true;
    }

    /////////////////////////

    public double getCartoletas() {
        return cartoletas;
    }

    public void setCartoletas(double cartoletas) { this.cartoletas = cartoletas; }

    public TimeUsuario getTimeUsuario() {
        return timeUsuario;
    }

    public void setTimeUsuario(TimeUsuario timeUsuario) {
        this.timeUsuario = timeUsuario;
    }

    public Liga getLiga() {
        return this.liga;
    }
}
