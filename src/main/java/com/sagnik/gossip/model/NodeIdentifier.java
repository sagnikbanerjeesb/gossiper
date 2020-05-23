package com.sagnik.gossip.model;

import java.util.Objects;

public class NodeIdentifier {
    public String host;
    public Integer port;

    public NodeIdentifier() {
    }

    public NodeIdentifier(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeIdentifier nodeIdentifier = (NodeIdentifier) o;
        return Objects.equals(host, nodeIdentifier.host) &&
                Objects.equals(port, nodeIdentifier.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "Node{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
