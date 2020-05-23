package com.sagnik.gossip.model;

import java.util.Map;

public class GossipDigestSynAck2 {
    public Map<NodeIdentifier, Metadata> knownNodesWithMetadata;

    public GossipDigestSynAck2() {
    }

    public GossipDigestSynAck2(Map<NodeIdentifier, Metadata> knownNodesWithMetadata) {
        this.knownNodesWithMetadata = knownNodesWithMetadata;
    }

    @Override
    public String toString() {
        return "GossipDigestSynAck2{" +
                "knownNodesWithMetadata=" + knownNodesWithMetadata +
                '}';
    }
}
