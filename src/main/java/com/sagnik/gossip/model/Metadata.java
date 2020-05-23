package com.sagnik.gossip.model;

public class Metadata {
    public Long token;

    public Metadata() {
    }

    public Metadata(Long token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "token=" + token +
                '}';
    }
}
