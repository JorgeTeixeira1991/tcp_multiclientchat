package TCP_MultiClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
    /*
       handle each connection separately
       server must relay all messages sent to it to all users
     */

public class TCPMultiClientServer {
    private Vector<ClientHandler> clients;
    private ExecutorService service;
    private ServerSocket serverSocket;

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.service = Executors.newCachedThreadPool();

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
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
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

    class ClientHandler implements Runnable {

        private TCPMultiClientServer server;
        private User user;
        private Socket clientSocket;
        private PrintStream out;
        private BufferedReader in;
        private String sent_message;
        private UsernameValidator usernameValidator;

        public ClientHandler(Socket clientSocket, TCPMultiClientServer server) throws IOException {

            this.clientSocket = clientSocket;
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

        public void checkForCommands(Commands commands) throws IOException {
            CommandsImpl cmds = new CommandsImpl(user, server, this);
            switch (commands) {
                case CH_NAME-> cmds.changeUsername();
                case KICK -> cmds.kick();
                case QUIT -> cmds.quit();
                case WHISPER -> cmds.whisper();
                case LOGIN_SU -> cmds.SU_login();
            }
        }

        public void promptForUsername() throws IOException {
            out.println("Please choose your username: ");
            String username = in.readLine();
            if (UsernameValidator.isValid(username)) {
                setUsername(username);
                broadcast(username + " has joined the chat...");
            }else {
                promptForUsername();
            }
        }

        public void commandChecker(String msg) throws IOException {
            if (msg.startsWith("/quit")){
                checkForCommands(Commands.QUIT);
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
                while (true) {

                    sent_message = in.readLine();
                    commandChecker(sent_message);
                    if (sent_message == null) break;

                    broadcast(user.getName() + " : " + sent_message);

                }
            } catch (IOException e) {
                //ignore
            }
        }
    }
}
