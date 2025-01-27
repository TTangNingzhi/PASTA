package chat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAIHttpPost {

    private final URL url;
    private final String openAIAPIKey;
    private final String contentType;

    public OpenAIHttpPost(String url, String openAIAPIKey, String contentType) throws Exception {
        this.url = new URL(url);
        this.openAIAPIKey = openAIAPIKey;
        this.contentType = contentType;
    }

    public OpenAIHttpPost() throws Exception {
        this("https://api.openai.com/v1/chat/completions", System.getenv("OPENAI_API_KEY"), "application/json");
    }

    public ChatCompletion sendPostRequest(String jsonInputString) throws Exception {
        HttpURLConnection connection = createConnection();
        sendRequestData(connection, jsonInputString);
        return getResponse(connection);
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Authorization", "Bearer " + openAIAPIKey);
        connection.setDoOutput(true);
        return connection;
    }

    private void sendRequestData(HttpURLConnection connection, String data) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    private ChatCompletion getResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
        }
        return new Gson().fromJson(response.toString(), ChatCompletion.class);
    }
}
