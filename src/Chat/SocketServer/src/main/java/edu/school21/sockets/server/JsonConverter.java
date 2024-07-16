package edu.school21.sockets.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {
    private ObjectMapper om;

    public JsonConverter() {
        om = new ObjectMapper();
    }

    public String convertMessageToJson(String message, Long fromId, Long roomId) {
        JsonObject jo = new JsonObject(message, fromId, roomId);
        String jsonFormat;
        try {
            jsonFormat = om.writeValueAsString(jo);
        } catch (JsonProcessingException e) {
            jsonFormat = null;
        }
        return jsonFormat;
    }

    public String sendMessageFromServer(String message) {
        return convertMessageToJson(message, 0L, 0L);
    }

    public String getMessageFromJson(String json) {
        if (json == null) {
            return null;
        }

        String message;
        try {
            JsonObject jo = om.readValue(json, JsonObject.class);
            message = jo.getMessage();
        } catch (JsonProcessingException e) {
            message = null;
        }
        return message;
    }
}
