import java.sql.Time;

public abstract class Pessoa {
    protected static int nroPessoas = 0;

    protected int id;
    protected String nome;
    protected String senha;

    public Pessoa(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.id = nroPessoas++;
    }
}

class Admin extends Pessoa {
    private Liga liga;

    public Admin(String nome, String senha, Liga liga) {
        super(nome, senha);
        this.liga = liga;
    }
}

class Usuario extends Pessoa {
    private double cartoletas = 150.0;
    private TimeUsuario timeUsuario;
    private Liga liga;

    public Usuario(String nome, String senha) {
        super(nome, senha);
        this.timeUsuario = new TimeUsuario(this);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getCartoletas() {
        return cartoletas;
    }

    public void setCartoletas(double cartoletas) {
        this.cartoletas = cartoletas;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TimeUsuario getTimeUsuario() {
        return timeUsuario;
    }

    public void setTimeUsuario(TimeUsuario timeUsuario) {
        this.timeUsuario = timeUsuario;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }

    public Liga getLiga() {
        return this.liga;
    }
}

