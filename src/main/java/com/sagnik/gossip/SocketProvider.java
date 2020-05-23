package com.sagnik.gossip;

import java.io.IOException;
import java.net.Socket;

public class SocketProvider {
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}
