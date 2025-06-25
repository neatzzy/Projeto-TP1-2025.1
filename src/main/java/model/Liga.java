package model;

import java.util.*;

public class Liga {

    private int nroUsuarios = 0;
    private String nome;
    private List<Usuario> usuarios;

    public Liga(String nome){
        this.nome = nome;
        usuarios = new ArrayList<>();
    }

    public void removeAll(){
        for (Usuario u : usuarios){
            u.sairLiga(this);
        }
    }

    public void addUsuario(Usuario usuario){
        usuarios.add(usuario);
        this.nroUsuarios++;
    }

    public void removeUsuario(Usuario usuario){
        usuarios.remove(usuario);
        this.nroUsuarios--;
    }

    public List<TimeUsuario> gerarRanking(){
        List<TimeUsuario> times = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            times.add(usuario.getTimeUsuario());
        }

        times.sort(Comparator.comparingDouble(TimeUsuario::getPontuacao).reversed());
        return times;
    }

    public void exibirRanking(List <TimeUsuario> times){
        int ranking = 1;
        for (TimeUsuario time : times) {
            System.out.println(ranking + ": "+ time.getUsuario().getNome() + " - " + time.getPontuacao() + " pontos");
            ranking++;
        }
    }

    public int getNroUsuarios() {
        return nroUsuarios;
    }

    public void setNroUsuarios(int nroUsuarios) {
        this.nroUsuarios = nroUsuarios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuario> getUsuarios(){
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
