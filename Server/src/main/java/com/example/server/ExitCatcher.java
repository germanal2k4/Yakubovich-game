package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * class that cathhes and ban disconnected users
 */
public class ExitCatcher extends Thread {
    public ExitCatcher(ArrayList<Socket> sockets, GameSession gameSession, int tn) {
        this.sockets = sockets;
        this.gameSession = gameSession;
        this.tn = tn;
    }

    /**
     * user remover if program catches a disconnected client outside of exit catcher user can be banned externally
     * @param socket
     */
    public synchronized void remove(Socket socket) {

        sockets.remove(socket);
    }
    public synchronized boolean notNullAmountOfPlayers(){
        return !sockets.isEmpty();
    }
    /**
     * overidden method that check, that all users are in game
     */
    @Override
    public void run() {
        LocalTime currentRun = LocalTime.now();
        while (!Thread.currentThread().isInterrupted()) {
            if (LocalTime.now().isAfter(currentRun.plusMinutes(tn)) || tn != 0) {
                synchronized (this) {
                    String[] values = gameSession.currentCondition();
                    for (int i = 0; i < sockets.size(); i++) {
                        if (!Alive(sockets.get(i))) {
                            try {
                                gameSession.removeSocket(i);
                                sockets.remove(sockets.get(i));
                                i--;
                            } catch (IOException e) {
                                System.out.println(e.getMessage() + " unclosed Socket");
                            }
                        } else {
                            try {
                                if(!notNullAmountOfPlayers()) return;
                                writeToEveryBody(sockets.get(i), "Current conditions");
                                DataOutputStream out = new DataOutputStream(sockets.get(i).getOutputStream());
                                out.writeInt(values.length);
                                for(String s: values){
                                    writeToEveryBody(sockets.get(i), s);
                                }
                            } catch (IOException e) {
                                System.out.println("something went wrong" + e.getMessage());
                                return;
                            }
                        }
                    }
                }
                currentRun = LocalTime.now();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * private method for writing a message to everubody in game
     * @param socket
     * @param s
     * @throws IOException
     */
    private synchronized void writeToEveryBody(Socket socket, String s) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(s);
    }

    /**
     * adding a user into the session
     * @param socket
     */
    public synchronized void add(Socket socket) {
        sockets.add(socket);
    }

    public synchronized boolean Alive(Socket socket) {
        try{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Alive");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String s = in.readUTF();
            if(!s.isEmpty()) return true;
        }catch (IOException i){
            return false;
        }
        return false;
    }

    private final ArrayList<Socket> sockets;
    private final GameSession gameSession;
    private final int tn;
}

