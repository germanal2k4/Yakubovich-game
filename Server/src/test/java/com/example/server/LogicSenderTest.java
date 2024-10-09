package com.example.server;

import com.example.server.LogicSender;
import com.example.words.WordsReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogicSenderTest {
    private LogicSender logicSender;
    private final String wordList = "exampleWordList";

    @BeforeEach
    public void setUp() {
        logicSender = new LogicSender(wordList);
    }

    @Test
    public void testSetAndGetWordLength() {
        logicSender.setWordLength(7);
        assertEquals(7, logicSender.getN());
    }

    @Test
    public void testConditionSender() {
        List<String> players = List.of("Player1", "Player2");
        logicSender.addUser();
        logicSender.addUser();
        logicSender.setGuessingWord("test");

        String[] conditions = logicSender.conditionSender(players);

        assertNotNull(conditions);
        assertEquals(2, conditions.length);
        assertTrue(conditions[0].contains("Player1"));
        assertTrue(conditions[1].contains("Player2"));
    }

    @Test
    public void testWinnerState() {
        logicSender.addUser();
        logicSender.setGuessingWord("test");
        logicSender.go();

        int winner = logicSender.winnerState();
        assertEquals(-1, winner);

        logicSender.guess("t", 0, 0);
        logicSender.guess("e", 1, 0);
        logicSender.guess("s", 2, 0);
        logicSender.guess("t", 3, 0);

        winner = logicSender.winnerState();
        assertEquals(0, winner);
    }



    @Test
    public void testGo() {
        logicSender.addUser();
        logicSender.setGuessingWord("test");
        logicSender.go();

        assertFalse(logicSender.isFinished());
        assertEquals(4, logicSender.getN());
    }

    @Test
    public void testGetNextUserTurn() {
        logicSender.addUser();
        logicSender.addUser();
        int nextUser = logicSender.getNextUserTurn();
        assertEquals(1, nextUser);
    }
}

