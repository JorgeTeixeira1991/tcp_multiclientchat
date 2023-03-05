package TCP_MultiClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

    /*
       handle each connection separately
       server must relay all messages sent to it to all users
     */

public class TCPMultiClientServer {
    private Vector<ClientHandler> clients;
    private ArrayList<String> names;
    private ArrayList<Thread> service;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String superuser_pswd;

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.names = new ArrayList<>();
        this.service = new ArrayList<>();
        this.superuser_pswd = "cenasetal";

    }

    public static void main(String[] args) {

        try {
            TCPMultiClientServer tcpMultiClientServer = new TCPMultiClientServer();
            while (true) {
                tcpMultiClientServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        while (true) {
            this.clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(this);
            Thread client = new Thread(clientHandler);
            service.add(client);
            client.start();
            clients.add(clientHandler);
        }
    }

    public void shutdown() throws IOException {
        serverSocket.close();
    }

    public void broadcast(String s) {
        for (ClientHandler c : clients) {
            try {
                if (c.getUsername() != Thread.currentThread().getName()) {
                    c.send(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Vector<ClientHandler> getClients() {
        return clients;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getSuperuser_pswd() {
        return superuser_pswd;
    }

    class ClientHandler extends Thread {

        private TCPMultiClientServer server;
        private User user;
        private PrintStream out;
        private BufferedReader in;
        private String sent_message;
        String currentThreadName;

        public ClientHandler(TCPMultiClientServer server) throws IOException {
            this.server = server;
            this.user = new User(server);
            this.out = new PrintStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.sent_message = "default";
            out.println(" / /| | | |           / _|   | | |             /  ___| | | |_   _|  ___|_   _\\ \\   \n" +
                    " / / | |_| |__   ___  | |_ ___| | | _____      _\\ `--.| |_| | | | | |_    | |  \\ \\  \n" +
                    "< <  | __| '_ \\ / _ \\ |  _/ _ \\ | |/ _ \\ \\ /\\ / /`--. \\  _  | | | |  _|   | |   > > \n" +
                    " \\ \\ | |_| | | |  __/ | ||  __/ | | (_) \\ V  V //\\__/ / | | |_| |_| |     | |  / /  \n" +
                    "  \\_\\ \\__|_| |_|\\___| |_| \\___|_|_|\\___/ \\_/\\_/ \\____/\\_| |_/\\___/\\_|     \\_/ /_/   \n" +
                    "  ______          ______                                                  ______    \n" +
                    " |______|        |______|                                                |______|   ");
        }

        public void setUsername(String name) {
            user.setName(name);
            Thread.currentThread().setName(name);
        }

        public String getUsername() {
            return user.getName();
        }

        public void send(String s) throws IOException {
            out.println(s);
        }

        public void commandChecker(String msg) throws IOException {
            switch (msg) {
                case "/quit" -> Commands.QUIT.quit(server, this, user);
                case "/ch_name" -> Commands.CH_NAME.changeUsername(server, this, user);
                case "/kick" -> Commands.KICK.kick();
                case "/whisper" -> Commands.WHISPER.whisper(server, this, user);
                case "/super" -> Commands.LOGIN_SU.SU_login(server, this, user);
            }
        }

        public void promptForUsername() throws IOException {
            out.println("Please choose your username: ");
            String username = in.readLine();
            if (UsernameValidator.isValid(username) && !names.contains(username)) {
                setUsername(username);
                names.add(username);
                broadcast(username + " has joined the chat...");
                System.out.println(names);
            } else if (names.contains(username)) {
                out.println("Username already taken!");
                promptForUsername();
            } else {
                promptForUsername();
            }
        }

        public void setSent_message(String sent_message) {
            this.sent_message = sent_message;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        public BufferedReader getIn() {
            return in;
        }

        public PrintStream getOut() {
            return out;
        }

        @Override
        public void run() {
            try {
                promptForUsername();
                this.currentThreadName = Thread.currentThread().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    sent_message = in.readLine();
                    System.out.println(sent_message);
                    commandChecker(sent_message);
                    if (!sent_message.equals(user.getCommand())) {
                        broadcast("\n" + user.getName() + ": " + sent_message);
                    }
                }
            } catch (IOException e) {
                names.remove(this.getUsername());
                System.out.println(names);
                clients.remove(this);
            }
        }
    }
}