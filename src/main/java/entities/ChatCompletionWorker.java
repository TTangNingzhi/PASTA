package entities;

import chat.ChatCompletion;
import chat.ChatRequest;
import chat.OpenAIHttpPost;
import com.intellij.openapi.command.WriteCommandAction;
import logger.InteractionLogger;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ChatCompletionWorker extends SwingWorker<ChatCompletion, Void> {
    String id;
    private final OpenAIHttpPost openAIHttpPost;
    private final ChatRequest chatRequest;
    private final String jsonInputString;
    private final MySimpleToolWindowPanel base;
    private long startTime;

    public ChatCompletionWorker(String id, ChatRequest chatRequest, MySimpleToolWindowPanel base) throws Exception {
        this.id = id;
        this.openAIHttpPost = new OpenAIHttpPost();
        this.chatRequest = chatRequest;
        this.jsonInputString = chatRequest.toString();
        this.base = base;
    }

    @Override
    protected ChatCompletion doInBackground() throws Exception {
        startTime = System.currentTimeMillis();
        return openAIHttpPost.sendPostRequest(jsonInputString);
    }

    @Override
    protected void done() {
        long endTime = System.currentTimeMillis();
        ChatCompletion chatCompletion;
        try {
            chatCompletion = get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        printInfo(chatRequest, chatCompletion, endTime - startTime);
        switch (id) {
            case "summarize" -> {
                String summary = chatCompletion.choices.get(0).message.content;
                base.setOriginalSummary(summary);
                base.getProceduralTextPane().setText(summary);
                InteractionLogger.log(new HashMap<>() {{
                    put("event", "retrieve_summary");
                    put("file_path", base.getFilePath());
                    put("selected_code", base.getOriginalCode());
                    put("summary", summary);
                }});
            }
            case "procedural", "declarative" -> {
                String modifiedCode = chatCompletion.choices.get(0).message.content;
                String trimmedCode = modifiedCode.replace("```", "").trim();
                WriteCommandAction.runWriteCommandAction(base.getProject(), () -> {
                    base.getCodeDocument().setText(trimmedCode);
                });
                InteractionLogger.log(new HashMap<>() {{
                    if (id.equals("procedural")) {
                        put("event", "commit_procedural");
                        put("original_summary", base.getOriginalSummary());
                        put("revised_summary", base.getProceduralTextPane().getText());
                    } else {
                        put("event", "commit_declarative");
                        put("prompt", base.getDeclarativeTextPane().getText());
                    }
                    put("file_path", base.getFilePath());
                    put("selected_code", base.getOriginalCode());
                    put("modified_code", trimmedCode);
                }});
            }
            default -> throw new RuntimeException("Invalid id for ChatCompletionWorker: " + id);
        }
    }

    public static void printInfo(ChatRequest chatRequest, ChatCompletion chatCompletion, long duration) {
        System.out.println("\n====================================================================");
        System.out.println(chatRequest.messages.get(0).role);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(chatRequest.messages.get(0).content);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(chatRequest.messages.get(1).role);
        System.out.println("--------------------------------------------------------------------");
        System.out.println(chatRequest.messages.get(1).content);
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Time taken: " + duration + "ms");
        System.out.println("--------------------------------------------------------------------");
        System.out.println(chatCompletion.choices.get(0).message.content);
        System.out.println("====================================================================\n");
    }
}
