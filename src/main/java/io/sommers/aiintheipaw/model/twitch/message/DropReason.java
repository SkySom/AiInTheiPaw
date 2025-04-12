package io.sommers.aiintheipaw.model.twitch.message;

import jakarta.validation.constraints.NotBlank;

public class DropReason {
    @NotBlank
    private String code;
    @NotBlank
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
