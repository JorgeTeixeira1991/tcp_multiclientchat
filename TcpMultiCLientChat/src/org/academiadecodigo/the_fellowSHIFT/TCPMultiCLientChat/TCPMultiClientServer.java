package org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat.Commands.CH_NAME;

    /*
       handle each connection separately
       server must relay all messages sent to it to all users
     */

public class TCPMultiClientServer {
    private Vector<ClientHandler> clients;
    private ExecutorService service;
    private ServerSocket serverSocket;
    private Commands commands;

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.service = Executors.newCachedThreadPool();

    }

    public static void main(String[] args) throws IOException {

        TCPMultiClientServer tcpMultiClientServer = new TCPMultiClientServer();

        while (true) {
            try {
                tcpMultiClientServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void broadcast(String s) {
        for (ClientHandler c : clients) {
            try {
                if (c.getSent_message() != s) {
                    c.send(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkForCommands(String sent_message) {
        switch (commands.getCommand()) {
            case "/quit":
                CommandsImpl
        }
    }


    class ClientHandler implements Runnable {

        private TCPMultiClientServer server;
        private User user;
        private Socket clientSocket;
        private PrintStream out;
        private BufferedReader in;
        private String sent_message;


        public ClientHandler(Socket clientSocket, TCPMultiClientServer server) throws IOException {

            this.clientSocket = clientSocket;
            this.server = server;
            this.out = new PrintStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        }

        public void quit() throws IOException {
            in.close();
            clientSocket.out.close();
            clientSocket.close();
        }

        public void setUsername(String name) {
            user.setName(name);
        }

        public void send(String s) throws IOException {
            out.println(s);
        }

        public String getSent_message() {
            return sent_message;
        }

        @Override
        public void run() {
            try {
                out.println("Please choose your username: ");
                setUsername(in.readLine());
                while (true) {

                    sent_message = in.readLine();
                    broadcast(user.getName() + " : " + sent_message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
