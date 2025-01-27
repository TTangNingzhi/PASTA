package actions;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.NlsActions;
import logger.InteractionLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;

public class AcceptModifiedCodeAction extends BaseAction {
    public AcceptModifiedCodeAction(@Nullable @NlsActions.ActionText String text,
                                    @Nullable @NlsActions.ActionDescription String description,
                                    @Nullable Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(@NotNull AnActionEvent e) {
        assert e.getProject() != null;
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
        if (editor == null || getBase().getCodeDocument().getText().trim().equals("")) return;

        DiffContent originalCodeContent = DiffContentFactory.getInstance().createFragment(getBase().getProject(), editor.getDocument(), getBase().getSelectedRange());
        DiffContent modifiedCodeContent = DiffContentFactory.getInstance().createEditable(getBase().getProject(), getBase().getCodeDocument().getText(), editor.getVirtualFile().getFileType());

        InteractionLogger.log(new HashMap<>() {{
            put("event", "accept_modified_code");
            put("file_path", editor.getVirtualFile().getPath());
            put("original_code", getBase().getOriginalCode());
            put("modified_code", getBase().getCodeDocument().getText());
        }});

        SimpleDiffRequest diffRequest = new SimpleDiffRequest("Original Code Vs Modified Code",
                originalCodeContent, modifiedCodeContent, "Original code", "Modified code");
        ApplicationManager.getApplication().invokeLater(() -> DiffManager.getInstance().showDiff(e.getProject(), diffRequest));

        getBase().refresh();
    }
}
