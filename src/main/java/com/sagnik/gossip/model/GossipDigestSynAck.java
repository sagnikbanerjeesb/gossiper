package com.sagnik.gossip.model;

import java.util.Map;
import java.util.Set;

public class GossipDigestSynAck {
    public Map<NodeIdentifier, Metadata> knownNodesWithMetadata;
    public Set<NodeIdentifier> unknownNodeIdentifiers;

    public GossipDigestSynAck() {
    }

    public GossipDigestSynAck(Map<NodeIdentifier, Metadata> knownNodesWithMetadata, Set<NodeIdentifier> unknownNodeIdentifiers) {
        this.knownNodesWithMetadata = knownNodesWithMetadata;
        this.unknownNodeIdentifiers = unknownNodeIdentifiers;
    }

    @Override
    public String toString() {
        return "GossipDigestSynAck{" +
                "knownNodesWithMetadata=" + knownNodesWithMetadata +
                ", unknownNodes=" + unknownNodeIdentifiers +
                '}';
    }
}
