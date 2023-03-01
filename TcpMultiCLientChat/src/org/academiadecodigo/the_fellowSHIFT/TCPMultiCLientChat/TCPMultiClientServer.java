package org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiClientServer {

    private ServerSocket serverSocket;
    private int port;

    public TCPMultiClientServer() throws IOException {
        this.port = 51243;
        this.serverSocket = new ServerSocket(port);
    }

    public void listen() {
        Socket clientSocket = new Socket();
        try {
            clientSocket = serverSocket.accept();
            


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
