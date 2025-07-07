package model;

import java.util.*;

public class Liga {

    private int id;
    private String nome;
    private String senha;
    private List<Usuario> usuarios;

    public Liga(int id, String nome, String senha){
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        usuarios = new ArrayList<>();
    }

    public void addUsuario(Usuario usuario){
        usuarios.add(usuario);
    }

    public void removeUsuario(Usuario usuario){
        usuarios.remove(usuario);
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Usuario> getUsuarios(){
        return usuarios;
    }

    @Override
    public String toString() {
        return "Liga: " + this.nome;
    }
}
