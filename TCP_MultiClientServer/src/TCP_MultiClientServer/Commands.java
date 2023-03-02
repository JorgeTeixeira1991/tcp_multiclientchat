package TCP_MultiClientServer;

import java.lang.reflect.Method;

public enum Commands {
    QUIT("/quit",),
    CH_NAME("/ch_name", ),
    WHISPER("/whisper"),
    KICK("/kick"),
    LOGIN_SU("/super");
    private String command;
    Commands(String command, Method method) {
        this.command = command;

    }

    public String getCommand() {
        return command;
    }




}
