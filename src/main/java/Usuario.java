public class Usuario {
    private static int nroUsuarios = 0;

    private int id;
    private String nome;
    private double cartoletas;
    private String senha;
    private TimeUsuario timeUsuario;

    public Usuario(String nome, String senha) {
        this.id = nroUsuarios++;
        this.nome = nome;
        this.cartoletas = 110.0;
        this.senha = senha;
        this.timeUsuario = new TimeUsuario(this);
    }

    public static int getNroUsuarios() {
        return nroUsuarios;
    }

    public static void setNroUsuarios(int nroUsuarios) {
        Usuario.nroUsuarios = nroUsuarios;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
