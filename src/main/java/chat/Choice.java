package chat;

import com.google.gson.Gson;

public class Choice {
    public int index;
    public Message message;
    public Object logprobs;
    public String finishReason;

    public Choice(int index, Message message, Object logprobs, String finishReason) {
        this.index = index;
        this.message = message;
        this.logprobs = logprobs;
        this.finishReason = finishReason;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

}
