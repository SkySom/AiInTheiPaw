package io.sommers.aiintheipaw.model.sprint;

public enum SprintStatus {
    NEW("New"),
    SIGN_UP("Sign Up"),
    IN_PROGRESS("In Progress"),
    SUBMIT_COUNTS("Submit Counts"),
    COMPLETED("Completed"),
    PAUSED("Paused"),
    ;

    private final String text;

    SprintStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
