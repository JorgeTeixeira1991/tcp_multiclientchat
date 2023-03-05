package TCP_MultiClientServer;

import java.io.IOException;
import java.util.Objects;

public enum Commands {
    QUIT("/quit"),
    CH_NAME("/ch_name"),
    WHISPER("/whisper"),
    KICK("/kick"),
    LOGIN_SU("/super"),
    UNBAN("/unban");

    private String description;

    Commands(String description) {
        this.description = description;
    }

    public void quit(TCPMultiClientServer server, TCPMultiClientServer.ClientHandler clientHandler, User user) throws IOException {

        server.getNames().remove(clientHandler.getUsername());
        user.setCommand("/quit");
        server.broadcast(user.getName() + " has left the chat...");
        clientHandler.getIn().close();
        clientHandler.getOut().close();
        clientHandler.getClientSocket().close();
    }

    public void changeUsername(TCPMultiClientServer server, TCPMultiClientServer.ClientHandler clientHandler, User user) throws IOException {
        user.setCommand("/ch_name");
        String old_username = user.getName();
        clientHandler.getOut().println("Please choose your new username: ");
        String new_username = clientHandler.getIn().readLine();
        if (UsernameValidator.isValid(new_username) && !server.getNames().contains(new_username)) {
            user.setName(new_username);
            server.getNames().remove(old_username);
            server.broadcast(old_username + " has changed his username to: " + new_username);
        } else if (server.getNames().contains(new_username)) {
            clientHandler.getOut().println("Username already taken!");
            changeUsername(server, clientHandler, user);
        } else {
            changeUsername(server, clientHandler, user);
        }
    }

    public void whisper(TCPMultiClientServer server, TCPMultiClientServer.ClientHandler clientHandler, User user) throws IOException {

        user.setCommand("/whisper");
        clientHandler.getOut().println("You have now entered whisper mode...");
        clientHandler.getOut().println("Choose the person to whisper: ");
        String whisper_name = clientHandler.getIn().readLine();
        for (TCPMultiClientServer.ClientHandler name : server.getClients()) {
            if (name.getUsername().equals(whisper_name) && !clientHandler.getUsername().equals(whisper_name)) {
                clientHandler.getOut().println("You are now whispering " + whisper_name);
                String sent_message;
                while ((sent_message = clientHandler.getIn().readLine()) != null) {
                    if (sent_message.equals(Commands.QUIT.description)){
                        break;
                    }
                    name.send(clientHandler.getUsername() + " (whisper): " + sent_message);
                }
            }
        }
    }


    public void SU_login(TCPMultiClientServer server, TCPMultiClientServer.ClientHandler clientHandler, User user) throws IOException {
        user.setCommand("/super");
        int try_count = user.getSU_login_try_count();
        clientHandler.getOut().println("Super User Password: ");
        String pswd = clientHandler.getIn().readLine();
        if (try_count == 3) {

            clientHandler.getOut().println("You have been permanently banned from trying to login as super user!");
            server.broadcast(clientHandler.getUsername() + "has been permanently banned from trying to login as super user!");
            user.setBanned(true);

        } else if (pswd != server.getSuperuser_pswd()) {

            user.setSU_login_try_count(try_count + 1);
            clientHandler.getOut().println("You have tried to login unsuccessfully " + user.getSU_login_try_count() + " times...");
            SU_login(server, clientHandler, user);

        } else if (pswd == server.getSuperuser_pswd()) {
            clientHandler.getOut().println("You have successfully logged in as Super User!");
            user.setAdmin(true);
        }
    }

    public void kick() {
    }


    public String getDescription() {
        return description;
    }
}