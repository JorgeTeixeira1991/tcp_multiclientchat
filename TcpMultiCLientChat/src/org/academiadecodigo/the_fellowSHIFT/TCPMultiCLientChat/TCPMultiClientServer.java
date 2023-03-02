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

    private ServerSocket serverSocket;
    private Vector<ClientHandler> clients;
    private ExecutorService service;
    private int port;

    public TCPMultiClientServer() throws IOException {

        this.port = 51243;
        this.serverSocket = new ServerSocket(port);
        this.clients = new Vector<>();
        this.service = Executors.newCachedThreadPool();

    }


    public void listen() {

        while (serverSocket.isBound()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                service.submit(clientHandler);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void broadcast(String msg) {

        for (ClientHandler c : clients) {
            c.out.println(msg);
        }

    }


    class ClientHandler implements Runnable {

        private PrintWriter out;
        private BufferedReader in;
        private String sent_message;
        private String received_message;


        public ClientHandler(Socket clientSocket) throws IOException {
            this.out = new PrintWriter(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public String receive() {
            return received_message;
        }



        public void send(String msg) {
            out.println(msg);
            sent_message = msg;
        }

        @Override
        public void run() {
            System.out.println("Message: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String message_to_send = reader.readLine();
                send(message_to_send);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public static void main(String[] args) {
        try {
            TCPMultiClientServer tcpMultiClientServer = new TCPMultiClientServer();
            tcpMultiClientServer.listen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
