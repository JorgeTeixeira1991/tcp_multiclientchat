package TCP_MultiClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
    /*
       handle each connection separately
       server must relay all messages sent to it to all users
     */

public class TCPMultiClientServer {
    private Vector<ClientHandler> clients;
    private ExecutorService service;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String superuser_pswd;

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.service = Executors.newCachedThreadPool();
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
            clients.add(clientHandler);
            service.submit(clientHandler);
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

    public String getSuperuser_pswd() {
        return superuser_pswd;
    }

    class ClientHandler implements Runnable {

        private TCPMultiClientServer server;
        private Commands commands;
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
            if (UsernameValidator.isValid(username)) {
                setUsername(username);
                broadcast(username + " has joined the chat...");
            } else {
                promptForUsername();
            }
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
                while (true) {
                    sent_message = in.readLine();
                    commandChecker(sent_message);
                    //if (sent_message == null) break;
                    if (sent_message != user.getCommand()) {
                        broadcast(user.getName() + " : " + sent_message);
                    }
                }
            } catch (IOException e) {
                broadcast( currentThreadName + "has unexpectedly disconnected!");
            }
        }
    }
}
