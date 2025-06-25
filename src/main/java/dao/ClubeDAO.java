package dao;

import model.Clube;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubeDAO {

    private final Connection conn;

    public ClubeDAO(Connection conn) {
        this.conn = conn;
    }

    // Cria tabela dos Clubes (clubeid , nome, overdefesa, overataque)
    public void createTableClubes(){
        String createQuery= "CREATE TABLE clubes"+
                "(clubeid SERIAL, nome VARCHAR(200), " +
                "overAtaque DOUBLE PRECISION, " +
                "overDefesa DOUBLE PRECISION, " +
                "PRIMARY KEY(clubeid))";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table clubes created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Retorna o ID de um Clube pelo nome
    public int getClubeIdByName(String name) throws SQLException {
        String dataQuery = "SELECT clubeid FROM clubes WHERE nome = ?";
        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, name);
            ResultSet rs = dataStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("clubeid");
            }
        }

        throw new SQLException("model.Clube not found: " + name);
    }

    // Insere um time na database e retorna o id desse Clube
    public int insertClube(String name, double overDefesa, double overAtaque) throws SQLException {
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

    // Atualiza os valores de overDefesa e overAtaque do Clube
    public void atualizarClubeById(int id, double overDefesa, double overAtaque){
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

    // Retorna um objeto Clube a partir do id
    public Clube getClubeById(int id){
        String dataQuery = "SELECT * FROM clubes WHERE clubeid = ?";
        try(PreparedStatement dataStmt = conn.prepareStatement(dataQuery)){
            dataStmt.setInt(1, id);
            ResultSet rs = dataStmt.executeQuery();

            if(rs.next()){
                String nome = rs.getString("nome");
                double overAtaque = rs.getDouble("overAtaque");
                double overDefesa = rs.getDouble("overDefesa");
                return new Clube(id, nome, overAtaque, overDefesa); // não retorna os jogadores do clube( para isso é necessário chamar a função de buscar todos os jogadores do clube, se não for feito assim fica em um loop!
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // Retorna se existe tal Clube a partir do id
    public boolean existsClubeById(int id){
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
    public List<Clube> getAllClubes(){
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

                Clube clube = new Clube(id, nome, overAtaque, overDefesa); // não retorna os jogadores do clube(para isso é necessário chamar a função de buscar todos os jogadores do clube)
                clubes.add(clube);
            }

            return clubes;

        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    // Remove Clube por id(também remove os jogadores associados)
    public void deleteClubeById(int id){

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

    // deleta tabela de times(também deleta a de jogadores)
    public void deleteClubesTable() {
        String deleteQuery = "DROP TABLE IF EXISTS jogadores, clubes CASCADE;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Team table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
