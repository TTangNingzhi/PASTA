package actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction;
import entities.MySimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

public class ModeLabelAction extends ToolbarLabelAction {
    private final MySimpleToolWindowPanel base;

    public ModeLabelAction(MySimpleToolWindowPanel base) {
        super();
        this.base = base;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(base.isGAM() ? "GAM Mode" : "Baseline Mode");
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
