package dao;

import model.Jogador;
import model.Posicao;
import model.Clube;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JogadorDAO {

    private final Connection conn;
    private final ClubeDAO clubeDAO;

    public JogadorDAO(Connection conn) {
        this.conn = conn;
        this.clubeDAO = new ClubeDAO(conn);
    }

    // Cria tabela dos Jogadores(jogadorid, nome, posicao, preco, overall, clubeid)
    public void createTableJogadores(){
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

    // retorna o ID de um Jogador pelo nome
    public int getJogadorIdByName(String name) throws SQLException {
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

    // Insere um Jogador na database
    public int insertJogador(String name, String posicao, double preco, double overall, int clubeid) throws SQLException{

        if(!clubeDAO.existsClubeById(clubeid)){
            throw new SQLException("model.Clube not found," + " ID: " + clubeid);
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

    // Retorna um objeto Jogador a partir do id
    public Jogador getPlayerById(int id){
        String dataQuery = "SELECT * FROM jogadores WHERE jogadorid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();

            if (rs.next()) {
                int jogadorId = rs.getInt("jogadorid");
                String nome = rs.getString("nome");
                String posicaoStr = rs.getString("posicao");
                double preco = rs.getDouble("preco");
                double overall = rs.getDouble("overall");
                String clubeStr = rs.getString("clube");
                int clubeid = rs.getInt("clubeid");

                System.out.println("model.Jogador encontrado:" + nome);

                Posicao posicao = Posicao.valueOf(posicaoStr);
                Clube clube = clubeDAO.getClubeById(clubeid);
                return new Jogador(jogadorId, nome, posicao, clube, preco, overall);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // Retorna se existe tal jogador a partir do id
    public boolean existsJogadorById(int id){
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

    // Retorna lista de todos os Jogadores
    public List<Jogador> getAllJogadores(List<Clube> clubes){
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
                clube.addJogador(jogador);
            }

            return jogadores;

        }
        catch(Exception e){
            System.out.println("Não conseguiu retornar todos os jogadores:" + e);
        }

        return null;
    }

    // Retorna lista do tipo Jogador de jogadores por clube
    public List<Jogador> getJogadoresByClub(Clube clube){
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
                    jogadores.add(jogador); // adiciona ao objeto de clube definido em getClubeById com os jogadores

                }
            }
            return jogadores;

        }
        catch(Exception e){
            System.out.println(e);
        }

        return null;
    }

    // Remove Jogador por id
    public void deleteJogadorById(int id){
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

    // deleta tabela de Jogadores
    public void deleteJogadoresTable() {
        String deleteQuery = "DROP TABLE IF EXISTS jogadores CASCADE";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Player table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
