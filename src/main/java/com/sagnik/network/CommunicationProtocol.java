package com.sagnik.network;

import java.io.*;
import java.net.Socket;

public class CommunicationProtocol {
    public String read(Socket source) throws IOException {
        InputStream inputStream = source.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int len = dataInputStream.readInt();
        byte[] data = new byte[len];
        dataInputStream.read(data);
        return new String(data, "UTF-8");
    }

    public void write(Socket destination, String data) throws IOException {
        OutputStream outputStream = destination.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(data.length());
        dataOutputStream.write(data.getBytes("UTF-8"));
        dataOutputStream.flush();
        outputStream.flush();
    }
}
