package org.academiadecodigo.the_fellowSHIFT.TCPMultiCLientChat;

public class CommandsImpl {

    private User user;
    private TCPMultiClientServer server;

    public CommandsImpl(User user, TCPMultiClientServer server) {
        this.user = user;
        this.server = server;
    }

    public static void quit(){}
    public static void changeUsername(){}
    public static void kick(){}
    public static void whisper(){}
    public static void SU_login(){}




}