package dao;

import model.Pessoa;
import model.UserType;
import model.Usuario;
import model.Admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// nota: mudar para passar objetos como parâmetros? facilitaria visualização do programa
// adicionar funções: adicionar e remover liga

public class UsuarioDAO {

    private final Connection conn;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    // Cria tabela de Usuários( usuarioid, nome, tipo, senha, nomeliga)
    public void createTableUsuarios(){
        String createQuery = "CREATE TABLE usuarios" +
                "(usuarioid SERIAL, nome VARCHAR(200), " +
                "email VARCHAR(255) UNIQUE," +
                "tipo VARCHAR(200)," +
                "senha VARCHAR(200)," +
                "FOREIGN KEY (ligaid) REFERENCES ligas(id) ON DELETE SET NULL";
        try(PreparedStatement createStmt = conn.prepareStatement(createQuery)){
            createStmt.executeUpdate();
            System.out.println("Table usuarios created");
        } catch(Exception e) {
            System.out.println(e);
        }

    }

    // Adiciona Usuário no banco de dados
    public int insertUsuario(String name, String email, String tipo, String senha, int ligaid) throws SQLException {

        String insertQuery = "INSERT INTO usuarios (nome, email, tipo, senha, ligaid) VALUES (?, ?, ?, ?, ?) RETURNING usuarioid";

        try(PreparedStatement insertStmt = conn.prepareStatement(insertQuery)){

            String senhaHash = encoder.encode(senha);

            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, tipo);
            insertStmt.setString(4, senhaHash);
            insertStmt.setInt(5, ligaid);

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
    /*
    public void insertUsuarioLiga(Pessoa p, Liga l) throws SQLException {

        int ligaid = l.

    }
    */


    // Retorna objeto Pessoa a partir de um id
    public Pessoa getUsuarioById(int id) throws SQLException {
        String dataQuery = "SELECT * FROM usuarios WHERE usuarioid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setInt(1, id);

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("usuarioid");
                    String nome = rs.getString("nome");
                    String tipo = rs.getString("tipo");
                    String senha = rs.getString("senha");
                    int ligaid = rs.getInt("ligaid");

                    Pessoa usuario;

                    if ("user".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(usuarioId, nome, senha, UserType.USUARIO);
                    } else if ("adminLiga".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(usuarioId, nome, senha, UserType.ADMLIGA);
                    } else if ("admin".equalsIgnoreCase(tipo)) {
                        usuario = new Admin(usuarioId, nome, senha, conn); // lidar com a lógica da liga!
                    } else {
                        throw new IllegalArgumentException("Tipo inválido de usuário.");
                    }

                    return usuario;
                } else {
                    return null;  // Usuário não encontrado
                }
            }
        }
    }

    // Retorna objeto pessoa a partir de um email
    public Pessoa getUsuarioByEmail(String email) throws SQLException {
        String dataQuery = "SELECT * FROM usuarios WHERE email = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(dataQuery)) {
            dataStmt.setString(1, email);  // seta o parâmetro do PreparedStatement

            try (ResultSet rs = dataStmt.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("usuarioid");
                    String nome = rs.getString("nome");
                    String tipo = rs.getString("tipo");
                    String senha = rs.getString("senha");
                    int ligaid = rs.getInt("ligaid");

                    Pessoa usuario;

                    if ("user".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(usuarioId, nome, senha, UserType.USUARIO);
                    } else if ("adminLiga".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(usuarioId, nome, senha, UserType.ADMLIGA);
                    } else if ("admin".equalsIgnoreCase(tipo)) {
                        usuario = new Admin(usuarioId, nome, senha, conn); // lidar com a lógica da liga!
                    } else {
                        throw new IllegalArgumentException("Tipo inválido de usuário.");
                    }

                    return usuario;
                } else {
                    return null;  // usuário não encontrado
                }
            }
        }
    }

    // Retorna uma lista com todos os usuários usando o programa
    public List<Pessoa> getAllUsuarios() {

        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios";

        try (PreparedStatement dataStmt = conn.prepareStatement(query);

             ResultSet rs = dataStmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("usuarioid");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo");
                String senha = rs.getString("senha");
                int ligaid = rs.getInt("ligaid");

                Pessoa usuario;

                // Instancia a subclasse certa com base no tipo
                if ("user".equalsIgnoreCase(tipo)) {
                    usuario = new Usuario(id, nome, senha, UserType.USUARIO);
                } else if ("adminLiga".equalsIgnoreCase(tipo)) {
                    usuario = new Usuario(id, nome, senha, UserType.ADMLIGA);
                } else if ("admin".equalsIgnoreCase(tipo)) {
                    usuario = new Admin(id, nome, senha, conn); // lidar com a lógica da liga!
                } else {
                    throw new IllegalArgumentException("Tipo inválido de usuário.");
                }

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
        }

        return usuarios;
    }

    // Retorna uma lista com todos os usuários de uma certa liga
    public List<Pessoa> getAllUsuariosByLigaId(int ligaid) {

        List<Pessoa> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios WHERE ligaid = ?";

        try (PreparedStatement dataStmt = conn.prepareStatement(query)) {

            dataStmt.setInt(1, ligaid);

            try (ResultSet rs = dataStmt.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("usuarioid");
                    String nome = rs.getString("nome");
                    String email = rs.getString("email");
                    String tipo = rs.getString("tipo");
                    String senha = rs.getString("senha");
                    int ligaIdDoBanco = rs.getInt("ligaid");

                    Pessoa usuario;

                    if ("user".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(id, nome, senha, UserType.USUARIO);
                    } else if ("adminLiga".equalsIgnoreCase(tipo)) {
                        usuario = new Usuario(id, nome, senha, UserType.ADMLIGA);
                    } else if ("admin".equalsIgnoreCase(tipo)) {
                        usuario = new Admin(id, nome, senha, conn); // lidar com a lógica da liga!
                    } else {
                        throw new IllegalArgumentException("Tipo inválido de usuário.");
                    }

                    usuarios.add(usuario);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários: " + e.getMessage());
        }

        return usuarios;
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

}
