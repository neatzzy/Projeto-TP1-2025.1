package dao;

import model.Pessoa;
import model.TimeUsuario;
import model.Usuario;
import model.Jogador;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeDAO {

    private final Connection conn;
    private final JogadorDAO jogadorDAO;
    private final UsuarioDAO usuarioDAO;

    public TimeDAO(Connection conn, UsuarioDAO usuarioDAO, JogadorDAO jogadorDAO) {
        this.conn = conn;
        this.jogadorDAO = jogadorDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // Cria tabela de Times( timeid = usuarioid, nome, jogadores, ligaid )
    public void createTableTimes(){
        String createQuery = "CREATE TABLE times (" +
                "timeid PRIMARY KEY, " +
                "nome VARCHAR(200), " +
                "jogadores INTEGER[], " +
                "capitaoid INT," +
                "ligaid INT, " +
                "FOREIGN KEY (timeid) REFERENCES usuarios(usuarioid) ON DELETE CASCADE," +
                "FOREIGN KEY (ligaid) REFERENCES ligas(id) ON DELETE CASCADE" +
                ")";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table times created");
        } catch(Exception e) {
            System.out.println(e);
        }

    }

    // Adiciona Time no banco de dados(sem jogadores)
    public int insertTime(int usuarioid, String name, int ligaid) throws SQLException {

        String insertQuery = "INSERT INTO times (timeid, nome, jogadores, capitaoid, ligaid) VALUES (?, ?, ?, ?, ?) RETURNING timeid";

        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            insertStmt.setInt(1, usuarioid);
            insertStmt.setString(2, name);
            insertStmt.setNull(3, java.sql.Types.ARRAY);
            insertStmt.setNull(4, java.sql.Types.INTEGER);
            insertStmt.setInt(5, ligaid);

            try(ResultSet rs = insertStmt.executeQuery()){
                if(rs.next()) {
                    int newId = rs.getInt("timeid");
                    System.out.println("Time '" + name + "' inserido com ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Falha ao obter ID do time.");
                }
            }

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Insere Jogador no Time do Usuário
    //public void insertJogadorTime(){}

    // Remove Jogador do Time do Usuário
    //public void removeJogadorTime(){}

    // Retorna objeto Time a partir de um id junto com seus jogadores
    public TimeUsuario getTimeById(int id) throws SQLException {

        String dataQuery = "SELECT * FROM times WHERE timeid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, id);

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {

                    Set<Jogador> jogadoresSet = new HashSet<>();

                    int timeId = rs.getInt("timeid");

                    Usuario usuario = (Usuario) usuarioDAO.getUsuarioById(rs.getInt("timeid"));

                    String nome = rs.getString("nome");
                    Array array = rs.getArray("jogadores_ids");

                    if (array != null) {
                        Integer[] jogadoresIds = (Integer[]) array.getArray();
                        for (Integer jogadorId : jogadoresIds) {
                            Jogador jogador = jogadorDAO.getPlayerById(jogadorId);
                            if (jogador != null) {
                                jogadoresSet.add(jogador);
                            }
                        }
                    }
                    int capitaoid = rs.getInt("capitaoid");
                    int ligaid = rs.getInt("ligaid");

                    Jogador jogadorcap = jogadorDAO.getPlayerById(capitaoid);
                    return new TimeUsuario(usuario, jogadoresSet, jogadorcap);
                } else {
                    return null;  // Time não encontrado
                }
            }
        }
    }

    // Retorna lista de todos os objetos Time
    //public void List<TimeUsuario> getAllTimes(){}

    // Retorna objeto Time a partir de um id da liga
    //public void List<TimeUsuario> getAllTimesByLigaId(){}

    //Remove Time por id
    public void deleteTimeById(int id){
        String deleteQuery = "DELETE FROM times WHERE timeid = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setInt(1, id);
            int timeDeletado = deleteStmt.executeUpdate();
            if (timeDeletado > 0) {
                System.out.println("Time com ID " + id + " foi deletado com sucesso.");
            } else {
                System.out.println("Nenhum time encontrado com ID " + id + ".");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // deleta tabela de Times
    public void deleteTimesTable() {
        String deleteQuery = "DROP TABLE IF EXISTS times";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("Time table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
