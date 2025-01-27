package actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import entities.MySimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

public class ToggleGAMAction extends AnAction {
    private final MySimpleToolWindowPanel base = MySimpleToolWindowPanel.getInstance();

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(base.isGAM()
                ? "Switch to Baseline Mode"
                : "Switch to GAM Mode");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        base.toggleGAM();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}
