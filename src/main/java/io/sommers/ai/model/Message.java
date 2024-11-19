package io.sommers.ai.model;

public record Message(
        String text
) implements IMessage {
    @Override
    public String getText() {
        return this.text();
    }
}
