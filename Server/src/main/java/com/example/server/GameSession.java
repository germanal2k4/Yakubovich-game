package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * class that is responsible for sending information about session to users
 */
public class GameSession extends Thread {
    public GameSession(int id, int tb, int ts, int tn, String wordList) {
        this.id = id;
        this.tb = tb;
        this.ts = ts;
        this.clients = new ArrayList<>();
        this.logicSender = new LogicSender(wordList);
        this.exitCatcher = new ExitCatcher(new ArrayList<>(), this, tn);
        exitCatcher.start();
    }

    /**
     * getter for sockets by id
     * @param id
     * @return
     */
    public Socket getSocketInQueue(int id) {
        synchronized (exitCatcher){
            return clients.get(id).getSocket();
        }
    }

    /**
     * removing a socket bu id
     * @param ind
     * @throws IOException
     */
    public void removeSocket(int ind) throws IOException {
        clients.get(ind).getSocket().close();
        clients.remove(ind);
        logicSender.deleteUser(ind);
    }

    /**
     * stopping a session
     */
    public void killSession() {
        synchronized (exitCatcher) {
            for (ClientHandler client : clients) {
                try {
                    client.getSocket().close();
                } catch (IOException e) {
                    System.out.println("unclosed socket");
                }
            }
            stop = true;
        }
    }

    /**
     * adding a client into the session
     * @param s
     * @throws IOException
     */
    public void addClient(Socket s) throws IOException {
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataOutputStream out = new DataOutputStream(s.getOutputStream());

        String input = in.readUTF();
        System.out.println(input);
        synchronized (exitCatcher) {
            ClientHandler clientHandler = new ClientHandler(s, input);
            userCounter = userCounter + 1;
            clients.add(clientHandler);
            logicSender.addUser();
            exitCatcher.add(s);
            try {
                out.writeUTF(String.format("session number=%d", id));
            } catch (IOException e) {
                exitCatcher.remove(s);
                logicSender.deleteUser(clients.size() - 1);
                clients.get(clients.size() - 1).getSocket().close();
                clients.remove(clients.size() - 1);
            }
        }
    }

    /**
     * getter of count of users
     * @return
     */
    public int getNumberOfUsers() {
        return clients.size();
    }

    /**
     * overriden method that runs a session
     */
    @Override
    public void run() {
        logicSender.go();

        try {
            Thread.sleep(tb * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Error occured" + e.getMessage());
        }

        synchronized (exitCatcher) {
            for (ClientHandler client : clients) {
                try {
                    DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());
                    out.writeUTF("ID");
                    out.writeInt(id);
                    out.writeInt(logicSender.getN());
                    out.writeInt(ts);
                } catch (IOException e) {
                    System.out.println(client.getName() + " cannot be connected");
                }
            }
        }

        LocalTime start = LocalTime.now();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        if (ts != 0) {
            executorService.schedule(() -> Thread.currentThread().interrupt(), ts, TimeUnit.SECONDS);
        }

        while (!stop && (ts == 0 || LocalTime.now().isBefore(start.plusSeconds(ts))) && exitCatcher.notNullAmountOfPlayers()) {
            synchronized (exitCatcher) {
            int player = logicSender.getNextUserTurn();
                try {
                    Socket current = getSocketInQueue(player);
                    DataOutputStream out = new DataOutputStream(current.getOutputStream());
                    DataInputStream in = new DataInputStream(current.getInputStream());
                    out.writeUTF("Do your guess");

                    String input = in.readUTF();
                    int place = in.readInt();
                    System.out.println(place + " " + input);
                    int k = logicSender.guess(input, place, player);
                    out.writeInt(k);
                } catch (IOException e) {
                    continue;
                }
            }

            if(!exitCatcher.notNullAmountOfPlayers()){
                stop = true;
            }

            if (logicSender.isFinished()) {
                stop = true;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        if (!logicSender.isFinished()) {
            synchronized (exitCatcher){
                for (ClientHandler client : clients) {
                    try {
                        DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());
                        out.writeUTF("Time is up");
                    } catch (IOException e) {
                        System.out.println(client.getName() + " banned");
                    }
                }
            }
        } else {
            int winner = logicSender.winnerState();
            System.out.println(winner);
            synchronized (exitCatcher) {
                for (int i = 0; i < clients.size(); i++) {
                    try {
                        DataOutputStream out = new DataOutputStream(clients.get(i).getSocket().getOutputStream());
                        out.writeUTF("Game over");
                        if (winner == i) {
                            out.writeUTF("Winner winner chicken dinner");
                        } else {
                            out.writeUTF("Loser!");
                        }
                    } catch (IOException e) {
                        System.out.println(clients.get(i).getName() + " banned");
                    }
                }
            }
        }

        synchronized (exitCatcher){
            for (ClientHandler client : clients) {
                try {
                    client.getSocket().close();
                } catch (IOException e) {
                    System.out.println("unclosed socket");
                }
            }
        }
        executorService.close();
        exitCatcher.interrupt();
    }

    /**
     * setter for guessing word
     * @param word
     */
    public void setWord(String word) {
        logicSender.setGuessingWord(word);
    }

    /**
     * setter for length og the word
     * @param n
     */
    public void setLenWord(int n) {
        logicSender.setWordLength(n);
    }

    private final int id;

    private final LogicSender logicSender;

    private final ArrayList<ClientHandler> clients;

    private volatile Boolean stop = false;

    private final int tb;

    private final int ts;

    private final ExitCatcher exitCatcher;

    private volatile Integer userCounter = 0;

    /**
     * sender of game progress
     * @return
     */
    public String[] currentCondition() {
        ArrayList<String> strings = new ArrayList<>();
        synchronized (exitCatcher){
            for (ClientHandler clientHandler : clients) {
                strings.add(clientHandler.getName());
            }
        }
        return logicSender.conditionSender(strings);
    }
}
