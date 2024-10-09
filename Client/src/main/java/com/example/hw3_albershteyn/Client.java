package com.example.hw3_albershteyn;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * class that is responsible for client catcher of signals that server sends
 */
public class Client extends Thread{
    /**
     * method that is required for blocking a button
     */
    public void changeFlag(){
        flag = !flag;
    }
    public Client(int port, String name, String host){
        this.map = new HashMap<>();
        this.port = port;
        this.name = name;
        this.host = host;
    }

    /**
     * game stopper
     */
    public synchronized void stopGame() {
        stop = true;
    }

    /**
     * is client successfully launched or not
     * @return - whether client launched or not
     */
    public boolean getSuccessfullylaunched(){
        return successfullylaunched;
    }

    /**
     * overriden method that cathes instructions from server and parse them
     */
    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {
            successfullylaunched = true;
            out.writeUTF(name);
            int id, waitTime, length;

            while (true) {
                synchronized (this) {
                    if (stop) {
                        break;
                    }
                }
                String recievedInfo = in.readUTF();

                System.out.println(recievedInfo);
                if (recievedInfo.contains("ID")) {
                    id = in.readInt();
                    length = in.readInt();
                    waitTime = in.readInt();

                    while (ClientWidget.getInstance() == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("something went wrong");
                        }
                    }
                    int finalId = id;
                    int finalWaitTime = waitTime;
                    Platform.runLater(() -> ClientWidget.getInstance().initParams(finalId, finalWaitTime));
                    int num = length;
                    Platform.runLater(() -> ClientWidget.getInstance().addPlayerMoveToLeft(name, "*".repeat(num)));
                    map.put(name, "*".repeat(num));
                    startFlag = true;
                    Platform.runLater(() -> ClientWidget.getInstance().setWordLabel("*".repeat(num)));
                } else if (recievedInfo.contains("conditions")) {
                    List<String> progress = readArrayString(in);
                    if(startFlag) {
                        synchronized (this){
                            if (stop) {
                                break;
                            }
                            Set<String> strings = new HashSet<>();
                            for (String player : progress) {
                                List<String> boxConstructor;
                                boxConstructor = List.of(player.split(" "));
                                strings.add(boxConstructor.get(0));
                                if (map.containsKey(boxConstructor.get(0).trim()) && !map.get(boxConstructor.get(0).trim()).equals(boxConstructor.get(1).trim())) {
                                    map.replace(boxConstructor.get(0).trim(), boxConstructor.get(1).trim());
                                    if(!boxConstructor.get(0).trim().equals(name)){
                                        Platform.runLater(() -> ClientWidget.getInstance().updateWord(boxConstructor.get(0).trim(), boxConstructor.get(1).trim()));
                                    } else{
                                        Platform.runLater(() -> ClientWidget.getInstance().updateCurrentWord(name));
                                    }
                                } else if(!map.containsKey(boxConstructor.get(0).trim())){
                                    map.put(boxConstructor.get(0).trim(), boxConstructor.get(1).trim());
                                    Platform.runLater(() -> ClientWidget.getInstance().addPlayerMoveToLeft(boxConstructor.get(0).trim(), boxConstructor.get(1).trim()));
                                }
                            }
                            Set<String> disc = new HashSet<>();
                            for (String namer: map.keySet()){
                                if(!strings.contains(namer)){
                                    disc.add(namer);
                                    Platform.runLater(() -> ClientWidget.getInstance().updateWord(namer, "disconnect"));
                                }
                            }
                            for(String namer: disc){
                                map.remove(namer);
                            }
                        }
                    }
                } else if (recievedInfo.contains("Time is up")) {
                    stopGame();
                    TimeExpiredWidget.launchWidget("Time is up!");
                    System.out.println("Time is up");
                    break;
                } else if (recievedInfo.contains("Game over")) {
                    stopGame();
                    String result = in.readUTF();
                    GameOverWidget.launchWidget("Game Over", result);
                    System.out.println(result);
                    break;
                } else if (recievedInfo.contains("guess")) {
                    while (true) {
                        synchronized (this) {
                            if (stop) {
                                return;
                            }
                        }
                        try {
                            boolean cur = flag;
                            Platform.runLater(() -> ClientWidget.getInstance().setEditableFields(true));
                            while (flag.equals(cur)){
                                if(stop){
                                    return;
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    System.out.println("something went wrong");
                                }
                            }
                            String s = ClientWidget.getInstance().getFieldString().trim();
                            int num = Integer.parseInt(ClientWidget.getInstance().getNumber().trim());
                            out.writeUTF(s);
                            out.writeInt(num - 1);
                            Platform.runLater(() -> ClientWidget.getInstance().setEditableFields(false));
                            int k = in.readInt();
                            System.out.println(k);
                            Platform.runLater(() -> {
                                ClientWidget.getInstance().addPlayerMoveToRight(s, num, k);
                            });
                            if(k == 1){
                                Platform.runLater(() -> {
                                    ClientWidget.getInstance().setCharacterAtPosition(num - 1, s);
                                });
                            }
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println("Unknown command");
                        }
                    }
                } else if(recievedInfo.contains("Alive")){
                    if(stop) break;
                    out.writeUTF("Nice!");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * getter for flag
     */
    public boolean getFlag(){
        return flag;
    }
    /**
     * getter for stop
     * @return - whtehter game is stopped ot not
     */
    public boolean getStop(){
        return stop;
    }
    /**
     * reading a whole array of strings
     * @param in - inputstream
     * @return - list for parsing
     * @throws IOException
     */
    private List<String> readArrayString(DataInputStream in) throws IOException {
        int len = in.readInt();
        System.out.println(len);
        List<String> array = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            array.add(in.readUTF());
            System.out.println(array.getLast());
        }
        return array;
    }

    private final String host;

    private final int port;
    private boolean successfullylaunched = false;
    private final String name;
    private final Map<String, String> map;
    private volatile Boolean stop = false;
    private Boolean flag = false;
    private Boolean startFlag = false;

}
