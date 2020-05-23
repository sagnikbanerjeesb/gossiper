package com.sagnik.network;

import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server implements Runnable {
    private final Logger logger = Logger.getLogger(Server.class);

    private ServerSocket serverSocket;
    private Executor executor;

    private Consumer<Socket> handler;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.executor = Executors.newFixedThreadPool(10); // todo refactor
    }

    public void setHandler(Consumer<Socket> handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            logger.info("Accepting connections on port " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                this.executor.execute(() -> handler.accept(socket));

                this.executor.execute(() -> {

                });
            }
        } catch (Exception e) {
            // todo
        }
    }
}
