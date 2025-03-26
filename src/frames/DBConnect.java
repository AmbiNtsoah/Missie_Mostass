package frames;

import frames.HashUtils;
import frames.AuthService;
import java.sql.*;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe qui permet l'interaction de l'application
 * à la base de données sqlite
 */
public class DBConnect implements AuthService {
    private static final String URL = "jdbc:sqlite:users.db";

    /**
     * Crée la table si elle n'éxiste pas encore
     * */
    public DBConnect() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "username TEXT NOT NULL,"
                        + "password TEXT NOT NULL"
                        + ");";
                try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
                    pstmt.execute();
                }
                
             // Création de la table messages
                String createMessagesTableSQL = "CREATE TABLE IF NOT EXISTS messages ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "user_id INTEGER NOT NULL,"
                        + "file_path TEXT NOT NULL,"
                        + "FOREIGN KEY (user_id) REFERENCES users(id)"
                        + ");";
                try (PreparedStatement pstmt = conn.prepareStatement(createMessagesTableSQL)) {
                    pstmt.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode pour permettre la connexion utilisateurs déjà inscrit
     * */
    @Override
    public boolean login(String username, String password) {
        String hashedPassword = HashUtils.hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de connexion à la base de données");
        }
    }

    /**
     * Methode pour permettre l'inscription des nouveaux utilisateurs
     * */
    @Override
    public void register(String username, String password) {
        String hashedPassword = HashUtils.hashPassword(password);
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur d'enregistrement dans la base de données");
        }
    }
    
 /**
  *  Ajout de la méthode getAllUsers
  * @return
  */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de lecture de la base de données");
        }
        return users;
    }

    /**
     *  Méthode pour ajouter un nouvel utilisateur
     * @param username
     * @param password
     */
    public void addUser(String username, String password) {
        String hashedPassword = HashUtils.hashPassword(password);
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur d'enregistrement dans la base de données");
        }
    }

    /**
     *  Méthode pour lire les informations d'un utilisateur
     * @param username
     * @return
     */
    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
            } else {
                throw new CustomException("Utilisateur non trouvé");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de lecture de la base de données");
        }
    }

    /**
     *  Méthode pour mettre à jour les informations d'un utilisateur
     * @param id
     * @param newUsername
     * @param newPassword
     */
    public void updateUser(int id, String newUsername, String newPassword) {
        String hashedPassword = HashUtils.hashPassword(newPassword);
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, hashedPassword);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de mise à jour de la base de données");
        }
    }

    /**
     *  Méthode pour supprimer un utilisateur
     * @param id
     */
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de suppression de la base de données");
        }
    }
    
 /**
  *  Méthode pour réinitialiser le mot de passe
  * @param id
  * @param newPassword
  */
    public void resetPassword(int id, String newPassword) {
        String hashedPassword = HashUtils.hashPassword(newPassword);
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de réinitialisation du mot de passe");
        }
    }
    
    /**
     *  Méthode pour ajouter des messages
     * @param id
     */
    public void addMessage(String userId, String filePath) {
        String sql = "INSERT INTO messages(user_id, file_path) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, filePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur d'enregistrement dans la base de données");
        }
    }
    /**
     *  Méthode pour lister les messages
     * @param id
     */
    public List<Message> getMessages(String userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getInt("id"), rs.getString("user_id"), rs.getString("file_path")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de lecture de la base de données");
        }
        return messages;
    }

    /**
     *  Méthode pour supprimer les messages
     * @param id
     */
    public void deleteMessage(int id) {
        String sql = "DELETE FROM messages WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de suppression de la base de données");
        }
    }
}