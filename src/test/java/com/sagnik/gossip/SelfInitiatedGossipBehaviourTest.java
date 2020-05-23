package com.sagnik.gossip;

import com.sagnik.gossip.model.*;
import com.sagnik.network.CommunicationProtocol;
import com.sagnik.util.Util;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class SelfInitiatedGossipBehaviourTest {
    private NodeIdentifier self = new NodeIdentifier("localhost", 8081);

    @Test
    public void testGossipWithSingleNode() throws IOException {
        NodeIdentifier otherNode = new NodeIdentifier("localhost", 8082);
        Random randomGen = mock(Random.class);
        Socket socket = mock(Socket.class);
        CommunicationProtocol communicationProtocol = mock(CommunicationProtocol.class);
        TargetSelector<NodeIdentifier> targetSelector = mock(TargetSelector.class);
        SocketProvider socketProvider = mock(SocketProvider.class);
        when(socketProvider.createSocket(any(), anyInt())).thenReturn(socket);
        when(targetSelector.selectRandomTarget(any(), any(), any())).thenReturn(Optional.of(otherNode));

        when(communicationProtocol.read(any())).thenReturn(Util.serialize(new Packet(Packet.Key.GOSSIP_DIGEST_SYN_ACK, Util.serialize(new GossipDigestSynAck(null, Set.of(self))))));

        new SelfInitiatedGossipBehaviour(randomGen, self,
                Set.of(otherNode), communicationProtocol, targetSelector, socketProvider).initiate(Map.of(self, new Metadata(2L)));

        verify(socketProvider, atLeastOnce()).createSocket(eq("localhost"), eq(8082));
        verify(communicationProtocol, atLeastOnce()).write(eq(socket), eq((Util.serialize(new Packet(Packet.Key.GOSSIP_DIGEST_SYN, Util.serialize(new GossipDigestSyn(Set.of(self))))))));
        verify(communicationProtocol, atLeastOnce()).write(eq(socket), eq((Util.serialize(new Packet(Packet.Key.GOSSIP_DIGEST_SYN_ACK_2, Util.serialize(new GossipDigestSynAck2(Map.of(self, new Metadata(2L)))))))));
    }
}