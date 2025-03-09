package io.sommers.aiintheipaw.core.sprint;

import reactor.core.publisher.Mono;

public enum SprintStatus {
    SIGN_UP("Sign Up", true, false),
    IN_PROGRESS("In Progress", true, false),
    AWAITING_COUNTS("Awaiting Counts", false, true),
    COMPLETED("Completed", false, true);

    private final String name;
    private final boolean allowSignUp;
    private final boolean allowCounts;

    SprintStatus(String name, boolean allowSignUp, boolean allowCounts) {
        this.name = name;
        this.allowSignUp = allowSignUp;
        this.allowCounts = allowCounts;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowSignUp() {
        return allowSignUp;
    }

    public boolean isAllowCounts() {
        return allowCounts;
    }

    public static Mono<SprintStatus> fromString(String name) {
        for (SprintStatus status : SprintStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return Mono.just(status);
            }
        }

        return Mono.error(new IllegalArgumentException("Invalid sprint status: " + name));
    }
}
