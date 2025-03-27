package frames;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Classe HashUtils pour crypter les données avec SHA-256.
 * Cette classe fournit des méthodes pour hasher des mots de passe et convertir des tableaux de bytes en chaînes hexadécimales.
 */
public class HashUtils {
	
	/**
     * Méthode pour hasher un mot de passe avec l'algorithme SHA-256.
     * 
     * @param password Le mot de passe à hasher.
     * @return La chaîne hexadécimale représentant le hash du mot de passe.
     * @throws RuntimeException Si l'algorithme SHA-256 n'est pas disponible.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return byteArrayToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashFile(byte[] fileContent) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(fileContent);
            return byteArrayToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Méthode pour convertir un tableau de bytes en chaîne hexadécimale.
     * 
     * @param bytes Le tableau de bytes à convertir.
     * @return La chaîne hexadécimale représentant le tableau de bytes.
     */
    private static String byteArrayToHexString(byte[] bytes) {
        try (Formatter formatter = new Formatter()) {
			for (byte b : bytes) {
			    formatter.format("%02x", b);
			}
			return formatter.toString();
		}
    }
}

