package frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Classe qui affiche l'interface graphique 
 * où l'utilisateur peut faire des actions sur les messages enregistrés
 */
public class MessageCRUDFrame extends JFrame {
    private static final long serialUID = 1L;
    private JPanel contentPane;
    private JTextField messageIdField;
    private JTextField filePathField;
    private JTable table;
    private DBConnect dbConnect = new DBConnect();

    /**
     * Constructeur de la classe qui crée l'interface graphique
     */
    public MessageCRUDFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel titleLabel = new JLabel("Gestion des Messages Vocaux");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel messageIdLabel = new JLabel("ID du Message:");
        messageIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        messageIdField = new JTextField();
        messageIdField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageIdField.setColumns(10);

        JLabel filePathLabel = new JLabel("Chemin du fichier:");
        filePathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        filePathField = new JTextField();
        filePathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filePathField.setColumns(10);

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMessage();
            }
        });

        JButton playButton = new JButton("Lire");
        playButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playMessage();
            }
        });

        JButton backButton = new JButton("Retour");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Ferme la fenêtre actuelle
                new VoiceApp().setVisible(true); // Ouvre l'interface précédente
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Chemin du fichier"}
        ));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        scrollPane.setViewportView(table);

        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(200)
                            .addComponent(titleLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(30)
                            .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(messageIdLabel)
                                .addComponent(filePathLabel))
                            .addGap(18)
                            .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(messageIdField)
                                .addComponent(filePathField, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                            .addGap(18)
                            .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(playButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(30)
                            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(30)
                            .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(30)
                    .addComponent(titleLabel)
                    .addGap(30)
                    .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(messageIdLabel)
                        .addComponent(messageIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteButton))
                    .addGap(18)
                    .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(filePathLabel)
                        .addComponent(filePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(playButton))
                    .addGap(30)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(backButton)
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);

        // Charger les messages dans le tableau
        loadMessages();
    }

    /**
     * Metohde de classe qui va supprimer un message
     * qui était enregistré dans l'application
     */
    private void deleteMessage() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int messageId = (int) table.getValueAt(selectedRow, 0);
            dbConnect.deleteMessage(messageId);
            JOptionPane.showMessageDialog(this, "Message supprimé avec succès !");
            loadMessages();
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un message à supprimer.");
        }
    }

    /**
     * Methode qui va lire le message enregistré 
     * étant séléctioné
     */
    private void playMessage() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String filePath = (String) table.getValueAt(selectedRow, 1);
            playAudio(filePath);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un message à lire.");
        }
    }
    
    /**
     * Methode qui lance l'écoute d'un message audio 
     * @param filePath
     */
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

    /**
     * Methode qui charge les messages enregistés dans l'application
     * dans un tableau
     */
    private void loadMessages() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows
        List<Message> messages = dbConnect.getMessages("user_id_placeholder"); // Remplacez par l'ID utilisateur approprié
        for (Message message : messages) {
            model.addRow(new Object[]{message.getId(), message.getFilePath()});
        }
    }
}