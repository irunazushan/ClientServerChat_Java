package edu.school21.sockets.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonObject {
    private String message;
    private Long fromId;
    private Long roomId;

    public JsonObject() {};

    public JsonObject(String message, Long fromId, Long roomId) {
        this.message = message;
        this.fromId = fromId;
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "{" +
                "message: " + message +
                ", fromId: " + fromId +
                ", roomId: " + roomId +
                '}';
    }
}
