package io.sommers.ai.command.sprint;

public enum SprintStatus {
    SIGN_UP("Sign Up", true, false),
    IN_PROGRESS("In Progress", true, false),
    WAITING_COUNTS("Waiting Counts", false, true),
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
}
