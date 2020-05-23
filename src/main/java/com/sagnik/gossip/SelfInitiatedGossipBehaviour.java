package com.sagnik.gossip;

import com.sagnik.gossip.model.*;
import com.sagnik.network.CommunicationProtocol;
import com.sagnik.util.Util;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class SelfInitiatedGossipBehaviour {
    private final Logger logger = Logger.getLogger(SelfInitiatedGossipBehaviour.class);

    private NodeIdentifier self;
    private Random randomGenerator;
    private Set<NodeIdentifier> seeds;
    private CommunicationProtocol communicationProtocol;
    private TargetSelector<NodeIdentifier> targetSelector;
    private SocketProvider socketProvider;

    // todo self should be present within knownNodesWithMetadata
    public SelfInitiatedGossipBehaviour(Random randomGenerator, NodeIdentifier self, Set<NodeIdentifier> seeds,
                                        CommunicationProtocol communicationProtocol, TargetSelector<NodeIdentifier> targetSelector,
                                        SocketProvider socketProvider) {
        this.randomGenerator = randomGenerator;
        this.self = self;
        this.seeds = seeds;
        this.communicationProtocol = communicationProtocol;
        this.targetSelector = targetSelector;
        this.socketProvider = socketProvider;

        // todo handle multiple seeds
        // todo handle connection with other seeds if self is a seed
    }

    public void initiate(Map<NodeIdentifier, Metadata> knownNodesWithMetadata) {
        targetSelector.selectRandomTarget(randomGenerator, self, seeds).ifPresent(target -> {
            logger.info("Initiating first communication with seed: "+target);
            try {
                gossipWithNode(target, knownNodesWithMetadata);
            } catch (IOException e) {
                e.printStackTrace(); // todo
                throw new RuntimeException("Failed to initiate comm", e);
            }
        });
    }

    public void gossip(Map<NodeIdentifier, Metadata> knownNodesWithMetadata) {
        targetSelector.selectRandomTarget(randomGenerator, self, knownNodesWithMetadata.keySet())
                .ifPresent(target -> {
                    try {
                        gossipWithNode(target, knownNodesWithMetadata);
                    } catch (IOException e) {
                        e.printStackTrace(); // todo
                        throw new RuntimeException("Failed to connect during gossip", e);
                    }
                });
        // todo if target is unavailable
    }

    void gossipWithNode(NodeIdentifier targetNode, Map<NodeIdentifier, Metadata> knownNodesWithMetadata) throws IOException {
        // todo refactor socket out of here
        logger.debug("Initiating gossip with node: "+targetNode);

        Socket socket = socketProvider.createSocket(targetNode.host, targetNode.port);
        Packet packet;
        synchronized (knownNodesWithMetadata) {
            GossipDigestSyn gossipDigestSyn = new GossipDigestSyn(knownNodesWithMetadata.keySet());
            packet = new Packet(Packet.Key.GOSSIP_DIGEST_SYN, Util.serialize(gossipDigestSyn));
        }
        communicationProtocol.write(socket, Util.serialize(packet));
        logger.debug("About to read from socket");
        String resp = communicationProtocol.read(socket);
        logger.debug("Read from socket");
        packet = Util.deSerialize(resp, Packet.class);
        logger.debug("Deserialized packet");
        if (packet == null || packet.key != Packet.Key.GOSSIP_DIGEST_SYN_ACK || packet.serializedData == null) {
            logger.debug("Protocol error");
            System.err.println("Protocol error from GossipBehaviour"); //todo
            throw new RuntimeException("Protocol error");
        }

        logger.debug("Protocol ok");
        GossipDigestSynAck gossipDigestSynAck = null;
        try {
            gossipDigestSynAck = Util.deSerialize(packet.serializedData, GossipDigestSynAck.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        logger.debug("Deserialized gossipDigestSynAck");

        if (gossipDigestSynAck.knownNodesWithMetadata != null && gossipDigestSynAck.knownNodesWithMetadata.size() > 0) {
            synchronized (knownNodesWithMetadata) {
                knownNodesWithMetadata.putAll(gossipDigestSynAck.knownNodesWithMetadata);
                logger.info("Updated node data: "+knownNodesWithMetadata);
            }
        }

        logger.debug("Constructing gossipDigestSynAck2");

        Map<NodeIdentifier, Metadata> replyWithNodes;
        if (gossipDigestSynAck.unknownNodeIdentifiers != null && gossipDigestSynAck.unknownNodeIdentifiers.size() > 0) {
            synchronized (knownNodesWithMetadata) {
                replyWithNodes = gossipDigestSynAck.unknownNodeIdentifiers.parallelStream()
                        .collect(Collectors.toMap(node -> node, node -> knownNodesWithMetadata.get(node)));
            }
        } else {
            replyWithNodes = new HashMap<>(0);
        }

        GossipDigestSynAck2 gossipDigestSynAck2 = new GossipDigestSynAck2(replyWithNodes);
        packet = new Packet(Packet.Key.GOSSIP_DIGEST_SYN_ACK_2, Util.serialize(gossipDigestSynAck2));
        logger.debug("Writing to socket");
        communicationProtocol.write(socket, Util.serialize(packet));

        socket.close();
    }
}
