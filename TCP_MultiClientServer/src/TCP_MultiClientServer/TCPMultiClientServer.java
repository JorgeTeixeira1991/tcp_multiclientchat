package TCP_MultiClientServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


    /*
       handle each connection separately
       server must relay all messages sent to it to all users
     */

public class TCPMultiClientServer {
    private Vector<ClientHandler> clients;
    private ArrayList<String> names;
    private CopyOnWriteArrayList<Thread> service;
    private ServerSocket serverSocket;
    private ClientHandler clientHandler;
    private Socket clientSocket;
    private String superuser_pswd;

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.names = new ArrayList<>();
        this.service = new CopyOnWriteArrayList<>();
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

    public CopyOnWriteArrayList<Thread> getService() {
        return service;
    }

    public void start() throws IOException {
        while (true) {
            checkClients(service);
            clientSocket = serverSocket.accept();
            this.clientHandler = new ClientHandler(this);
            service.add(clientHandler.client);
            clientHandler.client.start();
            clients.add(clientHandler);
        }
    }

    public synchronized void removeClient() {
        clients.remove(clientHandler);
        service.remove(clientHandler);
        broadcast( clientHandler.getUsername() + " has left the chat.");
        names.remove(clientHandler.getUsername());
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
    private synchronized void checkClients(CopyOnWriteArrayList<Thread> threads) {
        for (Thread client : threads) {
            if (!client.isAlive()) {
                removeClient();
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

    class ClientHandler implements Runnable {

        private static TCPMultiClientServer server;
        private Thread client;
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
            this.client = new Thread(this);
            this.sent_message = "default";
            out.println("    __       __    __              ___        __   __                ____   __ __   ____   ____ ______     __  ");
            out.println("   / /      / /_  / /  ___        / _/ ___   / /  / / ___  _    __  / __/  / // /  /  _/  / __//_  __/     \\ \\ ");
            out.println("  < <      / __/ / _ \\/ -_)      / _/ / -_) / /  / / / _ \\| |/|/ / _\\ \\   / _  /  _/ /   / _/   / /         > >");
            out.println("   \\_\\ ____\\__/ /_//_/\\__/  ____/_/   \\__/ /_/  /_/  \\___/|__,__/ /___/  /_//_/  /___/  /_/    /_/     ____/_/ ");
            out.println("      /___/                /___/                                                                      /___/    ");
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
                    if ((sent_message = in.readLine()) == null) {
                        break;
                    }
                    System.out.println(sent_message);
                    commandChecker(sent_message);
                    if (!sent_message.equals(user.getCommand())) {
                        broadcast("\n" + user.getName() + ": " + sent_message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}