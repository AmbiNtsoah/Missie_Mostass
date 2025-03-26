package frames;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MessageCRUDFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField userIdField;
    private JTextField filePathField;
    private JTable table;
    private DBConnect dbConnect = new DBConnect();

    public MessageCRUDFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel titleLabel = new JLabel("Gestion des Messages Vocaux");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel userIdLabel = new JLabel("ID Utilisateur:");
        userIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        userIdField = new JTextField();
        userIdField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userIdField.setColumns(10);

        JLabel filePathLabel = new JLabel("Chemin du fichier:");
        filePathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        filePathField = new JTextField();
        filePathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filePathField.setColumns(10);

        JButton addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMessage();
            }
        });

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMessage();
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "ID Utilisateur", "Chemin du fichier"}
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
                                .addComponent(userIdLabel)
                                .addComponent(filePathLabel))
                            .addGap(18)
                            .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(userIdField)
                                .addComponent(filePathField, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                            .addGap(18)
                            .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(30)
                            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGap(30)
                    .addComponent(titleLabel)
                    .addGap(30)
                    .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(userIdLabel)
                        .addComponent(userIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(addButton))
                    .addGap(18)
                    .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(filePathLabel)
                        .addComponent(filePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteButton))
                    .addGap(30)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(30, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);

        // Charger les messages dans le tableau
        loadMessages();
    }

    private void addMessage() {
        String userId = userIdField.getText();
        String filePath = filePathField.getText();
        dbConnect.addMessage(userId, filePath);
        JOptionPane.showMessageDialog(this, "Message ajouté avec succès !");
        loadMessages();
    }

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

    private void loadMessages() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows
        List<Message> messages = dbConnect.getMessages("user_id_placeholder"); // Remplacez par l'ID utilisateur approprié
        for (Message message : messages) {
            model.addRow(new Object[]{message.getId(), message.getUserId(), message.getFilePath()});
        }
    }
}