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

public class RetrieveSummaryAction extends BaseAction {

    public RetrieveSummaryAction(@Nullable @NlsActions.ActionText String text,
                                 @Nullable @NlsActions.ActionDescription String description,
                                 @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        assert e.getProject() != null;
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if (editor == null) return;

        getBase().refresh();

        String fileContext = editor.getDocument().getText();
        String selectedCode = editor.getSelectionModel().getSelectedText();
        if (selectedCode == null || selectedCode.trim().equals("")) {
            getBase().getProceduralTextPane().setText("Please select some code to summarize.");
            return;
        }
        TextRange selectedRange = new TextRange(editor.getSelectionModel().getSelectionStart(), editor.getSelectionModel().getSelectionEnd());
        getBase().setOriginalCode(selectedCode);
        getBase().setSelectedRange(selectedRange);
        getBase().setFilePath(editor.getVirtualFile().getPath());

        ChatRequest chatRequest = OpenAIRequestTemplates.createSummaryRequest(selectedCode, fileContext);
        getBase().getLoadingAction().setLoading(true);
        try {
            ChatCompletionWorker worker = new ChatCompletionWorker("summarize", chatRequest, getBase());
            worker.execute();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
