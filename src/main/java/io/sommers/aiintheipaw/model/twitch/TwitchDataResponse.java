package io.sommers.aiintheipaw.model.twitch;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public record TwitchDataResponse<T>(
       List<T> data
) {

}
