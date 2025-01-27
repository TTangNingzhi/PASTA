package actions;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class DiffBuggyCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        String buggyCode = """
                #include <iostream>
                                
                int main() {
                    std::cout << "Hello World";
                    return 0;
                }
                """;
        DiffContent currentCodeContent = DiffContentFactory.getInstance().create(psiFile.getText(), psiFile.getFileType());
        DiffContent buggyCodeContent = DiffContentFactory.getInstance().create(buggyCode, psiFile.getFileType());
        SimpleDiffRequest diffRequest = new SimpleDiffRequest("Current Code Vs Buggy Code", currentCodeContent, buggyCodeContent, "Current code", "Buggy code");
        ApplicationManager.getApplication().invokeLater(() -> DiffManager.getInstance().showDiff(e.getProject(), diffRequest));
    }
}
