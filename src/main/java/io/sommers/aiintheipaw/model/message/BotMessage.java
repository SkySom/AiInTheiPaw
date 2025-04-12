package io.sommers.aiintheipaw.model.message;

public record BotMessage(
        String text
) implements IMessage {

    @Override
    public String getText() {
        return this.text();
    }
}
