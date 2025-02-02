package io.sommers.aiintheipaw.core.util;

public record IntRange(
        int min,
        int max
) {

    public IntRange(int min, int max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    public boolean isInRange(int value) {
        return value >= min && value <= max;
    }

    public boolean isInRange(Number value) {
        return this.isInRange(value.intValue());
    }
}
