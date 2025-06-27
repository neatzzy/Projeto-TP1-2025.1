package model;

public class Usuario extends Pessoa {
    private double cartoletas = 150.0;
    private UserType tipo;
    private TimeUsuario timeUsuario;
    private Liga liga;

    public Usuario(int id, String nome, String senha, UserType tipo, Liga liga) {
        super(id, nome, senha);
        this.timeUsuario = new TimeUsuario(this);
        this.tipo = tipo;
        this.liga = liga;
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

    /*public boolean deleteLiga() {

        if (liga == null || this.tipo != UserType.ADMLIGA) return false;

        liga.removeAll();
        tipo = UserType.USUARIO;
        TODO: quando tiver o DAO de liga, remover a liga do banco de dados
        return true;
    }*/

    /*public boolean criarLiga(String nome) {

        if (liga != null) return false;

        liga = new Liga(nome);
        liga.addUsuario(this);
        tipo = UserType.ADMLIGA;
        TODO: quando tiver o DAO de liga, adiciona a liga ao banco de dados
        return true;
    }*/

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