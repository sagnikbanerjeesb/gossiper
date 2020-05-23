package com.sagnik.network;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

public class CommunicationProtocolTest {
    private CommunicationProtocol communicationProtocol = new CommunicationProtocol();
    public static final String MSG = "Hi";

    @Test
    public void testWrite() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);

        communicationProtocol.write(socket, MSG);

        Mockito.verify(outputStream, atLeastOnce()).write(MSG.length() >>> 24 & 255);
        Mockito.verify(outputStream, atLeastOnce()).write(MSG.length() >>> 16 & 255);
        Mockito.verify(outputStream, atLeastOnce()).write(MSG.length() >>> 8 & 255);
        Mockito.verify(outputStream, atLeastOnce()).write(MSG.length() >>> 0 & 255);
        Mockito.verify(outputStream, atLeastOnce()).write(new byte[]{(byte) MSG.charAt(0), (byte) MSG.charAt(1)}, 0, 2);
    }

    @Test
    public void testRead() throws IOException {
        Socket socket = Mockito.mock(Socket.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                new byte[] {(byte) (MSG.length() >>> 24 & 255),
                        (byte) (MSG.length() >>> 16 & 255),
                        (byte) (MSG.length() >>> 8 & 255),
                        (byte) (MSG.length() >>> 0 & 255),
                        (byte) MSG.charAt(0),
                        (byte) MSG.charAt(1)});
        when(socket.getInputStream()).thenReturn(byteArrayInputStream);

        String resp = communicationProtocol.read(socket);

        assertEquals(MSG, resp);
    }
}