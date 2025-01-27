package actions;

import chat.ChatRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.NlsActions;
import entities.ChatCompletionWorker;
import entities.OpenAIRequestTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CommitModifiedSummaryAction extends BaseAction {
    public CommitModifiedSummaryAction(@Nullable @NlsActions.ActionText String text,
                                       @Nullable @NlsActions.ActionDescription String description,
                                       @Nullable Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        assert e.getProject() != null;
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if (editor == null) return;
        String fileContext = editor.getDocument().getText();
        String selectedCode = getBase().getOriginalCode();
        String originalSummary = getBase().getOriginalSummary();
        String modifiedSummary = getBase().getTextPane().getText();
        ChatRequest chatRequest = OpenAIRequestTemplates.createGAMModificationRequest(selectedCode, fileContext, originalSummary, modifiedSummary);
        try {
            ChatCompletionWorker worker = new ChatCompletionWorker("gam", chatRequest, getBase());
            worker.execute();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
