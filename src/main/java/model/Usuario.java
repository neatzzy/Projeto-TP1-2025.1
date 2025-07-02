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

    @Override
    public String toString() {
        return "ID: " + this.id + " Nome: " + this.nome;
    }

    /////////////////////////

    public UserType getTipo() {
        return tipo;
    }

    public void setTipo(UserType tipo) {
        this.tipo = tipo;
    }

    public double getCartoletas() {
        return cartoletas;
    }

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