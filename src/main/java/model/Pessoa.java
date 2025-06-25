package model;

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

    public static int getNroPessoas() {
        return nroPessoas;
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

