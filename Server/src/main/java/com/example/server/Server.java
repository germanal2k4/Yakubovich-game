package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import java.util.concurrent.TimeUnit;

/**
 * class that is respondent for Server and sending the clients information about the current game
 */
public class Server extends Thread {
    /**
     * construct for Server with all parametrs that was applied in a task
     * @param port
     * @param m
     * @param tp
     * @param ts
     * @param tb
     * @param tn
     * @param wordList
     */
    public Server(int port, int m, int tp, int ts, int tb, int tn, String wordList) {
        this.port = port;
        this.m = m;
        this.tb = tb;
        this.tp = tp;
        this.ts = ts;
        this.tn = tn;
        this.wordList = wordList;
    }

    /**
     * finishing the server after it stopped
     * @throws IOException
     */
    public synchronized void killGame() throws IOException {
        for (GameSession s : sessions) {
            s.killSession();
        }
        serverSocket.close();
    }

    /**
     * setter for the word length cause it can be changed during the runtime
     * @param i
     */
    public void setN(int i) {
        n = i;
        word = "";
    }

    /**
     * getter for condition of server
     * @return
     */
    public boolean isStarted(){
        return startFlag;
    }
    /**
     * overrided method run that launches server
     */
    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(port)) {
            serverSocket = ss;
            System.out.println("Server started number of port: " + port);
            startFlag = true;
            int sid = 0;

            while (true) {
                GameSession gameSession = new GameSession(sid++, tb, ts, tn, wordList);
                sessions.add(gameSession);

                LocalTime end = LocalTime.now().plusSeconds(tp);

                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                if (tp != 0) {
                    executorService.schedule(() -> Thread.currentThread().interrupt(), tp, TimeUnit.SECONDS);
                }

                while ((tp == 0 || LocalTime.now().isBefore(end)) && gameSession.getNumberOfUsers() < m && !flag) {
                    System.out.println(Duration.between(LocalTime.now(), end).getNano());
                    try {
                        serverSocket.setSoTimeout(10000);
                        Socket socket = serverSocket.accept();
                        gameSession.addClient(socket);
                    } catch (IOException e) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                }

                if (gameSession.getNumberOfUsers() == 0) {
                    sessions.removeLast();
                    gameSession.killSession();
                } else {
                    synchronized (this) {
                        if (!word.isEmpty()) {
                            gameSession.setLenWord(n);
                        } else if (n >= 5) {
                            gameSession.setWord(word);
                        }
                    }

                    gameSession.start();
                }

                executorService.close();
            }
        } catch (IOException e) {
            System.out.println("Server is not reached");
        }
    }

    public void setServerSocket(ServerSocket s){
        serverSocket = s;
    }

    /**
     * setter for the flag
     * @param b
     */
    public void setFlag(boolean b){
        this.flag = true;
    }
    /**
     * setter for word cause it can be dinamically changed for the next session
     * @param text
     */
    public void setWord(String text) {
        n = 0;
        word = text;
    }

    private final int port;

    private final int m;

    private final int tb;

    private final int tp;

    private final int ts;

    private final int tn;

    protected int n = 0;

    protected String word = "";
    private boolean startFlag = false;
    private final String wordList;
    private boolean flag = false;
    private ServerSocket serverSocket;
    protected final List<GameSession> sessions = new ArrayList<>();
}

