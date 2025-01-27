package chat;

import com.google.gson.Gson;

public class Message {
    public String role;
    public String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
