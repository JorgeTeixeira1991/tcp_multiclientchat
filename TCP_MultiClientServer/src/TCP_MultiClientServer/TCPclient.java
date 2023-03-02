package TCP_MultiClientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
   separate sending messages from receiving messages using multi-threading (create two synchronized (methods? classes?))
 */
public class TCPclient {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TCPclient() throws IOException {
        this.clientSocket = new Socket("127.0.0.1", 51243);
    }

    public void send(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String msg = reader.readLine();
            System.out.println("Message: ");
            out.println(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
