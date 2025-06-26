package dao;

import model.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// nota: mudar para passar objetos como parâmetros? facilitaria visualização do programa

public class LigaDAO {

    private final Connection conn;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public LigaDAO(Connection conn) {
        this.conn = conn;
    }

    // Cria tabela das Ligas (ligaid , nome)
    public void createTableLigas(){
        String createQuery= "CREATE TABLE ligas"+
                "(ligaid SERIAL PRIMARY KEY, nome VARCHAR(200)," +
                "senha VARCHAR(200))";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table ligas created");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Adiciona Liga no banco de dados
    public int insertLiga(String name, String senha) throws SQLException {

        String insertQuery = "INSERT INTO ligas (nome, senha) VALUES (?, ?) RETURNING ligaid";

        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            String senhaHash = encoder.encode(senha);

            insertStmt.setString(1, name);
            insertStmt.setString(2, senhaHash);

            try(ResultSet rs = insertStmt.executeQuery()){
                if(rs.next()) {
                    int newId = rs.getInt("ligaid");
                    System.out.println("Liga '" + name + "' inserido com ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Falha ao obter ID da liga.");
                }
            }

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Retorna objeto Liga pelo id
    public Liga getLigaByID(int id){
        String dataQuery = "SELECT * FROM ligas WHERE ligaid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, id);

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    int ligaid = rs.getInt("ligaid");
                    String nome = rs.getString("nome");
                    String senha = rs.getString("senha");

                    return new Liga(ligaid, nome, senha);
                } else {
                    return null;  // Liga não encontrada
                }
            }
        }
    }

    // Retorna dados de todas as ligas
    public List<Liga> getAllLigas(){
        List<Liga> ligas = new ArrayList<>();
        String query = "SELECT * FROM ligas";

        try (PreparedStatement dataStmt = conn.prepareStatement(query);

             ResultSet rs = dataStmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("ligaid");
                String nome = rs.getString("nome");
                String senha = rs.getString("senha");

                Liga liga = new Liga(id, nome, senha);

                ligas.add(liga);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar ligas: " + e.getMessage());
        }

        return ligas;
    }

    // Remove uma Liga do banco de dados(usuários daquela liga recebem null no ligaid)
    public void deleteLiga(int id) throws SQLException {
        String deleteQuery = "DELETE FROM ligas WHERE ligaid = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setInt(1, id);
            int usuarioDeletado = deleteStmt.executeUpdate();
            if (usuarioDeletado > 0) {
                System.out.println("Liga com ID " + id + " foi deletada com sucesso.");
            } else {
                System.out.println("Nenhuma Liga encontrada com ID " + id + ".");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    // Deleta tabela de Ligas
    public void deleteLigasTable() {
        String deleteQuery = "DROP TABLE IF EXISTS ligas";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Ligas table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
