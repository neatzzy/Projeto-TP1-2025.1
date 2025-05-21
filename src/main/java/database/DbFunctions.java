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
    public void createTableTeam(Connection conn, String table_name){
        String createQuery= "CREATE TABLE " + table_name+
                "(teamid SERIAL, nome VARCHAR(200), " +
                "overDefesa DOUBLE PRECISION, " +
                "overAtaque DOUBLE PRECISION, " +
                "PRIMARY KEY(teamid))";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table " + table_name + " created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Cria tabela dos jogadores
    public void createTablePlayer(Connection conn, String table_name){
        String createQuery= "CREATE TABLE " + table_name+
                "(playerid SERIAL, nome VARCHAR(200), " +
                "posicao VARCHAR(200), " +
                "clube VARCHAR(200), " +
                "preco DOUBLE PRECISION, " +
                "overall DOUBLE PRECISION, " +
                "teamid INT, " +
                "PRIMARY KEY(playerid)," +
                "FOREIGN KEY (teamid) REFERENCES team(teamid))";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table " + table_name + " created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // retorna o ID de um time pelo nome
    public int getTeamIdByName(Connection conn, String name) throws SQLException {
        String dataQuery = "SELECT teamid FROM team WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("teamid");
            }
        }

        throw new SQLException("Team not found: " + name);
    }

    // retorna o ID de um jogador pelo nome
    public int getPlayerIdByName(Connection conn, String name) throws SQLException {
        String dataQuery = "SELECT playerid FROM player WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("playerid");
            }
        }

        throw new SQLException("Player not found: " + name);
    }

    // Insere um time na database(nota: quando tiver a implementação das classes, mudar parâmetro para objeto)
    public void insertTeam(Connection conn, String name, double overDefesa, double overAtaque){
        String insertQuery = "INSERT INTO team (nome, overDefesa, overAtaque) VALUES (?, ?, ?)";
        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setString(1, name);
            insertStmt.setDouble(2, overDefesa);
            insertStmt.setDouble(3, overAtaque);

            int teamsInserted = insertStmt.executeUpdate();

            if (teamsInserted > 0) {
                System.out.println("Team " + name + " inserted.");
            } else {
                System.out.println("Failed to insert team.");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Insere um jogador na database(nota: quando tiver a implementação das classes, mudar parâmetro para objeto)
    public void insertPlayer(Connection conn, String name, String posicao, String team, double preco, double overall){
        String insertQuery = "INSERT INTO player (nome, posicao, preco, overall, teamid) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setString(1, name);
            insertStmt.setString(2, posicao);
            insertStmt.setDouble(3, preco);
            insertStmt.setDouble(4, overall);

            try {
                insertStmt.setInt(5, getTeamIdByName(conn, team));
            } catch (SQLException e) {
                System.err.println("Failed to get team ID: " + e.getMessage());
                return;
            }

            int playersInserted = insertStmt.executeUpdate();

            if (playersInserted > 0) {
                System.out.println("Player " + name + " inserted.");
            } else {
                System.out.println("Failed to insert player.");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna dados dos times(nota: quando tiver a implementação das classes, retornar lista de objetos do tipo Clube())
    public void getTeamsData(Connection conn){
        String dataQuery = "SELECT * FROM team";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery);
            ResultSet rs = dataStmt.executeQuery()){
            System.out.println("Teams found:");
            while(rs.next()){
                System.out.print(rs.getInt("teamid") + " ");
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
    public void getPlayersData(Connection conn){
        String dataQuery = "SELECT * FROM player";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery);
             ResultSet rs = dataStmt.executeQuery()) {
            System.out.println("Players found:");
            while(rs.next()){
                System.out.print(rs.getInt("playerid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getDouble("preco") + " ");
                System.out.print(rs.getDouble("overall") + " ");
                System.out.println(rs.getInt("teamid") + " ");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna jogadores por time(nota: quando tiver a implementação das classes, retornar lista de objetos do tipo Jogador())
    public void getPlayersByTeamName(Connection conn, String team){
        String dataQuery = "SELECT * FROM player WHERE teamid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){

            try {
                dataStmt.setInt(1, getTeamIdByName(conn, team));
            } catch (SQLException e) {
                System.err.println("Failed to get team ID: " + e.getMessage());
                return;
            }

            ResultSet rs = dataStmt.executeQuery();

            System.out.println("Players for team " + team + ":");
            while (rs.next()) {
                System.out.print(rs.getString("playerid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getString("preco") + " ");
                System.out.print(rs.getString("overall") + " ");
                System.out.println(rs.getString("teamid") + " ");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void getPlayersByPosition(Connection conn, String posicao){
        String dataQuery = "SELECT * FROM player WHERE posicao = ?";

        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {

            dataStmt.setString(1, posicao);
            ResultSet rs = dataStmt.executeQuery();

            System.out.println("Players " + posicao + ":");
            while (rs.next()) {
                System.out.print(rs.getString("playerid") + " ");
                System.out.print(rs.getString("nome") + " ");
                System.out.print(rs.getString("posicao") + " ");
                System.out.print(rs.getString("preco") + " ");
                System.out.print(rs.getString("overall") + " ");
                System.out.println(rs.getString("teamid") + " ");
            }
        }

        catch(Exception e){
            System.out.println(e);
        }
    }

    // Remove jogador por nome
    public void deletePlayerByName(Connection conn, String name){
        String deleteQuery = "DELETE FROM player WHERE nome = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setString(1, name);
            deleteStmt.executeUpdate();
            System.out.println("Player " + name + " deleted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Remove time por nome(nota: depois complementar com a retirada dos jogadores!)
    public void deleteTeamByName(Connection conn, String team){
        String deleteQuery = "DELETE FROM team WHERE nome = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setString(1, team);
            deleteStmt.executeUpdate();
            System.out.println("Team " + team + " deleted.");
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
    public void deletePlayerTable(Connection conn) {
        String deleteQuery = "DROP TABLE IF EXISTS player CASCADE";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Player table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
