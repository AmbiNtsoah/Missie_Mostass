package frames;

import frames.HashUtils;
import frames.AuthService;
import java.sql.*;
import java.beans.Statement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DBConnect implements AuthService {
    private static final String URL = "jdbc:sqlite:users.db";
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private SecretKey secretKey;

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
                
                String createMessagesTableSQL = "CREATE TABLE IF NOT EXISTS messages ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "user_id INTEGER NOT NULL,"
                        + "file_path TEXT NOT NULL,"
                        + "file_hash TEXT NOT NULL," // Ajout de la colonne file_hash
                        + "FOREIGN KEY (user_id) REFERENCES users(id)"
                        + ");";
                try (PreparedStatement pstmt = conn.prepareStatement(createMessagesTableSQL)) {
                    pstmt.execute();
                }
                
             // Création de la table keys
                String createKeysTableSQL = "CREATE TABLE IF NOT EXISTS keys ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "key TEXT NOT NULL"
                        + ");";
                try (PreparedStatement pstmt = conn.prepareStatement(createKeysTableSQL)) {
                    pstmt.execute();
                }

                // Génération de la clé secrète
                generateSecretKey();
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
    
 // Ajout de la méthode getAllUsers
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

    // Méthode pour ajouter un nouvel utilisateur
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

    // Méthode pour lire les informations d'un utilisateur
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

    // Méthode pour mettre à jour les informations d'un utilisateur
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

    // Méthode pour supprimer un utilisateur
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
    
 // Méthode pour réinitialiser le mot de passe
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
    public void addMessage(String userId, String filePath, String fileHash) {
        String sql = "INSERT INTO messages(user_id, file_path, file_hash) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, filePath);
            pstmt.setString(3, fileHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur d'enregistrement dans la base de données");
        }
    }
 

    public List<Message> getMessages(String userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(rs.getInt("id"), rs.getString("user_id"), rs.getString("file_path"), sql));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("Erreur de lecture de la base de données");
        }
        return messages;
    }
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
    private void generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            secretKey = keyGen.generateKey();
            saveSecretKey(secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void saveSecretKey(SecretKey secretKey) {
        String encodedKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String sql = "INSERT INTO keys(key) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encodedKeyString);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SecretKey loadSecretKey() {
        String sql = "SELECT key FROM keys LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String encodedKeyString = rs.getString("key");
                byte[] decodedKey = Base64.getDecoder().decode(encodedKeyString);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
            } else {
                System.out.println("Secret key not found in database. Generating a new key...");
                generateSecretKey();
                return secretKey;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}