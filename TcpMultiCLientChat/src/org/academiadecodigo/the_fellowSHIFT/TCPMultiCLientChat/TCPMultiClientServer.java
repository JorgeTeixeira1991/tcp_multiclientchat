package org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

    public TCPMultiClientServer() throws IOException {

        this.serverSocket = new ServerSocket(1234);
        this.clients = new Vector<>();
        this.service = Executors.newCachedThreadPool();

    }

    public void listen() throws IOException {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clients.add(clientHandler);
        service.submit(clientHandler);
    }

    public void broadcast(String msg, Vector<ClientHandler> clients) {

        for (ClientHandler c : clients) {
            c.send(msg);
        }
    }

    class ClientHandler implements Runnable {

        private PrintStream out;
        private BufferedReader in;
        private String sent_message;
        private String received_message;


        public ClientHandler(Socket clientSocket) throws IOException {
            this.out = new PrintStream(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public void send(String s) {
            out.println(s);
        }

        @Override
        public void run() {
            while (true) {
                out.println("Message: ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String message_to_send = reader.readLine();
                    send(message_to_send);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {

        TCPMultiClientServer tcpMultiClientServer = new TCPMultiClientServer();

        while (true) {
            try {
                tcpMultiClientServer.listen();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
