package dao;

import model.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// nota: mudar para passar objetos como parâmetros? facilitaria visualização do programa

public class UsuarioDAO {

    private final Connection conn;
    private final LigaDAO ligaDAO;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Construtor
    public UsuarioDAO(Connection conn, LigaDAO ligaDAO) {
        this.conn = conn;
        this.ligaDAO = ligaDAO;
    }

    // Função auxiliar para ajudar na construção do Usuário(cria junto com a liga, mas a liga está vazia de usuários)
    // Agora, use construirUsuario(ResultSet, Map<Integer, Liga>) para buscas em lote
    private Pessoa construirUsuario(ResultSet rs) throws SQLException {

        int id = rs.getInt("usuarioid");
        String nome = rs.getString("nome");
        String senha = rs.getString("senha");
        String tipo = rs.getString("tipo");
        int ligaid = rs.getInt("ligaid");

        Liga liga = null;
        if (ligaid > 0) {
            // Usa método otimizado do LigaDAO para buscar várias ligas em lote
            // Aqui, para busca individual, mantém busca única
            liga = ligaDAO.getLigaByID(ligaid);
        }

        if ("user".equalsIgnoreCase(tipo)) {
            return new Usuario(id, nome, senha, UserType.USUARIO, liga);
        } else if ("adminLiga".equalsIgnoreCase(tipo)) {
            return new Usuario(id, nome, senha, UserType.ADMLIGA, liga);
        } else if ("admin".equalsIgnoreCase(tipo)) {
            return new Admin(id, nome, senha, conn);
        } else {
            throw new IllegalArgumentException("Tipo inválido de usuário.");
        }
    }

    // Função auxiliar otimizada para construir o Usuário usando um Map de ligas já carregadas
    private Pessoa construirUsuario(ResultSet rs, java.util.Map<Integer, Liga> ligasMap) throws SQLException {
        int id = rs.getInt("usuarioid");
        String nome = rs.getString("nome");
        String senha = rs.getString("senha");
        String tipo = rs.getString("tipo");
        int ligaid = rs.getInt("ligaid");
        Liga liga = (ligaid > 0) ? ligasMap.get(ligaid) : null;
        if ("user".equalsIgnoreCase(tipo)) {
            return new Usuario(id, nome, senha, UserType.USUARIO, liga);
        } else if ("adminLiga".equalsIgnoreCase(tipo)) {
            return new Usuario(id, nome, senha, UserType.ADMLIGA, liga);
        } else if ("admin".equalsIgnoreCase(tipo)) {
            return new Admin(id, nome, senha, conn);
        } else {
            throw new IllegalArgumentException("Tipo inválido de usuário.");
        }
    }

    // Cria tabela de Usuários( usuarioid, nome, tipo, senha, nomeliga)
    public void createTableUsuarios(){
        String createQuery = "CREATE TABLE usuarios" +
                "(usuarioid SERIAL PRIMARY KEY, nome VARCHAR(200), " +
                "email VARCHAR(255) UNIQUE," +
                "tipo VARCHAR(200)," +
                "senha VARCHAR(200) NOT NULL," +
                "ligaid INTEGER, " +
                "FOREIGN KEY (ligaid) REFERENCES ligas(ligaid) ON DELETE SET NULL)";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table usuarios created");
        } catch(Exception e) {
            System.out.println(e);
        }

    }

    // Adiciona Usuário no banco de dados
    public int insertUsuario(String name, String email, String tipo, String senha, Integer ligaid) throws SQLException {

        String insertQuery = "INSERT INTO usuarios (nome, email, tipo, senha, ligaid) VALUES (?, ?, ?, ?, ?) RETURNING usuarioid";

        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            String senhaHash = encoder.encode(senha);

            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, tipo);
            insertStmt.setString(4, senhaHash);
            if (ligaid == null) {
                insertStmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                insertStmt.setInt(5, ligaid);
            }

            try(ResultSet rs = insertStmt.executeQuery()){
                if(rs.next()) {
                    int newId = rs.getInt("usuarioid");
                    System.out.println("Usuario '" + name + "' inserido com ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Falha ao obter ID do usuario.");
                }
            }

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Insere Usuário em uma Liga
    public boolean insertUsuarioLiga(Usuario usuario, Liga liga) throws SQLException {
        String updateQuery = "UPDATE usuarios SET ligaid = ? WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, liga.getId());
            stmt.setInt(2, usuario.getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                liga.getUsuarios().add(usuario); // adiciona o usuário à lista da liga
                usuario.entrarLiga(liga);           // adiciona objeto da liga ao objeto do usuário
                return true;
            } else {
                System.out.println("Falha ao associar usuário à liga.");
                return false;
            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Insere vários Usuários em uma Liga
    public void insertUsuariosLiga(List<Usuario> usuarios, Liga liga) throws SQLException {
        String updateQuery = "UPDATE usuarios SET ligaid = ? WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            for (Usuario usuario : usuarios) {
                stmt.setInt(1, liga.getId());
                stmt.setInt(2, usuario.getId());
                stmt.addBatch();  // adiciona ao batch
                liga.getUsuarios().add(usuario); // adiciona o usuário à lista da liga
                usuario.entrarLiga(liga);           // adiciona objeto da liga ao objeto do usuário
            }
            stmt.executeBatch(); // executa tudo de uma vez
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }


    // Remove usuário de uma Liga
    public boolean removerUsuarioDaLiga(Usuario usuario, Liga liga) throws SQLException {
        // Atualiza no banco: ligaid do usuário fica NULL
        String updateQuery = "UPDATE usuarios SET ligaid = NULL WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setInt(1, usuario.getId());
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                // Remove da lista/Set de usuários da liga em memória e remove time dele
                liga.getUsuarios().remove(usuario);
                usuario.sairLiga(liga);
                usuario.setTimeUsuario(null);
                return true;
            } else {
                return false; // usuário não encontrado/no banco nenhuma linha afetada
            }
        }
    }


    // Retorna objeto Pessoa a partir de um id
    public Pessoa getUsuarioById(int id) throws SQLException {
        String dataQuery = "SELECT * FROM usuarios WHERE usuarioid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, id);

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    return construirUsuario(rs);
                } else {
                    return null;  // Usuário não encontrado
                }
            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Retorna objeto pessoa a partir de um email
    public Pessoa getUsuarioByEmail(String email) throws SQLException {
        String dataQuery = "SELECT * FROM usuarios WHERE email = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, email);  // seta o parâmetro do PreparedStatement

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    return construirUsuario(rs);
                } else {
                    return null;  // Usuário não encontrado
                }
            }
        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }
    }

    // Retorna uma lista com todos os usuários usando o programa
    public List<Pessoa> getAllUsuarios() throws SQLException {

        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios";

        try (PreparedStatement dataStmt = conn.prepareStatement(query);

             ResultSet rs = dataStmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }

            return usuarios;

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Retorna uma lista com todos os usuários usando o programa
    public List<Pessoa> getAllUsuariosSemLiga() throws SQLException {

        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios WHERE ligaid IS NULL AND tipo = 'user'";


        try (PreparedStatement dataStmt = conn.prepareStatement(query);

             ResultSet rs = dataStmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }

            return usuarios;

        } catch ( SQLException e ) {
            System.out.println(e);
            throw e;
        }

    }

    // Retorna uma lista com todos os usuários de uma certa liga
    public List<Pessoa> getAllUsuariosByLigaId(int ligaid) throws SQLException {

        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios WHERE ligaid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(query)) {

            dataStmt.setInt(1, ligaid);

            try (ResultSet rs = dataStmt.executeQuery()) {

                while (rs.next()) {
                    usuarios.add(construirUsuario(rs));
                }
            }

            return usuarios;

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
            throw e;
        }

    }

    //Remove usuário por id
    public void deleteUsuarioById(int id){
        String deleteQuery = "DELETE FROM usuarios WHERE usuarioid = ?";
        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)){
            deleteStmt.setInt(1, id);
            int usuarioDeletado = deleteStmt.executeUpdate();
            if (usuarioDeletado > 0) {
                System.out.println("Usuario com ID " + id + " foi deletado com sucesso.");
            } else {
                System.out.println("Nenhum usuario encontrado com ID " + id + ".");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Transforma usuário em admin liga após ele criar liga
    public boolean transformarUsuarioEmAdminLiga(int id) throws SQLException {

        String updateQuery = "UPDATE usuarios SET tipo = ? WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, "adminLiga");
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
            throw e;
        }
    }

    // Transforma admin em usuário após ele deletar liga
    public boolean transformarAdminLigaEmUsuario(int id) throws SQLException {

        String updateQuery = "UPDATE usuarios SET tipo = ? WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, "user");
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
            throw e;
        }
    }

    // Atualiza dados do usuário
    public boolean updateUsuario(Usuario usuario) throws SQLException {
        String updateQuery = "UPDATE usuarios SET nome = ?, senha = ? WHERE usuarioid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            String senhaHash = encoder.encode(usuario.getSenha()); // criptografa a nova senha

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, senhaHash);
            stmt.setInt(3, usuario.getId()); // ID fixo
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }

    // deleta tabela de Usuarios
    public void deleteUsuariosTable() {
        String deleteQuery = "DROP TABLE IF EXISTS usuarios";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteQuery);
            System.out.println("User table deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Busca todos os usuários otimizando as queries de liga usando LigaDAO.getLigasByIds
    public List<Pessoa> getAllUsuariosOtimizado() throws SQLException {
        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios";
        List<Integer> ligaIds = new ArrayList<>();
        try (PreparedStatement dataStmt = conn.prepareStatement(query);
             ResultSet rs = dataStmt.executeQuery()) {
            while (rs.next()) {
                int ligaid = rs.getInt("ligaid");
                if (ligaid > 0 && !ligaIds.contains(ligaid)) {
                    ligaIds.add(ligaid);
                }
            }
        }
        // Busca todas as ligas necessárias em uma query só usando método otimizado do LigaDAO
        java.util.Map<Integer, Liga> ligasMap = new java.util.HashMap<>();
        if (!ligaIds.isEmpty()) {
            List<Liga> ligas = ligaDAO.getLigasByIds(ligaIds);
            for (Liga liga : ligas) {
                ligasMap.put(liga.getId(), liga);
            }
        }
        // Agora busca novamente os usuários e monta usando o Map
        try (PreparedStatement dataStmt = conn.prepareStatement(query);
             ResultSet rs = dataStmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(construirUsuario(rs, ligasMap));
            }
        }
        return usuarios;
    }

}
