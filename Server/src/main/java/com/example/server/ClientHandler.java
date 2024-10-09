package com.example.server;

import java.net.Socket;

/**
 * proxy class for clients
 */
public class ClientHandler {
    public ClientHandler(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    /**
     * getter for socket
     * @return
     */
    public Socket getSocket(){
        return socket;
    }
    /**
     * getter for name
     * @return
     */
    public String getName(){return name;}

    private final Socket socket;
    private final String name;
}
