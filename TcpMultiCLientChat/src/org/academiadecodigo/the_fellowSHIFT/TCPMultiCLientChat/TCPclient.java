package org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat;

import java.io.IOException;
import java.net.Socket;

/*
   separate sending messages from receiving messages using multi-threading (create two synchronized (methods? classes?))




 */
public class TCPclient {

    private Socket clientSocket;

    public TCPclient() throws IOException {
        this.clientSocket = new Socket("127.0.0.1", 51243);
    }




}
