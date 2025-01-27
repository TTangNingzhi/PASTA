package chat;

import com.google.gson.Gson;

import java.util.List;

public class ChatCompletion {
    public String id;
    public String object;
    public long created;
    public String model;
    public List<Choice> choices;
    public Usage usage;
    public String systemFingerprint;

    public ChatCompletion(String id, String object, long created, String model, List<Choice> choices, Usage usage, String systemFingerprint) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
        this.systemFingerprint = systemFingerprint;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}


