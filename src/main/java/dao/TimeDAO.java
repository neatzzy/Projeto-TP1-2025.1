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

    // Construtor
    public TimeDAO(Connection conn, UsuarioDAO usuarioDAO, JogadorDAO jogadorDAO) {
        this.conn = conn;
        this.jogadorDAO = jogadorDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // Função auxiliar para ajudar na construção do Time
    private TimeUsuario construirTime(ResultSet rs) throws SQLException {
        int timeId = rs.getInt("timeid");
        Usuario usuario = (Usuario) usuarioDAO.getUsuarioById(timeId);
        String nome = rs.getString("nome");
        Array array = rs.getArray("jogadoresids");
        int capitaoid = rs.getInt("capitaoid");

        Set<Jogador> jogadoresSet = new HashSet<>();
        Jogador jogadorcap = null;
        List<Integer> idsParaBuscar = new ArrayList<>();
        if (array != null) {
            Integer[] jogadoresIds = (Integer[]) array.getArray();
            for (Integer jogadorId : jogadoresIds) {
                idsParaBuscar.add(jogadorId);
            }
        }
        if (capitaoid > 0 && !idsParaBuscar.contains(capitaoid)) {
            idsParaBuscar.add(capitaoid);
        }
        if (!idsParaBuscar.isEmpty()) {
            List<Jogador> jogadores = jogadorDAO.getPlayersByIds(idsParaBuscar);
            for (Jogador jogador : jogadores) {
                if (jogador.getId() == capitaoid) {
                    jogadorcap = jogador;
                }
                jogadoresSet.add(jogador);
            }
        }
        return new TimeUsuario(usuario, jogadoresSet, jogadorcap);
    }

    // Cria tabela de Times( timeid = usuarioid, nome, jogadoresids, ligaid )
    public void createTableTimes(){
        String createQuery = "CREATE TABLE times (" +
                "timeid INT PRIMARY KEY, " +
                "nome VARCHAR(200), " +
                "jogadoresids INTEGER[], " +
                "capitaoid INT," +
                "ligaid INT, " +
                "FOREIGN KEY (timeid) REFERENCES usuarios(usuarioid) ON DELETE CASCADE," +
                "FOREIGN KEY (ligaid) REFERENCES ligas(ligaid) ON DELETE CASCADE" +
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

        String insertQuery = "INSERT INTO times (timeid, nome, jogadoresids, capitaoid, ligaid) VALUES (?, ?, ?, ?, ?) RETURNING timeid";

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
    public boolean insertJogadorTime(int jogadorid, int timeid) throws SQLException {

        Jogador jogador = jogadorDAO.getPlayerById(jogadorid);
        if(jogador == null) {
            System.out.println("Jogador com ID " + jogadorid + " não encontrado.");
            return false;
        }

        List<Integer> jogadoresList = new ArrayList<>();

        String selectQuery = "SELECT jogadoresids FROM times WHERE timeid = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setInt(1, timeid);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Time com ID " + timeid + " não encontrado.");
                return false;
            }

            Array array = rs.getArray("jogadoresids");
            if (array != null) {
                jogadoresList.addAll(List.of((Integer[]) array.getArray()));
            }

            if (jogadoresList.contains(jogadorid)) {
                System.out.println("Jogador já está no time.");
                return false;
            }

            jogadoresList.add(jogadorid);

            String updateQuery = "UPDATE times SET jogadoresids = ? WHERE timeid = ?";

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                Array novoArray = conn.createArrayOf("INTEGER", jogadoresList.toArray());
                updateStmt.setArray(1, novoArray);
                updateStmt.setInt(2, timeid);
                updateStmt.executeUpdate();
                System.out.println("Jogador " + jogadorid + " adicionado ao time " + timeid);
                return true;
            }

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Função para time já no banco de dados a partir de uma lista de jogadores
    public void alterarTime(int idUsuario, Set<Jogador> jogadores) throws SQLException {

        Integer[] jogadorIds = jogadores.stream()
                .map(Jogador::getId)
                .toArray(Integer[]::new);

        String updateQuery = "UPDATE times SET jogadoresids = ? WHERE timeid = ?";

        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            Array novoArray = conn.createArrayOf("INTEGER", jogadorIds);
            updateStmt.setArray(1, novoArray);
            updateStmt.setInt(2, idUsuario);
            updateStmt.executeUpdate();
            System.out.println("Escalação atualizada para o time " + idUsuario);
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar escalação: " + e.getMessage());
            throw e;
        }
    }


    // Remove Jogador do Time do Usuário
    public boolean removeJogadorTime(int jogadorid, int timeid) throws SQLException {
        // Verifica se o jogador existe
        if (jogadorDAO.getPlayerById(jogadorid) == null) {
            System.out.println("Jogador com ID " + jogadorid + " não existe.");
            return false;
        }

        List<Integer> jogadoresList = new ArrayList<>();

        String selectQuery = "SELECT jogadoresids FROM times WHERE timeid = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, timeid);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Time com ID " + timeid + " não encontrado.");
                return false;
            }

            Array array = rs.getArray("jogadoresids");
            if (array != null) {
                jogadoresList.addAll(List.of((Integer[]) array.getArray()));
            }

            if (!jogadoresList.contains(jogadorid)) {
                System.out.println("Jogador não está no time.");
                return false;
            }

            jogadoresList.remove(Integer.valueOf(jogadorid));

            String updateQuery = "UPDATE times SET jogadoresids = ? WHERE timeid = ?";

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                Array novoArray = conn.createArrayOf("INTEGER", jogadoresList.toArray());
                updateStmt.setArray(1, novoArray);
                updateStmt.setInt(2, timeid);
                updateStmt.executeUpdate();
                System.out.println("Jogador " + jogadorid + " removido do time " + timeid);
                return true;
            }

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Retorna objeto Time a partir de um id junto com seus jogadores
    public TimeUsuario getTimeById(int id) throws SQLException {

        String dataQuery = "SELECT * FROM times WHERE timeid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, id);

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    return construirTime(rs);
                } else {
                    return null;  // Time não encontrado
                }
            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Retorna lista de todos os objetos Time
    public List<TimeUsuario> getAllTimes() throws SQLException {

        List<TimeUsuario> times = new ArrayList<>();

        String dataQuery = "SELECT * FROM times";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {

            try (ResultSet rs = dataStmt.executeQuery()) {
                while (rs.next()) {
                    times.add(construirTime(rs));
                }
                return times;

            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Retorna objeto Time a partir de um id da liga
    public List<TimeUsuario> getAllTimesByLigaId(int ligaid) throws SQLException {

        List<TimeUsuario> times = new ArrayList<>();

        String dataQuery = "SELECT * FROM times WHERE ligaid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, ligaid);

            try (ResultSet rs = dataStmt.executeQuery()) {
                while (rs.next()) {
                    times.add(construirTime(rs));
                }

                return times;
            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Define capitão do time
    public boolean setCapitao(int timeId, int jogadorId) throws SQLException {
        String sql = "UPDATE times SET capitaoid = ? WHERE timeid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jogadorId);
            stmt.setInt(2, timeId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Remove o capitão do time
    public boolean removeCapitao(int timeId) throws SQLException {
        String sql = "UPDATE times SET capitaoid = NULL WHERE timeid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timeId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println(e);
            throw e;
        }
    }

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
