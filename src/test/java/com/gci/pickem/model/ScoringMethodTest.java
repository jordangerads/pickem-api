package com.gci.pickem.model;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoringMethodTest {

    @Test
    public void testAbsoluteMethodScoring() {
        // Happy path.
        Assert.assertTrue(ScoringMethod.ABSOLUTE.areConfidencesValid(Collections.nCopies(16, 1)));

        // Empty pick, still should be good,
        Assert.assertTrue(ScoringMethod.ABSOLUTE.areConfidencesValid(Lists.newArrayList(1, 1, null, 1)));

        // Add in a bad one.
        Assert.assertFalse(ScoringMethod.ABSOLUTE.areConfidencesValid(Lists.newArrayList(1, 2, null, 1)));
    }

    @Test
    public void testSixteenDownMethodScoring() {
        // Happy path.
        Assert.assertTrue(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(getValidSixteenDownPicks(16)));

        // Could be fewer games than 16.
        Assert.assertTrue(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(getValidSixteenDownPicks(13)));
    }

    @Test
    public void testSixteenDownMethodScoringWithNulls() {
        List<Integer> confidences = getValidSixteenDownPicks(13);
        confidences.addAll(Collections.nCopies(3, null));

        Assert.assertTrue(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(confidences));
    }

    @Test
    public void testSixteenDownMethodScoringWithGapInValues() {
        // User might accidentally pick 1 as their low value when it should only be 4. Still valid, just bad.
        List<Integer> confidences = getValidSixteenDownPicks(13);
        confidences.add(1);

        Assert.assertTrue(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(confidences));
    }

    @Test
    public void testSixteenDownMethodScoringWithInvalidPicks() {
        Assert.assertFalse(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(Collections.nCopies(16, 1)));

        List<Integer> confidences = getValidSixteenDownPicks(13);

        // This isn't okay!
        confidences.add(20);

        Assert.assertFalse(ScoringMethod.SIXTEEN_DOWN.areConfidencesValid(confidences));
    }
    private List<Integer> getValidSixteenDownPicks(int nGames) {
        List<Integer> confidences = new ArrayList<>();

        for (int i = 0; i < nGames; i++) {
            confidences.add(16 - i);
        }

        return confidences;
    }
}
