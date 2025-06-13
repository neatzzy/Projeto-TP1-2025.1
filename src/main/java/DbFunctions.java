import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbFunctions {

    // Estabelece conexão com o banco de dados
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection conn=null;
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://dpg-d161kqmmcj7s73dtqs7g-a.oregon-postgres.render.com:5432/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            } else {
                System.out.println("Connection Failed");
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return conn;
    }

    // Cria tabela dos times (clubeid , nome, overdefesa, overataque)
    public void createTableClubes(Connection conn){
        String createQuery= "CREATE TABLE clubes"+
                "(clubeid SERIAL, nome VARCHAR(200), " +
                "overDefesa DOUBLE PRECISION, " +
                "overAtaque DOUBLE PRECISION, " +
                "PRIMARY KEY(clubeid))";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table clubes created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Cria tabela dos jogadores(jogadorid, nome, posicao, preco, overall, clubeid)
    public void createTableJogadores(Connection conn){
        String createQuery= "CREATE TABLE jogadores"+
                "(jogadorid SERIAL, nome VARCHAR(200), " +
                "posicao VARCHAR(200), " +
                "clube VARCHAR(200), " +
                "preco DOUBLE PRECISION, " +
                "overall DOUBLE PRECISION, " +
                "clubeid INT, " +
                "PRIMARY KEY(jogadorid)," +
                "FOREIGN KEY (clubeid) REFERENCES clubes(clubeid) ON DELETE CASCADE)";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table jogadores created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // retorna o ID de um time pelo nome
    public int getClubeIdByName(Connection conn, String name) throws SQLException {
        String dataQuery = "SELECT clubeid FROM clubes WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("clubeid");
            }
        }

        throw new SQLException("Clube not found: " + name);
    }

    // retorna o ID de um jogador pelo nome
    public int getJogadorIdByName(Connection conn, String name) throws SQLException {
        String dataQuery = "SELECT jogadorid FROM jogadores WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("jogadorid");
            }
        }

        throw new SQLException("Jogador not found: " + name);
    }

    // Insere um time na database e retorna o id desse time
    public int insertClube(Connection conn, String name, double overDefesa, double overAtaque) throws SQLException {
        String insertQuery = "INSERT INTO clubes (nome, overDefesa, overAtaque) VALUES (?, ?, ?)";
        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)){

            insertStmt.setString(1, name);
            insertStmt.setDouble(2, overDefesa);
            insertStmt.setDouble(3, overAtaque);

            int clubsInserted = insertStmt.executeUpdate();

            if(clubsInserted == 0){
                throw new SQLException("Insert failed: nenhum clube inserido");
            }

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1); // Pega o primeiro campo (clubeid)
                    System.out.println("Clube '" + name + "' inserido com ID: " + newId);
                    return newId;
                }
            }

            throw new SQLException("Falha ao obter ID do clube inserido.");

        }catch(SQLException e){
            System.out.println("Erro ao inserir clube: " + e);
            throw e;
        }

    }

    // atualiza os valores de overDefesa e overAtaque do time
    public void atualizarClubeById(Connection conn, int id, double overDefesa, double overAtaque){
        String updateQuery = "UPDATE clubes SET overDefesa = ?, overAtaque = ? WHERE clubeid = ?";
        try(PreparedStatement updateStmt = conn.prepareStatement(updateQuery)){
            updateStmt.setDouble(1, overDefesa);
            updateStmt.setDouble(2, overAtaque);
            updateStmt.setInt(3, id);

            int clubsUpdated = updateStmt.executeUpdate();
            if (clubsUpdated > 0) {
                System.out.println("Club " + id + " updated.");
            } else {
                System.out.println("Failed to update club.");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Insere um jogador na database(nota: quando tiver a implementação das classes, mudar parâmetro para objeto)
    // caso de erro, retorna uma exceção do tipo SQL
    public int insertJogador(Connection conn, String name, String posicao, double preco, double overall, int clubeid) throws SQLException{

        if(!existsClubeById(conn, clubeid)){
            throw new SQLException("Clube not found," + " ID: " + clubeid);
        }

        String insertQuery = "INSERT INTO jogadores (nome, posicao, preco, overall, clubeid) VALUES (?, ?, ?, ?, ?) RETURNING jogadorid";

        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setString(1, name);
            insertStmt.setString(2, posicao);
            insertStmt.setDouble(3, preco);
            insertStmt.setDouble(4, overall);
            insertStmt.setInt(5, clubeid);

            try (ResultSet rs = insertStmt.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("jogadorid");
                    System.out.println("Jogador '" + name + "' inserido com ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Falha ao obter ID do jogador inserido.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
            throw e;
        }

    }


    // retorna um objeto Jogador a partir do id
    public Jogador getPlayerById(Connection conn, int id){
        String dataQuery = "SELECT * FROM jogadores WHERE jogadorid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();

            if (rs.next()) {
                int jogadorId = rs.getInt("id");
                String nome = rs.getString("nome");
                String posicaoStr = rs.getString("posicao");
                double preco = rs.getDouble("preco");
                double overall = rs.getDouble("overall");
                String clubeStr = rs.getString("clube");
                int clubeid = rs.getInt("clubeid");

                System.out.println("Jogador encontrado:" + nome);

                Posicao posicao = Posicao.valueOf(posicaoStr);
                Clube clube = getClubeById(conn, clubeid);

                return new Jogador(jogadorId, nome, posicao, clube, preco, overall);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // retorna um objeto Clube a partir do id
    public Clube getClubeById(Connection conn, int id){
        String dataQuery = "SELECT * FROM clubes WHERE clubeid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();

            if(rs.next()){
                String nome = rs.getString("nome");
                double overAtaque = rs.getDouble("overAtaque");
                double overDefesa = rs.getDouble("overDefesa");
                // não retorna os jogadores do clube( para isso é necessário chamar a função de buscar todos os jogadores do clube
                // se não for feito assim fica em um loop
                Clube clube = new Clube(conn, id, nome, overAtaque, overDefesa);
                return clube;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // retorna se existe tal jogador a partir do id
    public boolean existsJogadorById(Connection conn, int id){
        String dataQuery = "SELECT * FROM jogadores WHERE jogadorid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    // retorna se existe tal clube a partir do id
    public boolean existsClubeById(Connection conn, int id){
        String dataQuery = "SELECT * FROM clubes WHERE clubeid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    // Retorna lista de objetos Clube com todos os clubes
    public List<Clube> getAllCLubes(Connection conn){
        String dataQuery = "SELECT * FROM clubes";
        List<Clube> clubes = new ArrayList<>();
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            ResultSet rs = dataStmt.executeQuery();

            while(rs.next()){
                int id = rs.getInt("clubeid");
                String nome = rs.getString("nome");
                double overAtaque = rs.getDouble("overAtaque");
                double overDefesa = rs.getDouble("overDefesa");
                System.out.println("Clube encontrado:" + nome);
                // não retorna os jogadores do clube( para isso é necessário chamar a função de buscar todos os jogadores do clube
                // se não for feito assim fica em um loop
                Clube clube = new Clube(conn, id, nome, overAtaque, overDefesa);
                clubes.add(clube);
            }

            return clubes;

        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // Retorna lista de todos os jogadores
    public List<Jogador> getAllJogadores(Connection conn, List<Clube> clubes){
        String dataQuery = "SELECT * FROM jogadores";
        List<Jogador> jogadores = new ArrayList<>();

        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            ResultSet rs = dataStmt.executeQuery();
            while (rs.next()) {
                int jogadorId = rs.getInt("jogadorid");
                String nome = rs.getString("nome");
                String posicaoStr = rs.getString("posicao");
                double preco = rs.getDouble("preco");
                double overall = rs.getDouble("overall");
                int clubeId = rs.getInt("clubeid");
                System.out.println("Jogador encontrado:" + nome);

                // verifica se tem clube
                Clube clube = clubes.stream()
                        .filter(c -> c.getId() == clubeId)
                        .findFirst()
                        .orElse(null);

                if (clube == null) {
                    System.out.println("Clube inválido para jogador id " + jogadorId + ", clubeId: " + clubeId);
                    continue;
                }

                Posicao posicao = Posicao.valueOf(posicaoStr);
                // cria jogador e adiciona na lista
                Jogador jogador = new Jogador(jogadorId, nome, posicao, clube, preco, overall);
                jogadores.add(jogador);

                // adiciona o jogador ao novo clube criado!
                clube.addJogador(conn, jogador);
            }

            return jogadores;

        }
        catch(Exception e){
            System.out.println("Não conseguiu retornar todos os jogadores:" + e);
        }

        return null;
    }

    // Retorna lista do tipo Jogador de jogadores por clube
    public List<Jogador> getJogadoresByClub(Connection conn, Clube clube){
        String dataQuery = "SELECT * FROM jogadores WHERE clubeid = ?";
        List<Jogador> jogadores = new ArrayList<>();

        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            if(clube == null){
                System.out.println("clube inválido");
                return null;
            }
            dataStmt.setInt(1, clube.getId());

            try (ResultSet rs = dataStmt.executeQuery()) {
                while (rs.next()) {
                    int jogadorId = rs.getInt("jogadorid");
                    String nome = rs.getString("nome");
                    String posicaoStr = rs.getString("posicao");
                    double preco = rs.getDouble("preco");
                    double overall = rs.getDouble("overall");

                    System.out.println("Jogador encontrado:" + nome);

                    Posicao posicao = Posicao.valueOf(posicaoStr);

                    Jogador jogador = new Jogador(jogadorId, nome, posicao, clube, preco, overall);
                    jogadores.add(jogador);
                    // adiciona ao objeto de clube definido em getClubeById com os jogadores
                    //clube.addJogador(conn, jogador);
                }
            }
            return jogadores;

        }
        catch(Exception e){
            System.out.println(e);
        }

        return null;
    }

    // Retorna lista do tipo Jogador de jogadores por posição
    public List<Jogador> getJogadoresByPosition(Connection conn, String posicao, List<Clube> clubes){
        String dataQuery = "SELECT * FROM jogadores WHERE posicao = ?";
        List<Jogador> jogadores = new ArrayList<>();
        Posicao posicaoPos = Posicao.valueOf(posicao);

        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            dataStmt.setString(1, posicao);

            try (ResultSet rs = dataStmt.executeQuery()) {
                while (rs.next()) {

                    int jogadorId = rs.getInt("jogadorid");
                    String nome = rs.getString("nome");
                    double preco = rs.getDouble("preco");
                    double overall = rs.getDouble("overall");
                    int clubeId = rs.getInt("clubeid");

                    System.out.println("Jogador encontrado:" + nome);

                    // verifica se tem clube
                    Clube clube = clubes.stream()
                            .filter(c -> c.getId() == clubeId)
                            .findFirst()
                            .orElse(null);

                    if (clube == null) {
                        System.out.println("Clube inválido para jogador id " + jogadorId + ", clubeId: " + clubeId);
                        continue;
                    }

                    Jogador jogador = new Jogador(jogadorId, nome, posicaoPos, clube, preco, overall);
                    jogadores.add(jogador);

                    // adiciona ao objeto de clube definido em getClubeById com os jogadores
                    //clube.addJogador(conn, jogador);
                }
            }

            return jogadores;

        }
        catch(Exception e){
            System.out.println(e);
        }

        return null;
    }

    // Remove clube por id(também remove os jogadores associados)
    public void deleteClubeById(Connection conn, int id){

        try {
            String deleteJogadores = "DELETE FROM jogadores WHERE clubeid = ?";
            try (PreparedStatement deleteJogadoresStmt= conn.prepareStatement(deleteJogadores)) {
                deleteJogadoresStmt.setInt(1, id);
                int jogadoresDeleted = deleteJogadoresStmt.executeUpdate();
                System.out.println(jogadoresDeleted + " jogadores deletados vinculados ao clube " + id);
            } catch (SQLException e) {
                System.out.println("Erro ao deletar jogadores vinculados: " + e.getMessage());
            }


            String deleteQuery = "DELETE FROM clubes WHERE clubeid = ?";

            try (PreparedStatement deleteClubeStmt = conn.prepareStatement(deleteQuery)) {
                deleteClubeStmt.setInt(1, id);
                int clubDeleted = deleteClubeStmt.executeUpdate();
                if (clubDeleted > 0) {
                    System.out.println("Clube com ID " + id + " deletado.");
                } else {
                    System.out.println("Nenhum clube encontrado com ID " + id);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Remove jogador por id
    public void deleteJogadorById(Connection conn, int id){
        String deleteQuery = "DELETE FROM jogadores WHERE jogadorid = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setInt(1, id);
            int jogadorDeletado = deleteStmt.executeUpdate();
            if (jogadorDeletado > 0) {
                System.out.println("Jogador com ID " + id + " foi deletado com sucesso.");
            } else {
                System.out.println("Nenhum jogador encontrado com ID " + id + ".");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // deleta tabela de times(também deleta a de jogadores)
    public void deleteClubesTable(Connection conn) {
        String deleteQuery = "DROP TABLE IF EXISTS jogadores, clubes CASCADE;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Team table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // deleta tabela de jogadores
    public void deleteJogadoresTable(Connection conn) {
        String deleteQuery = "DROP TABLE IF EXISTS jogadores CASCADE";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Player table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteTable(Connection conn, String table_name){
        String deleteQuery = "DROP TABLE IF EXISTS " + table_name;
        try(Statement stmt = conn.createStatement()){
            stmt.executeUpdate(deleteQuery);
            System.out.println("Table " + table_name + " deleted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
