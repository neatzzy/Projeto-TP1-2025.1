import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Hello, World!");

        Dotenv dotenv = Dotenv.load();
        String db_name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String pass = dotenv.get("DB_PASSWORD");

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db(db_name, user, pass);
        
        Clube clube = new Clube(conn, "BRASIL");
        clube.addJogador(conn, "Pietro Collares", Posicao.ATACANTE, 1.50, 60);
        clube.addJogador(conn, "Mário Sérgio Rei da América 2025", Posicao.ATACANTE, 3, 60);
        clube.addJogador(conn, "Pedro Haul", Posicao.ATACANTE, 1.50, 60);
        clube.addJogador(conn, "Odegar", Posicao.MEIA, 4, 60);
        clube.addJogador(conn, "Rafael Carvalheira", Posicao.MEIA, 10, 60);
        clube.addJogador(conn, "Maycon Baycon", Posicao.MEIA, 20, 60);
        clube.addJogador(conn, "Reinaldo", Posicao.ZAGUEIRO, 20, 100000);
        clube.addJogador(conn, "Péo Lelé", Posicao.ZAGUEIRO, 5, 100000);
        clube.addJogador(conn, "Gunché", Posicao.ZAGUEIRO, 5, 100000);
        clube.addJogador(conn, "Murilo Malnati Ismael", Posicao.ZAGUEIRO, 10, 100000);
        clube.addJogador(conn, "Doudou Hikarty", Posicao.GOLEIRO, 10, 100000);
        clube.addJogador(conn, "Vinícius Júnior", Posicao.ATACANTE, 8.98, 60);
        
        Clube clube2 = new Clube(conn, "ARGENTINA");
        clube2.addJogador(conn, "Meci Careca", Posicao.ATACANTE, 1.50, 60);
        clube2.addJogador(conn, "Messi na Centroavância", Posicao.ATACANTE, 3, 60);
        clube2.addJogador(conn, "Messi Normal", Posicao.ATACANTE, 1.50, 60);
        clube2.addJogador(conn, "Arrascaeta (Naturalizado)", Posicao.MEIA, 4, 60);
        clube2.addJogador(conn, "Enso Fernandes", Posicao.MEIA, 20, 60);
        clube2.addJogador(conn, "MacAllister", Posicao.MEIA, 20, 60);
        clube2.addJogador(conn, "Montiel", Posicao.ZAGUEIRO, 20, 100000);
        clube2.addJogador(conn, "Britez", Posicao.ZAGUEIRO, 5, 100000);
        clube2.addJogador(conn, "Garro (Saída de Bola)", Posicao.ZAGUEIRO, 5, 100000);
        clube2.addJogador(conn, "Benito Benitez", Posicao.ZAGUEIRO, 10, 100000);
        clube2.addJogador(conn, "Messi Gigante", Posicao.GOLEIRO, 10, 100000);
        clube2.addJogador(conn, "Tieco Arbanto Baratona", Posicao.ATACANTE, 8.98, 60);
        
        Usuario pedro = new Usuario("pedro", "1234");
        TimeUsuario time = pedro.getTimeUsuario();
        
        for(Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }
        
        Usuario mauroPatrao = new Usuario("patrao", "1234");
        TimeUsuario time2 = mauroPatrao.getTimeUsuario();
        
        for(Jogador jogador : clube.getJogadores()){
            time.addJogador(jogador);
            time.setCapitao(jogador);
        }
        
        for(Jogador jogador : clube2.getJogadores()){
            time2.addJogador(jogador);
            time2.setCapitao(jogador);
        }
        
        time.imprimirTime();
        time2.imprimirTime();
        
       Liga liga = new Liga("Cicartola");
       
       liga.addUsuario(pedro);
       liga.addUsuario(mauroPatrao);
       
       Simulacao.addPartida(clube, clube2);
       
       List<Clube> clubes = new ArrayList<>();
       clubes.add(clube);
       clubes.add(clube2);
       System.out.println("ataque over 1: " + clube.getOverAtaque());
       System.out.println("defesa over 1: " + clube.getOverDefesa());
       System.out.println("ataque over 2: " + clube2.getOverAtaque());
       System.out.println("defesa over 2: " + clube2.getOverDefesa());
       
       Simulacao.gerarPartidasAleatorias(clubes);
       Simulacao.simular(liga);
       
       liga.exibirRanking(liga.gerarRanking());
    }
}