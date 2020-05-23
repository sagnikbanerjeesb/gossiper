package com.sagnik.gossip;

import com.sagnik.gossip.model.*;
import com.sagnik.network.CommunicationProtocol;
import com.sagnik.util.Util;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IncomingGossipHandler {
    private final Logger logger = Logger.getLogger(IncomingGossipHandler.class);

    private CommunicationProtocol communicationProtocol;

    public IncomingGossipHandler(CommunicationProtocol communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    public void handleIncomingGossip(Socket socket, GossipDigestSyn gossipDigestSyn,
                                     Map<NodeIdentifier, Metadata> knownNodesWithMetadata) throws IOException {
        logger.debug("Handling incoming gossip request from ["+socket.getInetAddress().getHostAddress()+":"+socket.getPort()+"]");

        Set<NodeIdentifier> unknownNodes;
        Map<NodeIdentifier, Metadata> knownNodes;
        synchronized (knownNodesWithMetadata) {
            unknownNodes = gossipDigestSyn.knownNodeIdentifiers.parallelStream()
                    .filter(incomingKnownNode -> !knownNodesWithMetadata.containsKey(incomingKnownNode))
                    .collect(Collectors.toSet());

            knownNodes = knownNodesWithMetadata.keySet().parallelStream()
                    .filter(knownNode -> !gossipDigestSyn.knownNodeIdentifiers.contains(knownNode))
                    .collect(Collectors.toMap(node -> node, node -> knownNodesWithMetadata.get(node)));
        }

        GossipDigestSynAck gossipDigestSynAck = new GossipDigestSynAck(knownNodes, unknownNodes);
        Packet packet = new Packet(Packet.Key.GOSSIP_DIGEST_SYN_ACK, Util.serialize(gossipDigestSynAck));
        logger.debug("Writing to socket");
        communicationProtocol.write(socket, Util.serialize(packet));
        logger.debug("Reading from socket");
        packet = Util.deSerialize(communicationProtocol.read(socket), Packet.class);
        logger.debug("Read from socket");
        if (packet.key != Packet.Key.GOSSIP_DIGEST_SYN_ACK_2) {
            throw new RuntimeException("Protocol Error");
        }

        GossipDigestSynAck2 gossipDigestSynAck2 = Util.deSerialize(packet.serializedData, GossipDigestSynAck2.class);
        if (gossipDigestSynAck2.knownNodesWithMetadata != null && gossipDigestSynAck2.knownNodesWithMetadata.size() > 0) {
            synchronized (knownNodesWithMetadata) {
                knownNodesWithMetadata.putAll(gossipDigestSynAck2.knownNodesWithMetadata);
                logger.info("Updated nodes data via incoming gossip: "+knownNodesWithMetadata);
            }
        }

        logger.debug("Done handling incoming gossip");
        socket.close();
    }
}
