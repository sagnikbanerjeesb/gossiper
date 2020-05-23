package com.sagnik.gossip.model;

import java.util.Set;

public class GossipDigestSyn {
    public Set<NodeIdentifier> knownNodeIdentifiers;

    public GossipDigestSyn() {
    }

    public GossipDigestSyn(Set<NodeIdentifier> knownNodeIdentifiers) {
        this.knownNodeIdentifiers = knownNodeIdentifiers;
    }

    @Override
    public String toString() {
        return "GossipDigestSyn{" +
                "knownNodes=" + knownNodeIdentifiers +
                '}';
    }
}
