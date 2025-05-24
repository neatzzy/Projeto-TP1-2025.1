
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public class Liga {

    private int nroUsuarios = 0;
    private String nome;
    private Set<Usuario> usuarios;

    public Liga(String nome){
        this.nome = nome;
        usuarios = new HashSet<>();
    }

    public void addUsuario(Usuario usuario){
        usuarios.add(usuario);
        this.nroUsuarios++;
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
            System.out.println(posicao + ": "+ time);
        }
    }

    public int getNroUsuarios() {
        return nroUsuarios;
    }

    public void setNroUsuarios(int nroUsuarios) {
        Liga.nroUsuarios = nroUsuarios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Usuarios> getUsuarios(){
        return usuarios;
    }

    public void setUsuarios(List<Usuarios> usuarios){
        this.usuarios = usuarios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }


}
