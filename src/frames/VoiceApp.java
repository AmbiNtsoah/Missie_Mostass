package frames;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class VoiceApp extends JFrame {
    private VoiceRecorder recorder = new VoiceRecorder();
    private DBConnect dbConnect = new DBConnect();
    private JLabel recordingLabel;
    private JButton playButton;
    private JLabel recordedFileLabel;
    private String currentFilePath;

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
                recorder.stopRecording();
                recordingLabel.setVisible(false);
                playButton.setEnabled(true);
                recordedFileLabel.setText("Fichier enregistré:" + currentFilePath);
                recordedFileLabel.setVisible(true);
                String userId = "user_id_placeholder"; // Remplacez par l'ID utilisateur approprié
                String filePath = currentFilePath;
                dbConnect.addMessage(userId, filePath);
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
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(recordButton)
                    .addComponent(stopButton)
                    .addComponent(playButton)
                    .addComponent(crudButton)
                    .addComponent(messageCrudButton)
                    .addComponent(recordingLabel)
                    .addComponent(recordedFileLabel))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(recordButton)
                .addComponent(stopButton)
                .addComponent(playButton)
                .addComponent(crudButton)
                .addComponent(messageCrudButton)
                .addComponent(recordingLabel)
                .addComponent(recordedFileLabel)
        );

        add(panel);
    }

    private void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            System.out.println("Playing file: " + filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VoiceApp().setVisible(true);
            }
        });
    }
}