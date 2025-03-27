package frames;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Classe qui affiche l'interface graphique 
 * L'utilisateur pourra enregistrer son message audio
 */
public class VoiceApp extends JFrame {
	private DBConnect dbConnect = new DBConnect();
    private VoiceRecorder recorder = new VoiceRecorder(dbConnect);
    private JLabel recordingLabel;
    private JButton playButton;
    private JLabel recordedFileLabel;
    private String currentFilePath;

    /**
     * Constructeur permettant de faire le design de l'interface
     * utilisateur
     */
    public VoiceApp() {
        setTitle("Voice Recorder");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton recordButton = new JButton("Enregistrer");
        recordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recordButton.setBackground(new Color(60, 179, 113));
        recordButton.setForeground(Color.WHITE);

        JButton stopButton = new JButton("Arrêter");
        stopButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        stopButton.setBackground(new Color(220, 20, 60));
        stopButton.setForeground(Color.WHITE);

        playButton = new JButton("Lire");
        playButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        playButton.setBackground(new Color(100, 149, 237));
        playButton.setForeground(Color.WHITE);
        playButton.setEnabled(false);

        JButton crudButton = new JButton("Gestion des utilisateurs");
        crudButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        crudButton.setBackground(new Color(255, 165, 0));
        crudButton.setForeground(Color.WHITE);
        
        JButton messageCrudButton = new JButton("Gestion des messages");
        messageCrudButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageCrudButton.setBackground(new Color(255, 69, 0));
        messageCrudButton.setForeground(Color.WHITE);

        recordingLabel = new JLabel("Enregistrement en cours...");
        recordingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recordingLabel.setForeground(Color.RED);
        recordingLabel.setVisible(false);

        recordedFileLabel = new JLabel("Aucun fichier enregistré");
        recordedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recordedFileLabel.setVisible(false);

        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFilePath = VoiceRecorder.generateFilePath();
                recorder.startRecording(currentFilePath);
                recordingLabel.setVisible(true);
                playButton.setEnabled(false);
                recordedFileLabel.setVisible(false);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Arrêt de l'enregistrement...");
                    recorder.stopRecording(currentFilePath); // Passer currentFilePath à stopRecording
                    System.out.println("Enregistrement arrêté.");
                    recordingLabel.setVisible(false);
                    playButton.setEnabled(true);
                    recordedFileLabel.setText("Fichier enregistré: " + currentFilePath + ".enc");
                    recordedFileLabel.setVisible(true);
                    String userId = "user_id_placeholder"; // Remplacez par l'ID utilisateur approprié
                    String filePath = currentFilePath + ".enc";

                    // Générer et stocker le hachage SHA-256 du fichier chiffré
                    byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                    String fileHash = HashUtils.hashFile(fileContent);

                    dbConnect.addMessage(userId, filePath, fileHash);
                    System.out.println("Message ajouté à la base de données.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'arrêt de l'enregistrement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playAudio(currentFilePath);
            }
        });

        crudButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CRUDFrame().setVisible(true);
                VoiceApp.this.setVisible(false);
            }
        });
        
        messageCrudButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MessageCRUDFrame().setVisible(true);
                VoiceApp.this.setVisible(false);
            }
        });

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGap(47)
        					.addComponent(recordButton)
        					.addGap(52)
        					.addComponent(playButton)
        					.addGap(42)
        					.addComponent(stopButton))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(127)
        					.addComponent(recordingLabel))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(127)
        					.addComponent(recordedFileLabel))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(29)
        					.addComponent(crudButton)
        					.addGap(49)
        					.addComponent(messageCrudButton)))
        			.addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(61)
        			.addComponent(recordingLabel)
        			.addGap(45)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(recordButton)
        				.addComponent(playButton)
        				.addComponent(stopButton))
        			.addGap(40)
        			.addComponent(recordedFileLabel)
        			.addGap(48)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(crudButton)
        				.addComponent(messageCrudButton))
        			.addContainerGap(71, Short.MAX_VALUE))
        );
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        getContentPane().add(panel);
    }

    /**
     * Methode qui lance l'écoute d'un message audio 
     * @param filePath
     */
    private void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            recorder.decryptFile(audioFile); // Déchiffrer le fichier avant la lecture
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            System.out.println("Playing file: " + filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
           JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier audio : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void stopRecording() {
        try {
            recorder.stopRecording(currentFilePath);
            String filePath = currentFilePath + ".enc";
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            String fileHash = HashUtils.hashFile(fileContent);

            String userId = "user_id_placeholder"; // Remplacez par l'ID utilisateur approprié
            dbConnect.addMessage(userId, filePath, fileHash);
            System.out.println("Message ajouté à la base de données avec le hachage du fichier.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'arrêt de l'enregistrement : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Methode qui invoque l'interface d'enregistrement audio
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VoiceApp().setVisible(true);
            }
        });
    }
}