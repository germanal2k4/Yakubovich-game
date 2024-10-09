package com.example.words;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class WordsReader {
    private WordsReader(){}

    public static String[] readWords(int n, String fileName) {
        ArrayList<String> words = new ArrayList<>();
        System.out.println(fileName);

        if (fileName.equals("russian_nouns.txt")) {
            InputStream inputStream = WordsReader.class.getClassLoader().getResourceAsStream(fileName.trim());
            if(inputStream != null) {
                try (Scanner scanner = new Scanner(inputStream)) {
                    while (scanner.hasNextLine()) {
                        String fileLine = scanner.nextLine();
                        String word = fileLine.trim();
                        if (word.length() == n) {
                            words.add(word);
                        }
                    }
                }
            }
        } else {
            try (InputStream inputStream = new FileInputStream(fileName)) {
                try (Scanner scanner = new Scanner(inputStream)) {
                    while (scanner.hasNextLine()) {
                        String fileLine = scanner.nextLine();
                        String word = fileLine.trim();
                        if (word.length() == n) {
                            words.add(word);
                        }
                    }
                }
            } catch (IOException e) {
                return new String[0];
            }
        }

        return words.toArray(new String[0]);
    }
}
