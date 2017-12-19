package com.qci.pickem.model;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;

public enum ScoringMethod {
    ABSOLUTE(1, "Absolute Scoring"),
    SIXTEEN_DOWN(2, "Sixteen Down");

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
}
