package com.sagnik.gossip;

import com.sagnik.gossip.model.NodeIdentifier;
import org.junit.Test;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TargetSelectorTest {
    private NodeIdentifier self = new NodeIdentifier("localhost", 8081);
    private TargetSelector<NodeIdentifier> targetSelector = new TargetSelector<>();

    @Test
    public void targetSelectionShouldReturnEmptyWhenOnlySelfIsProvidedInSetOfChoices() {
        Random randomGen = mock(Random.class);

        Optional<NodeIdentifier> target = targetSelector.selectRandomTarget(randomGen, self, Set.of(self));

        assertTrue(target.isEmpty());
    }

    @Test
    public void targetSelectionShouldReturnOnlyOneNodeIfItsTheOnlyChoiceAndIsNotSelf() {
        Random randomGen = mock(Random.class);
        NodeIdentifier otherNode = new NodeIdentifier("localhost", 8082);

        Optional<NodeIdentifier> target = targetSelector.selectRandomTarget(randomGen, self, Set.of(otherNode));

        assertTrue(target.isPresent());
        assertEquals(otherNode, target.get());
    }

    @Test
    public void targetSelectionShouldReturnOnlyOtherNodeIfSelfAndAnotherNodeIsProvided() {
        Random randomGen = mock(Random.class);
        NodeIdentifier otherNode = new NodeIdentifier("localhost", 8082);

        Optional<NodeIdentifier> target = targetSelector.selectRandomTarget(randomGen, self, Set.of(self, otherNode));

        assertTrue(target.isPresent());
        assertEquals(otherNode, target.get());
    }

    @Test
    public void targetSelectionShouldReturnOneOfTwoOtherNodes() {
        Random randomGen = new Random();
        NodeIdentifier otherNode = new NodeIdentifier("localhost", 8082);
        NodeIdentifier otherNode2 = new NodeIdentifier("localhost", 8083);

        Optional<NodeIdentifier> target = targetSelector.selectRandomTarget(randomGen, self, Set.of(self, otherNode, otherNode2));

        assertTrue(target.isPresent());
        assertTrue(otherNode.equals(target.get()) || otherNode2.equals(target.get()));
    }
}