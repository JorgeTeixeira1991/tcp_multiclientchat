package TCP_MultiClientServer;

public class User {
    private String name;
    private int id;
    private int SU_login_try_count;
    private boolean isAdmin, banned;
    private String command;


    public User(TCPMultiClientServer server) {
        this.isAdmin = false;
        this.banned = false;
        this.SU_login_try_count = 0;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getCommand() {
        return command;
    }
    public int getId() {
        return id;
    }
    public int getSU_login_try_count() {
        return SU_login_try_count;
    }
    public void setSU_login_try_count(int SU_login_try_count) {
        this.SU_login_try_count = SU_login_try_count;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setId(int id) {
        this.id = id;
    }



}
