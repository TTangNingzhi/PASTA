package chat;

import com.google.gson.Gson;

public class Usage {
    public int promptTokens;
    public int completionTokens;
    public int totalTokens;

    public Usage(int promptTokens, int completionTokens, int totalTokens) {
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}