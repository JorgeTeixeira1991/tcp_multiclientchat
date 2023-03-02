package TCP_MultiClientServer;

public class User {
    private String name;
    private int id;
    private boolean isAdmin;
    private TCPMultiClientServer server;

    public User(TCPMultiClientServer server) {
        this.server = server;
        this.isAdmin = false;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setId(int id) {
        this.id = id;
    }



}
