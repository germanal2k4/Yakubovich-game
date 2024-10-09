package com.example.server;

import com.example.server.GameSession;
import com.example.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerTest {
    private Server server;
    private GameSession gameSessionMock;
    private ServerSocket serverSocketMock;
    private Socket socketMock;

    @BeforeEach
    public void setUp() {
        int port = 12345;
        int m = 2;
        int tp = 60;
        int ts = 5;
        int tb = 10;
        int tn = 7;
        String wordList = "exampleWordList";

        server = new Server(port, m, tp, ts, tb, tn, wordList);
        gameSessionMock = mock(GameSession.class);
        serverSocketMock = mock(ServerSocket.class);
        socketMock = mock(Socket.class);
    }

    @Test
    public void testSetN() {
        server.setN(5);
        Assertions.assertEquals(5, server.n);
    }

    @Test
    public void testSetWord() {
        String newWord = "testWord";
        server.setWord(newWord);
        Assertions.assertEquals(newWord, server.word);
        Assertions.assertEquals(0, server.n);
    }

    @Test
    public void testIsStarted() {
        Assertions.assertFalse(server.isStarted());
    }

    @Test
    public void testKillGame() throws IOException {
        server.sessions.add(gameSessionMock);
        doNothing().when(gameSessionMock).killSession();

        server.setServerSocket(serverSocketMock);
        doNothing().when(serverSocketMock).close();

        server.killGame();

        verify(gameSessionMock, times(1)).killSession();
        verify(serverSocketMock, times(1)).close();
    }
}

