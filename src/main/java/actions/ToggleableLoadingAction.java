package actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnimatedIcon;
import org.jetbrains.annotations.NotNull;

public class ToggleableLoadingAction extends AnAction {
    private boolean isLoading = false;

    public ToggleableLoadingAction() {
        super("", "", AllIcons.General.InspectionsOK);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        if (isLoading) {
            presentation.setIcon(new AnimatedIcon.Default());
        } else {
            presentation.setIcon(AllIcons.General.InspectionsOK);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
