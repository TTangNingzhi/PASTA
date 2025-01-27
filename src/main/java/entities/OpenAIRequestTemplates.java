package entities;

import chat.ChatRequest;
import chat.Message;

import java.util.ArrayList;
import java.util.List;

public class OpenAIRequestTemplates {
    public static ChatRequest createSummaryRequest(String selectedCode, String fileContext) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", """
                You are a helpful assistant tasked with providing concise summaries for selected code snippets within a file.
                I will begin by presenting the entire file to establish context, followed by the specific code snippet for summarization.
                """));
        messages.add(new Message("user", """
                Below is the context of the file:
                                        
                %s
                                        
                Below is the selected code snippet:
                                        
                %s
                                        
                Please provide a concise summary of this snippet in one paragraph consisting of 2-3 sentences. Start the paragraph with a verb.
                """.formatted(fileContext, selectedCode)));
        return new ChatRequest("gpt-3.5-turbo", messages);
    }

    public static ChatRequest createGAMModificationRequest(String selectedCode, String fileContext, String originalSummary, String modifiedSummary) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", """
                As a helpful assistant, your task is to revise the selected code snippet to reflect the changes specified in its modified summary.
                Initially, I will present the entire file to set the context. Then, I will provide both the specific code snippet and its original summary,
                followed by the modified summary which indicates the desired changes.
                """));

        messages.add(new Message("user", """
                Below is the context of the entire file:
                                                
                %s
                                                
                Below is the selected code snippet:
                                                
                %s
                        
                Original summary of this snippet:
                        
                %s
                        
                Modified summary (note the changes):
                        
                %s
                                                
                Please revise the code snippet to reflect the changes outlined in the modified summary.
                Focus only on modifying the selected snippet—do not add any additional code.
                Begin your response with ``` and conclude with ```.
                """.formatted(fileContext, selectedCode, originalSummary, modifiedSummary)));
        return new ChatRequest("gpt-3.5-turbo", messages);
    }

    public static ChatRequest createBaseModificationRequest(String selectedCode, String fileContext, String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", """
                You are a helpful assistant tasked with modifying the selected code snippet based on the provided prompt.
                Initially, I will present the entire file to establish context. Then, I will provide the specific code snippet along with the modification prompt.
                """));
        messages.add(new Message("user", """
                Below is the context of the entire file:
                                                
                %s
                                                
                Below is the selected code snippet:
                                                
                %s
                                                
                Prompt for modification:
                                                
                %s
                                                
                Please modify the code snippet based on the provided prompt.
                Focus only on modifying the selected snippet—do not add any additional code.
                Begin your response with ``` and conclude with ```.
                """.formatted(fileContext, selectedCode, prompt)));
        return new ChatRequest("gpt-3.5-turbo", messages);
    }

}
