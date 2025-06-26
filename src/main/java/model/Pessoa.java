package model;

public abstract class Pessoa {

    protected int id;
    protected String nome;
    protected String senha;

    public Pessoa(int id, String nome, String senha) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
    }

    public int getId() { return id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome;}

    public String getSenha() { return senha; }

    public void setSenha(String senha) { this.senha = senha; }
}

