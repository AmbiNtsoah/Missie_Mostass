package frames;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class VoiceRecorder {
    private static final int RECORD_TIME = 60000; // 1 minute
    private TargetDataLine line;
    private Thread recordingThread;
    private boolean isRecording = false;
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private SecretKey secretKey;
    private DBConnect dbConnect;

    public VoiceRecorder(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void startRecording(String filePath) {
        File audioFile = new File(filePath);
        AudioFormat format = new AudioFormat(16000, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            isRecording = true;

            recordingThread = new Thread(() -> {
                try {
                    System.out.println("Recording started...");
                    AudioInputStream ais = new AudioInputStream(line);
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            recordingThread.start();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void stopRecording(String filePath) {
        if (line != null) {
            line.stop();
            line.close();
            isRecording = false;
            System.out.println("Recording stopped.");
        }

        if (recordingThread != null) {
            try {
                recordingThread.join();
                encryptFile(new File(filePath)); // Chiffrer le fichier après l'enregistrement
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void encryptFile(File file) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            byte[] encryptedContent = cipher.doFinal(fileContent);
            
            // Écrire le contenu chiffré dans un nouveau fichier avec l'extension .enc
            File encryptedFile = new File(file.getPath() + ".enc");
            Files.write(encryptedFile.toPath(), encryptedContent);
            
            // Vérifier que le fichier chiffré existe avant de supprimer l'original
            if (encryptedFile.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("Original file deleted: " + file.getPath());
                } else {
                    System.out.println("Failed to delete original file: " + file.getPath());
                }
                System.out.println("File encrypted and renamed to: " + encryptedFile.getPath());
                
                // Générer et stocker le hachage SHA-256 du fichier chiffré
                String fileHash = HashUtils.hashFile(encryptedContent);
                dbConnect.addMessage("user_id_placeholder", encryptedFile.getPath(), fileHash); // Remplacez par l'ID utilisateur approprié
            } else {
                System.out.println("Failed to create encrypted file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du chiffrement du fichier : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void decryptFile(File file) {
        try {
            File encryptedFile = new File(file.getPath() + ".enc");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] fileContent = Files.readAllBytes(encryptedFile.toPath());
            byte[] decryptedContent = cipher.doFinal(fileContent);
            Files.write(file.toPath(), decryptedContent);

            // Renommer le fichier pour enlever l'extension .enc
            if (encryptedFile.renameTo(file)) {
                System.out.println("File renamed to: " + file.getPath());
            } else {
                System.out.println("Failed to rename file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du déchiffrement du fichier : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void decryptFilehash(File file) {
        try {
            // Utiliser directement le chemin du fichier sans ajouter l'extension .enc
            File encryptedFile = new File(file.getPath());
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] fileContent = Files.readAllBytes(encryptedFile.toPath());
            byte[] decryptedContent = cipher.doFinal(fileContent);
            
            // Écrire le contenu déchiffré dans un nouveau fichier temporaire
            File decryptedFile = new File(file.getPath().replace(".enc", ""));
            Files.write(decryptedFile.toPath(), decryptedContent);
            System.out.println("File decrypted: " + decryptedFile.getPath());
            
            // Lire le fichier déchiffré
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(decryptedFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            System.out.println("Playing file: " + decryptedFile.getPath());
            
            // Supprimer le fichier temporaire après la lecture
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    audioClip.close();
                    decryptedFile.delete();
                    System.out.println("Temporary decrypted file deleted: " + decryptedFile.getPath());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du déchiffrement du fichier : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String generateFilePath() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "recorded_audio_" + timestamp + ".wav";
    }
}