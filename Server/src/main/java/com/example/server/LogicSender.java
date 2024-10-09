package com.example.server;


import com.example.words.WordsReader;

import java.util.*;


/**
 * class for sending a logic of gameplay
 */
public class LogicSender {
    /**
     * getter for word length
     * @return - word length
     */
    public int getN() {
        return this.wordLength;
    }

    /**
     * instantation of word base
     * @param wordList - wordlist
     */
    public LogicSender(String wordList) {
        this.wordList = wordList;
    }


    /**
     * setter for word length
     * @param n
     */
    public void setWordLength(int n) {
        this.wordLength = n;
    }

    /**
     * method that sends progress to all players
     * @param players - list of results
     * @return - array
     */
    public synchronized String[] conditionSender(List<String> players) {
        String[] ans = new String['a'];
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < guess.size(); i++) {
            if (players.size() <= i) {
                break;
            }
            result.append(players.get(i)).append(String.format(" %s\n", String.copyValueOf(guess.get(i))));
            if(i == guess.size() - 1){
                ans =  result.toString().split("\n");
            }
        }
        return ans;
    }

    /**
     * method that choose a winner
     * @return - number of winner
     */
    public synchronized int winnerState() {
        if (isFinished()) {
            if (queueIndex == 0) {
                return results.size() - 1;
            } else {
                return queueIndex - 1;
            }
        }
        return -1;
    }

    /**
     * deleting a user due to disconnect or other issues
     * @param i - deleting user with index i
     */
    public synchronized void deleteUser(int i) {
        results.remove(i);
        guess.remove(i);
        if (queueIndex > i) {
            queueIndex--;
        }
    }

    /**
     * initializing of word
     */
    public synchronized void go() {
        chooseWord();
        for (int i = 0; i < results.size(); i++) {
            guess.set(i, "*".repeat(wordLength).toCharArray());
        }
    }

    /**
     * method that choose a next user to go
     * @return
     */
    public synchronized int getNextUserTurn() {
        queueIndex = (queueIndex + 1) % results.size();
        return queueIndex;
    }

    /**
     * method that is responsible for guessing a word of user
     * @param letter
     * @param place
     * @param player
     * @return
     */
    public synchronized int guess(String letter, int place, int player) {
        if (isFinished()) {
            return -1;
        }

        if (!guessingWord.contains(letter)) {
            return -1;
        } else if (guessingWord.charAt(place) == letter.charAt(0) && guess.get(player)[place] != '+') {
            results.set(player, results.get(player) + 1);
            if (results.get(player) == wordLength) {
                finished = true;
            }
            guess.get(player)[place] = '+';
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * adding a new player method
     */
    public synchronized void addUser() {
        results.add(0);
        guess.add(new char[]{});
    }

    /**
     * setter for guessing word
     * @param word
     */
    public void setGuessingWord(String word) {
        this.guessingWord = word;
        this.wordLength = word.length();
    }

    /**
     * choosing a word to guess
     */
    private void chooseWord(){
        if (guessingWord.isEmpty()) {
            Random gen = new Random();
            String[] wordsBase = WordsReader.readWords(wordLength, wordList);
            if (wordsBase.length < 1) {
                System.out.println("there are no words with your length");
            }
            int num = gen.nextInt(wordsBase.length);
            guessingWord = wordsBase[num];
            wordLength = guessingWord.length();
            System.out.println(guessingWord);
        }
    }

    /**
     * whether is game finished or not method
     * @return - is game finished
     */
    public synchronized boolean isFinished() {
        return finished;
    }


    private String guessingWord = "";

    private final ArrayList<char[]> guess = new ArrayList<>();

    private final ArrayList<Integer> results = new ArrayList<>();

    private int queueIndex = 0;

    private boolean finished = false;

    private int wordLength = 5;

    private final String wordList;
}
