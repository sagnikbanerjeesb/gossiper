package com.sagnik.gossip;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class TargetSelector<T> {
    public Optional<T> selectRandomTarget(Random randomGenerator, T exclude, Set<T> choices) {
        int skipIndexes = 0;
        if (choices.contains(exclude)) {
            if (choices.size() > 2) {
                skipIndexes = randomGenerator.nextInt(choices.size() - 2);
            }
        } else {
            if (choices.size() > 1) {
                skipIndexes = randomGenerator.nextInt(choices.size() - 1);
            }
        }

        return choices.parallelStream().filter(knownNode -> !exclude.equals(knownNode)).skip(skipIndexes).findFirst();
    }
}
