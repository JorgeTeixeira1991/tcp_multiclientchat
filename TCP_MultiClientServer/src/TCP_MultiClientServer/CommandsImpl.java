package TCP_MultiClientServer;

import java.io.IOException;

public class CommandsImpl {
    private User user;
    private TCPMultiClientServer server;
    private TCPMultiClientServer.ClientHandler clientHandler;

    public CommandsImpl(User user, TCPMultiClientServer server, TCPMultiClientServer.ClientHandler clientHandler) {
        this.user = user;
        this.server = server;
        this.clientHandler = clientHandler;
    }
    public void quit() throws IOException {
        server.broadcast(user.getName() + " has left the chat...");
        clientHandler.getIn().close();
        clientHandler.getOut().close();
        clientHandler.getClientSocket().close();
    }
    public void changeUsername(){}
    public void kick(){}
    public void whisper(){}
    public void SU_login(){}

}