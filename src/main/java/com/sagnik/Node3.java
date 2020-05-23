package com.sagnik;

import com.sagnik.gossip.IncomingGossipHandler;
import com.sagnik.gossip.SelfInitiatedGossipBehaviour;
import com.sagnik.gossip.SocketProvider;
import com.sagnik.gossip.TargetSelector;
import com.sagnik.gossip.model.NodeIdentifier;
import com.sagnik.network.CommunicationProtocol;
import com.sagnik.network.Server;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Set;

public class Node3 {
    private static final Logger logger = Logger.getLogger(Node3.class);

    public static void main(String[] args) throws IOException {
        logger.info("Starting up");
        int bindPort = 8083;
        NodeIdentifier self = new NodeIdentifier("localhost", bindPort);
        Set<NodeIdentifier> seeds = Set.of(new NodeIdentifier("localhost", 8081));
        CommunicationProtocol communicationProtocol = new CommunicationProtocol();
        SelfInitiatedGossipBehaviour selfInitiatedGossipBehaviour = new SelfInitiatedGossipBehaviour(new Random(), self, seeds, communicationProtocol,
                new TargetSelector<>(), new SocketProvider());
        Server server = new Server(new ServerSocket(bindPort));
        IncomingGossipHandler incomingGossipHandler = new IncomingGossipHandler(communicationProtocol);
        Node node = new Node(self, selfInitiatedGossipBehaviour, server, communicationProtocol, incomingGossipHandler);
        node.run();
    }
}
