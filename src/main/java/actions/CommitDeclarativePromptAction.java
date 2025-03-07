package actions;

import chat.ChatRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.TextRange;
import entities.ChatCompletionWorker;
import entities.OpenAIRequestTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CommitDeclarativePromptAction extends BaseAction {
    public CommitDeclarativePromptAction(@Nullable @NlsActions.ActionText String text,
                                         @Nullable @NlsActions.ActionDescription String description,
                                         @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        assert e.getProject() != null;
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if (editor == null) return;
        getBase().setEditor(editor);
        String fileContext = editor.getDocument().getText();
        String selectedCode = editor.getSelectionModel().getSelectedText();
        String prompt = getBase().getDeclarativeTextPane().getText();
        TextRange selectedRange = new TextRange(editor.getSelectionModel().getSelectionStart(), editor.getSelectionModel().getSelectionEnd());
        getBase().setOriginalCode(selectedCode);
        getBase().setSelectedRange(selectedRange);
        getBase().setFilePath(editor.getVirtualFile().getPath());
        ChatRequest chatRequest = OpenAIRequestTemplates.createDeclarativeModificationRequest(selectedCode, fileContext, prompt);
        getBase().getLoadingAction().setLoading(true);
        try {
            ChatCompletionWorker worker = new ChatCompletionWorker("declarative", chatRequest, getBase());
            worker.execute();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
