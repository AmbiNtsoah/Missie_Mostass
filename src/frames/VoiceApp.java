package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class VoiceApp extends JFrame {
    private DBConnect dbConnect = new DBConnect();

    public VoiceApp() {
        setTitle("Voice Recorder");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton recordButton = new JButton("Enregistrer");
        JButton playButton = new JButton("Lire");
        JButton crudButton = new JButton("Gestion des utilisateurs");

        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour enregistrer un fichier audio sans utiliser startRecording
                System.out.println("Recording started...");
                // Ajoutez ici la logique pour enregistrer un fichier audio
                System.out.println("Recording finished.");
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File inputFile = new File("recorded_audio.wav");
                    System.out.println("Playing file: " + inputFile.getAbsolutePath());
                    // Ajoutez la logique pour lire le fichier ici
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        crudButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CRUDFrame().setVisible(true);
                VoiceApp.this.setVisible(false);
            }
        });

        JPanel panel = new JPanel();
        panel.add(recordButton);
        panel.add(playButton);
        panel.add(crudButton);

        add(panel);
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