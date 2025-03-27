package frames;

public class Message {
    private int id;
    private String userId;
    private String filePath;
    private String fileHash;

    public Message(int id, String userId, String filePath, String filehash) {
        this.id = id;
        this.userId = userId;
        this.filePath = filePath;
        this.fileHash = fileHash;
    }

    // Getters et setters
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
    
    public String getFileHash() {
        return fileHash;
    }
    
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    
}
