package frames;

public class Keys {
    private int id;
    private String key;

    public Keys(int id, String key) {
        this.id = id;
        this.key = key;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}