package TCP_MultiClientServer;

import java.io.*;
import java.net.Socket;

/*
   separate sending messages from receiving messages using multi-threading (create two synchronized (methods? classes?))
 */
public class TCPclient {

    private Socket clientSocket;
    private PrintStream out;
    private BufferedReader in;

    public TCPclient() throws IOException {
        this.clientSocket = new Socket("127.0.0.1", 51243);
        this.out = new PrintStream(clientSocket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void send(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String msg = reader.readLine();
            out.println(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
