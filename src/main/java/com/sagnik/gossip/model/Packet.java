package com.sagnik.gossip.model;

public class Packet {
    public enum Key {
        GOSSIP_DIGEST_SYN, GOSSIP_DIGEST_SYN_ACK, GOSSIP_DIGEST_SYN_ACK_2
    }

    public Key key;
    public String serializedData;

    public Packet() {
    }

    public Packet(Key key, String serializedData) {
        this.key = key;
        this.serializedData = serializedData;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "key=" + key +
                ", serializedData='" + serializedData + '\'' +
                '}';
    }
}
