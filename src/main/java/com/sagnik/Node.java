package com.sagnik;

import com.sagnik.gossip.SelfInitiatedGossipBehaviour;
import com.sagnik.gossip.IncomingGossipHandler;
import com.sagnik.gossip.model.GossipDigestSyn;
import com.sagnik.gossip.model.Metadata;
import com.sagnik.gossip.model.NodeIdentifier;
import com.sagnik.gossip.model.Packet;
import com.sagnik.network.CommunicationProtocol;
import com.sagnik.network.Server;
import com.sagnik.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Node implements Runnable {
    public static final int GOSSIP_INTERVAL = 1000;

    private SelfInitiatedGossipBehaviour selfInitiatedGossipBehaviour;
    private Server server;
    private NodeIdentifier self;
    private ScheduledExecutorService gossipExecutor;
    private Map<NodeIdentifier, Metadata> knownNodesWithMetadata;
    private CommunicationProtocol communicationProtocol;
    private IncomingGossipHandler incomingGossipHandler;

    public Node(NodeIdentifier self, SelfInitiatedGossipBehaviour selfInitiatedGossipBehaviour, Server server,
                CommunicationProtocol communicationProtocol, IncomingGossipHandler incomingGossipHandler) {
        this.selfInitiatedGossipBehaviour = selfInitiatedGossipBehaviour;
        this.gossipExecutor = Executors.newSingleThreadScheduledExecutor();
        this.self = self;
        this.server = server;
        this.communicationProtocol = communicationProtocol;
        this.incomingGossipHandler = incomingGossipHandler;
        this.knownNodesWithMetadata = new HashMap<>();
        this.knownNodesWithMetadata.put(self, new Metadata(UUID.randomUUID().getLeastSignificantBits()));
    }

    // todo move to a diff class
    private void gossip() {
        this.selfInitiatedGossipBehaviour.gossip(this.knownNodesWithMetadata);
        this.gossipExecutor.schedule(() -> this.gossip(), GOSSIP_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        // todo move to a diff class
        gossipExecutor.schedule(() -> {
            this.selfInitiatedGossipBehaviour.initiate(this.knownNodesWithMetadata);
            this.gossipExecutor.schedule(() -> this.gossip(), GOSSIP_INTERVAL, TimeUnit.MILLISECONDS);
        }, GOSSIP_INTERVAL, TimeUnit.MILLISECONDS);

        this.server.setHandler(socket -> {
            try {
                Packet packet = Util.deSerialize(communicationProtocol.read(socket), Packet.class);
                if (packet.key == Packet.Key.GOSSIP_DIGEST_SYN) {
                    this.incomingGossipHandler.handleIncomingGossip(socket,
                            Util.deSerialize(packet.serializedData, GossipDigestSyn.class),
                            knownNodesWithMetadata);
                } else {
                    // todo other types of msg handlers (like row updates)
                    System.err.println("Protocol error from Node"); // todo
                    throw new RuntimeException("Protocol Error");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace(); //todo
            }
        });

        this.server.run();
    }
}
