package com.sagnik.network;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class ServerITTest {
    private CommunicationProtocol communicationProtocol = new CommunicationProtocol();
    @Test
    public void testComm() throws IOException {
//        ServerSocket serverSocket = new ServerSocket();
//        serverSocket.bind(new InetSocketAddress("127.0.0.1", 8081));
//        Server server1 = new Server(serverSocket, communicationProtocol, in -> "R: "+in);
//        new Thread(server1).start();
//
//        Socket socket = new Socket("127.0.0.1", 8081);
//        communicationProtocol.write(socket, "Hi");
//        assertEquals("R: Hi", communicationProtocol.read(socket));
    }
}