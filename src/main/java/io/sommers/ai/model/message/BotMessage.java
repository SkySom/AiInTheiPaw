package io.sommers.ai.model.message;

public record BotMessage(
        String text
) implements IMessage {
    @Override
    public String getText() {
        return this.text();
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
