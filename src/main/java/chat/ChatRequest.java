package chat;

import com.google.gson.Gson;

import java.util.List;

public class ChatRequest {
    public String model;
    public List<Message> messages;
    public ResponseFormat responseFormat;

    public ChatRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public ChatRequest(String model, List<Message> messages, ResponseFormat responseFormat) {
        this(model, messages);
        this.responseFormat = responseFormat;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}