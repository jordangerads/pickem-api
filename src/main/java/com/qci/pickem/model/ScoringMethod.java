package com.qci.pickem.model;

import com.google.common.collect.ImmutableMap;

import java.util.*;

public enum ScoringMethod {
    ABSOLUTE(1, "Absolute Scoring") {
        @Override
        public boolean areConfidencesValid(List<Integer> confidences) {
            // Absolute scoring should just be 1 for everything.
            Set<Integer> uniqueConfidences = new HashSet<>(confidences);

            if (uniqueConfidences.size() != 1) {
                // The only acceptable case if there's not just value is 1s and nulls.
                return uniqueConfidences.size() == 2 && uniqueConfidences.contains(1) && uniqueConfidences.contains(null);
            }

            // There's only one element, it should be one.
            return uniqueConfidences.contains(1);
        }
    },
    SIXTEEN_DOWN(2, "Sixteen Down") {
        @Override
        public boolean areConfidencesValid(List<Integer> confidences) {
            List<Integer> modifiable = new ArrayList<>(confidences);

            modifiable.removeIf(Objects::isNull);

            boolean valuesValid = true;
            for (Integer confidence : modifiable) {
                if (confidence > 16) {
                    // Values can only go as high as 16 (for now!)
                    valuesValid = false;
                }
            }

            return valuesValid && new HashSet<>(modifiable).size() == modifiable.size();
        }
    };

    private final int id;
    private final String name;

    private static final Map<Integer, ScoringMethod> SCORING_METHODS_BY_ID;

    static {
        ImmutableMap.Builder<Integer, ScoringMethod> builder = ImmutableMap.builder();

        Arrays.stream(values()).forEach(method -> builder.put(method.id, method));

        SCORING_METHODS_BY_ID = builder.build();
    }

    ScoringMethod(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ScoringMethod getScoringMethodById(Integer id) {
        return id != null ? SCORING_METHODS_BY_ID.get(id) : null;
    }

    public abstract boolean areConfidencesValid(List<Integer> confidences);
}
