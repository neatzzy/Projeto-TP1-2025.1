package database;

import java.sql.*;

public class DbFunctions {

    // Estabelece conexão com o banco de dados
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection conn=null;
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://maglev.proxy.rlwy.net:47087/"+dbname,user,pass);
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

    // Cria tabela dos times
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

    // Cria tabela dos jogadores
    public void createTableJogadores(Connection conn){
        String createQuery= "CREATE TABLE jogadores"+
                "(jogadorid SERIAL, nome VARCHAR(200), " +
                "posicao VARCHAR(200), " +
                "clube VARCHAR(200), " +
                "preco DOUBLE PRECISION, " +
                "overall DOUBLE PRECISION, " +
                "clubeid INT, " +
                "PRIMARY KEY(jogadorid)," +
                "FOREIGN KEY (clubeid) REFERENCES clubes(clubeid))";
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
        String dataQuery = "SELECT joagdorid FROM jogadores WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("jogadorid");
            }
        }

        throw new SQLException("Jogador not found: " + name);
    }

    // Insere um time na database(nota: quando tiver a implementação das classes, mudar parâmetro para objeto)
    public void insertClube(Connection conn, String name, double overDefesa, double overAtaque){
        String insertQuery = "INSERT INTO clubes (nome, overDefesa, overAtaque) VALUES (?, ?, ?)";
        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setString(1, name);
            insertStmt.setDouble(2, overDefesa);
            insertStmt.setDouble(3, overAtaque);

            int clubsInserted = insertStmt.executeUpdate();

            if (clubsInserted > 0) {
                System.out.println("Club " + name + " inserted.");
            } else {
                System.out.println("Failed to insert club.");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Insere um jogador na database(nota: quando tiver a implementação das classes, mudar parâmetro para objeto)
    public void insertJogador(Connection conn, String name, String posicao, String clube, double preco, double overall){
        String insertQuery = "INSERT INTO jogadores (nome, posicao, preco, overall, clubeid) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setString(1, name);
            insertStmt.setString(2, posicao);
            insertStmt.setDouble(3, preco);
            insertStmt.setDouble(4, overall);

            try {
                insertStmt.setInt(5, getClubeIdByName(conn, clube));
            } catch (SQLException e) {
                System.err.println("Failed to get team ID: " + e.getMessage());
                return;
            }

            int jogadoresInserted = insertStmt.executeUpdate();

            if (jogadoresInserted > 0) {
                System.out.println("Jogador " + name + " inserted.");
            } else {
                System.out.println("Failed to insert Jogador.");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna dados dos times(nota: quando tiver a implementação das classes, retornar lista de objetos do tipo Clube())
    public void getClubesData(Connection conn){
        String dataQuery = "SELECT * FROM clubes";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery);
            ResultSet rs = dataStmt.executeQuery()){
            System.out.println("Clubes found:");
            while(rs.next()){
                System.out.print(rs.getInt("clubeid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getDouble("overDefesa") + " ");
                System.out.println(rs.getDouble("overAtaque"));
            };
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna dado dos jogadores(nota: quando tiver a implementação das classes, retornar lista de objetos do tipo Time())
    public void getJogadoresData(Connection conn){
        String dataQuery = "SELECT * FROM jogadores";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery);
             ResultSet rs = dataStmt.executeQuery()) {
            System.out.println("Jogadores found:");
            while(rs.next()){
                System.out.print(rs.getInt("jogadorid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getDouble("preco") + " ");
                System.out.print(rs.getDouble("overall") + " ");
                System.out.println(rs.getInt("clubeid") + " ");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna jogadores por time(nota: quando tiver a implementação das classes, retornar lista de objetos do tipo Jogador())
    public void getJogadoresByTeamName(Connection conn, String clube){
        String dataQuery = "SELECT * FROM jogadores WHERE clubeid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            try {
                dataStmt.setInt(1, getClubeIdByName(conn, clube));
            } catch (SQLException e) {
                System.err.println("Failed to get clube ID: " + e.getMessage());
                return;
            }

            ResultSet rs = dataStmt.executeQuery();

            System.out.println("Jogadores for clube " + clube + ":");
            while (rs.next()) {
                System.out.print(rs.getString("jogadorid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getString("preco") + " ");
                System.out.print(rs.getString("overall") + " ");
                System.out.println(clube + " " + rs.getInt("clubeid"));
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void getJogadoresByPosition(Connection conn, String posicao){
        String dataQuery = "SELECT * FROM jogadores WHERE posicao = ?";

        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {

            dataStmt.setString(1, posicao);
            ResultSet rs = dataStmt.executeQuery();

            System.out.println("Jogadores " + posicao + ":");
            while (rs.next()) {
                System.out.print(rs.getInt("jogadorid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getString("preco") + " ");
                System.out.println(rs.getString("overall") + " ");
            }
        }

        catch(Exception e){
            System.out.println(e);
        }
    }

    // Remove jogador por nome
    public void deleteJogadorByName(Connection conn, String name){
        String deleteQuery = "DELETE FROM jogadores WHERE nome = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setString(1, name);
            deleteStmt.executeUpdate();
            System.out.println("Jogador " + name + " deleted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Remove time por nome(nota: depois complementar com a retirada dos jogadores!)
    public void deleteClubesByName(Connection conn, String clube){
        String deleteQuery = "DELETE FROM clubes WHERE nome = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setString(1, clube);
            deleteStmt.executeUpdate();
            System.out.println("Clube " + clube + " deleted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // deleta tabela de times
    /*
    public void deleteTeamTable(Connection conn) {
        String deleteQuery = "DROP TABLE IF EXISTS team";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Team table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    */


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
