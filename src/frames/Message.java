package frames;

/**
 * Classe utilie pour la manipulation des messages 
 * audio dans l'application
 */
public class Message {
    private int id;
    private String userId;
    private String filePath;

    /**
     * Constructeur qui permet de créer et manipuler les 
     * messages
     * @param id du message
     * @param userId de l'utilisateur ayant fait l'enregistrement
     * @param filePath le chemin du message
     */
    public Message(int id, String userId, String filePath) {
        this.id = id;
        this.userId = userId;
        this.filePath = filePath;
    }

    /**
     * Getters et setters des attributs privées
     * @return
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}